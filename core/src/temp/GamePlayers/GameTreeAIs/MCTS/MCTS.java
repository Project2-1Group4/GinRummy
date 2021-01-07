package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.MemoryPlayer;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class MCTS extends MemoryPlayer{

    public static final double explorationParam = 1.2;
    protected final int rolloutsPerNode = 1; // Should be =1 unless you rollout at least somewhat randomly
    protected final int maximumAmountOfRollouts = 100; // For stopping condition
    protected final Random rd; // For seeding

    protected boolean simpleKnocking = true;

    protected int rollouts;

    public MCTS(int seed){
        rd = new Random(seed);
    }
    public MCTS(){
        rd = new Random();
    }

    //Interface methods. Methods that get called by game itself.

    @Override
    public Boolean KnockOrContinue() {

        if(simpleKnocking){
            if (this.handLayout.getDeadwood() <= 10){
                return true;
            } else {
                return false;
            }
        }

        KnockAction action = (KnockAction) getBestAction(State.StepInTurn.KnockOrContinue);
        return action==null? null : action.knock;
    }

    @Override
    public Boolean PickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        PickAction action = (PickAction) getBestAction(State.StepInTurn.Pick);
        return action==null? null : action.deck;
    }

    @Override
    public MyCard DiscardCard() {
        DiscardAction action = (DiscardAction) getBestAction(State.StepInTurn.Discard);
        return action==null? null : action.card;
    }



    //Initial. Called every time it's your turn to get all possible moves from current state.



    /**
     * Returns best action found through MCTS.
     *
     * @param step current step
     * @return best action
     */
    protected Action getBestAction(State.StepInTurn step){
        rollouts = 0;
        KnowledgeBase knowledge = unpackMemory();
        assert knowledge.step == step;
        knowledge.step = step;
        knowledge.turn = 0;
        MCTSNode root = getPossibleMoves(new MCTSNode(null, null), knowledge);
        monteCarloTreeSearch(root, knowledge);

        int best = findBestAction(root.children);
        return root.children.get(best).action;
    }

    /**
     * Gets all possible moves given current knowledge (pretty much state).
     *
     * @param knowledge current knowledge of the game
     * @return list of possible moves
     */
    protected MCTSNode getPossibleMoves(MCTSNode parent, KnowledgeBase knowledge){
        if(knowledge.step == State.StepInTurn.Pick){
            getPickMoves(parent, knowledge);
        }
        else if(knowledge.step == State.StepInTurn.Discard){
            getDiscardMoves(parent, knowledge);
        }
        else if(knowledge.step == State.StepInTurn.KnockOrContinue){
            getKnockMoves(parent, knowledge);
        }
        return parent;
    }

    private void getPickMoves(MCTSNode parent, KnowledgeBase knowledge){
        if(knowledge.deck.size()!=0){
            parent.children.add(new MCTSNode(parent,new PickAction(knowledge.turn, true, knowledge.deck.get(knowledge.deck.size()-1))));
        }
        else{
            for (int i = 0; i < knowledge.unknown.size(); i++) {
                parent.children.add(new MCTSNode(parent,new PickAction(knowledge.turn, true, knowledge.unknown.get(i))));
            }
        }
        if(knowledge.discardPile.size()!=0){
            parent.children.add(new MCTSNode(parent, new PickAction(knowledge.turn, false, knowledge.discardPile.peek())));
        }
    }

    private void getDiscardMoves(MCTSNode parent, KnowledgeBase knowledge){
        if(knowledge.turn == 0){
            for (MyCard card : knowledge.player) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(knowledge.turn, card)));
            }
        }
        else{
            for (MyCard card : knowledge.otherPlayer) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(knowledge.turn, card)));
            }
            for (MyCard card : knowledge.unknown) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(knowledge.turn, card)));
            }
        }
    }

    private void getKnockMoves(MCTSNode parent, KnowledgeBase knowledge){
        //TODO what to do when other player's turn?
        if(knowledge.turn==0 || knowledge.unknown.size()==0) {
            HandLayout handLayout = new HandLayout(knowledge.turn==0? knowledge.player : knowledge.otherPlayer);
            if (handLayout.getDeadwood() <= GameRules.minDeadwoodToKnock) {
                parent.children.add(new MCTSNode(parent, new KnockAction(knowledge.turn, true, handLayout)));
            }
        }
        parent.children.add(new MCTSNode(parent, new KnockAction(knowledge.turn, false, handLayout)));
    }



    //Overridable methods



    /**
     * Overridable method.
     * For now, for MCTSV1 to generate random game states and apply MCTS to every game state generated.
     *
     * @param root node with all possible moves as children
     */
    protected void monteCarloTreeSearch(MCTSNode root, KnowledgeBase knowledge) {
        mcts(root,knowledge);
    }

    /**
     * Overridable method.
     * For now, for MCTSV2 to generate random hands at the end of the exploration for the enemy
     *
     * GamePlayers: ForcePlayer, Basic Greedy, Meld Greedy, RandomPlayer? Need fast players.
     *
     * @param state that needs to be played out
     */
    protected void rollout(MCTSNode node, KnowledgeBase state){
        executeRollouts(node, new ForcePlayer(), new ForcePlayer(),state, rd.nextInt());

        //executeRollouts(node, new basicGreedyTest(), new basicGreedyTest(),state, rd.nextInt());
    }



    //Main Monte Carlo Tree Simulation



    /**
     * Executes the MCTS on given knowledge.
     *
     * @param root containing all possible moves as children
     * @param state imagined state of game
     */
    protected void mcts(MCTSNode root, KnowledgeBase state){
        rollouts = 0;
        while(!stopCondition()) {
            // Explore
            MCTSNode node = explore(root, state);
            // Simulate
            for (int i = 0; i < rolloutsPerNode; i++) {
                rollout(node, state);
            }
            // Back propagate
            backPropagate(node);
            // Expand
            node = getPossibleMoves(node, state);
            // Go back to init state
            node = undoExploration(node, state);
            assert node == root;
        }
    }

    /**
     * When to stop MCTS.
     * Can be based on time, memory, number of rollouts, depth, etc...
     *
     * @return true if stop, false if not
     */
    protected boolean stopCondition(){
        return rollouts>=maximumAmountOfRollouts;
    }

    /**
     * Explores current tree based on exploration value up until it reaches a leaf node,
     * and averages out the value deck picks if the next move is a pick action that has been expanded
     * to give every deck possibility the same value.
     *
     * @param node start node for exploration (usually root)
     * @param state knowledge base at given start node
     * @return leaf node favored by exploration
     */
    private MCTSNode explore(MCTSNode node, KnowledgeBase state){
        while (!node.isLeaf()) {
            averageOutDeckPicks(node);
            node = node.getChildToExplore(rollouts);
            state.execute(node.action);
        }
        return node;
    }

    /**
     * Executes rollouts based on current state and saves results.
     *
     * @param node node you need to rollout
     * @param player1 player used to simulate yourself
     * @param player2 player used to simulate other
     * @param state state to start at
     * @param seed to allow replication
     */
    protected void executeRollouts(MCTSNode node, GamePlayer player1, GamePlayer player2, KnowledgeBase state, Integer seed){
        for (int i = 0; i < rolloutsPerNode; i++) {
            node.wins += executeRollout(player1, player2,state, seed);
            node.rollouts++;
            rollouts++;
        }
    }

    /**
     * Executes rollout based on current state
     *
     * @param player1 player used to simulate yourself
     * @param player2 player used to simulate other
     * @param knowledge state to start at
     * @param seed to allow replication
     * @return true if you win, false if other wins
     */
    private double executeRollout(GamePlayer player1, GamePlayer player2, KnowledgeBase knowledge, Integer seed) {
        GameLogic g = new GameLogic(true, false);
        State curState = knowledge.toState(player1, player2, seed);

        while (!curState.endOfGame()) {
            curState = g.update(curState);
        }

        // TODO: Modify result add the end so that it adds points to whatever gave a bigger point total
        // That way we give more value to stuff that won by a bigger margin

        double win;
        if(curState.roundWinnerIndex==null){
            win = 0.5; // TIE
        }
        else if(curState.roundWinnerIndex==0){
            win = 1; // I WIN
        }
        else{
            win = 0; // I LOSE
        }
        return win;
    }

    /**
     * Back propagate results of rollout.
     *
     * @param node that has been rolled out
     */
    protected static void backPropagate(MCTSNode node){
        double leafRollouts = node.rollouts;
        double leafWins = node.wins;
        while(node.parent!=null){
            MCTSNode temp = node.parent;
            temp.rollouts+= leafRollouts;
            temp.wins+= leafWins;
            node = temp;
        }
    }

    /**
     * Undoes all moves made to get to node (back to root node)
     *
     * @param node final node expanded
     * @param state knowledge base at the end of the expansion
     * @return root node
     */
    private MCTSNode undoExploration(MCTSNode node, KnowledgeBase state){
        while (!node.isRoot()) {
            state.undo(node.action);
            node = node.parent;
        }
        return node;
    }



    // Additional Methods



    /**
     * Finds index of best action.
     *
     * @param actions list to be searched
     * @return index of best action
     */
    protected int findBestAction(List<MCTSNode> actions){
        int best=0;
        double max = Double.MAX_VALUE;
        for (int i = 0; i < actions.size(); i++) {
            if(max<=actions.get(i).rollouts){
                max = actions.get(i).rollouts;
                best = i;
            }
        }
        return best;
    }

    /**
     * Generates a random world given the known information
     * Fills the rest of the enemy's hand and adds the rest of the unknown cards to the deck.
     *
     * @param knowledge known information
     * @return new (different objects) perfect information world
     */
    protected KnowledgeBase generateRandomWorld(KnowledgeBase knowledge){
        List<MyCard> player = new ArrayList<>(knowledge.player);
        List<MyCard> other = new ArrayList<>(knowledge.otherPlayer);
        List<MyCard> unknown = new ArrayList<>(knowledge.unknown);
        List<MyCard> deck = new ArrayList<>(knowledge.deck);
        Stack<MyCard> discard = (Stack<MyCard>) knowledge.discardPile.clone();

        while(other.size()<GameRules.baseCardsPerHand){
            // TODO: Add a way to modify how the decks are generated and store the probability of the resulting hand
            // Here we can modify this to pick cards based of set probabilities

            // This is to fix an issue that happens when most of the cards are already known
            // Just makes it so that it will never get an array out of bounds exception
            if(unknown.size()>1){
                other.add(unknown.remove(rd.nextInt(unknown.size()-1)));
            } else {
                other.add(unknown.remove(0));
            }


        }

        // TODO: Modify this to add the size of the deck, to limit the depth of the tree and probably add a speed increase
        // Should probably be a class held variable to be fair
        deck.addAll(unknown);
        unknown = new ArrayList<>();
        Executor.shuffleList(rd, 500, deck);
        return new KnowledgeBase(knowledge.step, knowledge.turn, player, other, deck, unknown, discard);
    }

    /**
     * Used to avoid MCTS from exploring the one card it needs that could be at the top of the deck,
     * but isn't necessarily.
     * Rolls to nearest integer.
     *
     * @param node node that needs to be averaged out
     */
    protected void averageOutDeckPicks(MCTSNode node){
        if(node.isLeaf() || node.children.get(0).action.getStep()!= State.StepInTurn.Pick){
            return;
        }
        int rollouts = 0;
        int wins =0;
        for (MCTSNode child : node.children) {
            if(((PickAction)child.action).deck) {
                rollouts += child.rollouts;
                wins += child.wins;
            }
        }
        for (MCTSNode child : node.children) {
            if(((PickAction)child.action).deck) {
                child.rollouts = rollouts /(double) node.children.size();
                child.wins = wins /(double) node.children.size();
            }
        }
    }

    /**
     * Helper method. Prints. To be deleted.
     *
     * @param root root you want to print
     * @param chosen index of action that has been chosen
     */
    protected void print(MCTSNode root, Integer chosen){
        System.out.println("Root "+root+"\nChose "+chosen+" out of:");
        for (int i = 0; i < root.children.size(); i++) {
            System.out.println("\t"+i+". "+root.children.get(i));
        }
    }
}