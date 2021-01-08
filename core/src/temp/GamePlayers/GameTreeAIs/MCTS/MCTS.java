package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.Extra.PostGameInformation.Result;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.Turn;
import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.RoundState;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.MemoryPlayer;
import temp.GameRules;

import java.util.*;
//TODO fix mouse player
//TODO fix backpropagation
//TODO evaluation function in HandLayout
//TODO what to do when other player's turn and not perfect information: knocking
//TODO do MCTSv2
//TODO remove HandLayout from knocker and integrate layout confirm+layoff steps in logic
public abstract class MCTS extends MemoryPlayer{
    public boolean debugmcts = false;
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
        KnockAction action = (KnockAction) getBestAction(Step.KnockOrContinue);
        return action==null? null : action.knock;
    }
    @Override
    public Boolean PickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        PickAction action = (PickAction) getBestAction(Step.Pick);
        return action==null? null : action.deck;
    }
    @Override
    public MyCard DiscardCard() {
        DiscardAction action = (DiscardAction) getBestAction(Step.Discard);
        return action==null? null : action.card;
    }

    /**
     * Returns best action found through MCTS.
     *
     * @param step current step
     * @return best action
     */
    protected Action getBestAction(Step step){
        if(this.step!=step){
            throw new AssertionError("Memory player step inconsistencies");
        }
        rollouts = 0;
        RoundState state = new RoundState(unpackMemory(), new Turn(step, 0));
        MCTSNode root = ExpandNode(new MCTSNode(null, null), state);
        monteCarloTreeSearch(root, state.cards());

        int best = findBestAction(root.children);
        return root.children.get(best).action;
    }

    // Move generation

    /**
     * Gets all possible moves given current knowledge (pretty much state).
     *
     * @param state current state of the game
     * @return list of possible moves
     */
    protected static MCTSNode ExpandNode(MCTSNode parent, RoundState state){
        if(state.turn().step == Step.Pick){
            getPickMoves(parent, state);
        }
        else if(state.turn().step == Step.Discard){
            getDiscardMoves(parent, state);
        }
        else if(state.turn().step == Step.KnockOrContinue){
            getKnockMoves(parent, state);
        }
        return parent;
    }
    private static void getPickMoves(MCTSNode parent, RoundState state){
        if(state.deck().size()!=0){
            parent.children.add(new MCTSNode(parent,new PickAction(state.getPlayerIndex(), true, state.deck().get(state.deck().size()-1))));
        }
        else{
            for (int i = 0; i < state.unassigned().size(); i++) {
                parent.children.add(new MCTSNode(parent,new PickAction(state.getPlayerIndex(), true, state.unassigned().get(i))));
            }
        }
        if(state.discardPile().size()!=0){
            parent.children.add(new MCTSNode(parent, new PickAction(state.getPlayerIndex(), false, state.discardPile().peek())));
        }
    }
    private static void getDiscardMoves(MCTSNode parent, RoundState state){
        if(state.getPlayerIndex()==0){
            for (MyCard card : state.getCards(state.getPlayerIndex())) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(state.getPlayerIndex(), card)));
            }
        }
        else{
            for (MyCard card : state.getCards(state.getPlayerIndex())) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(state.getPlayerIndex(), card)));
            }
            for (MyCard card : state.unassigned()) {
                parent.children.add(new MCTSNode(parent, new DiscardAction(state.getPlayerIndex(), card)));
            }
        }
    }
    private static void getKnockMoves(MCTSNode parent, RoundState state){
        //TODO what to do when other player's turn and not perfect information
        if(state.getPlayerIndex()==0 || state.hasPerfectInformation()) {
            HandLayout handLayout = new HandLayout(state.getCards(state.getPlayerIndex()));
            if (handLayout.getDeadwood() <= GameRules.minDeadwoodToKnock) {
                parent.children.add(new MCTSNode(parent, new KnockAction(state.getPlayerIndex(), true, handLayout)));
            }
        }
        parent.children.add(new MCTSNode(parent, new KnockAction(state.getPlayerIndex(), false, null)));
    }

    // Intermediate

    /**
     * For now, for MCTSV1 to generate random game states and apply MCTS to every game state generated.
     *
     * @param root node with all possible moves as children
     */
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge) {
        mcts(root,new RoundState(knowledge, new Turn(step, 0)));
    }
    /**
     * For now, for MCTSV2 to generate random hands at the end of the exploration for the enemy
     *
     * GamePlayers: ForcePlayer, Basic Greedy, Meld Greedy, RandomPlayer? Need fast players.
     *
     * @param state that needs to be played out
     * @return the value of that the state end up getting us after rollout
     */
    protected double rollout(RoundState state){
        return executeRollout(new ForcePlayer(), new ForcePlayer(),state);
    }

    // Main Monte Carlo Tree Simulation

    /**
     * Executes the MCTS on given knowledge.
     *
     * @param root containing all possible moves as children
     * @param state imagined state of game
     */
    protected void mcts(MCTSNode root, RoundState state){
        rollouts = 0;
        while(!stopCondition()) {
            if(debugmcts) {
                System.out.println("\n\nIteration: " + (rollouts/rolloutsPerNode)+" rollouts: "+rollouts);
            }
            // Explore
            MCTSNode node = explore(root, state);
            if(debugmcts) {
                System.out.println("\nExplored to "+node);
                System.out.println(state);
            }
            // Expand
            ExpandNode(node, state);
            if(debugmcts){
                System.out.println("\nExpanded:");
                print(node, null);
            }
            // Simulate
            for (int i = 0; i < rolloutsPerNode; i++) {
                node.wins += rollout(new RoundState(state));
                node.rollouts++;
                rollouts++;
            }
            if(debugmcts) {
                System.out.println("\nRolled out "+node);
            }
            // Back propagate
            backPropagate(node);
            if(debugmcts) {
                System.out.println("\nBack propagated");
            }
            // Go back to init state
            node = undoExploration(node, state);
            if(debugmcts){
                System.out.println("\nUndid exploration");
                System.out.println(state.turn());
                print(node, null);
            }
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
    private MCTSNode explore(MCTSNode node, RoundState state){
        while (!node.isLeaf()) {
            averageOutDeckPicks(node);
            node = node.getChildToExplore(rollouts);
            node.action.doAction(state,true);
        }
        return node;
    }
    /**
     * Executes rollout based on current state
     *
     * @param player1 player used to simulate yourself
     * @param player2 player used to simulate other
     * @param state state to start at
     * @return true if you win, false if other wins
     */
    private double executeRollout(GamePlayer player1, GamePlayer player2, RoundState state) {
        Game g= new Game(Arrays.asList(player1, player2), state, rd.nextInt());
        Result result = g.playOutRound();
        return getRoundValue(result);
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
    private MCTSNode undoExploration(MCTSNode node, RoundState state){
        while (!node.isRoot()) {
            if(!node.action.same(state.getLastAction())){
                System.out.println("NOOO");
                throw new AssertionError("WEWE");
            }
            node.action.undoAction(state);
            node = node.parent;
        }
        return node;
    }
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


    // Other

    /**
     * Generates a random world given the known information
     * Fills the rest of the enemy's hand and adds the rest of the unknown cards to the deck.
     *
     * @param knowledge known information
     * @return new (different objects) perfect information world
     */
    protected CardsInfo completeUnknownInformation(CardsInfo knowledge){
        List<List<MyCard>> players = new ArrayList<>();
        players.add(new ArrayList<>(knowledge.getCards(0)));
        players.add(new ArrayList<>(knowledge.getCards(1)));
        List<MyCard> unassigned = new ArrayList<>(knowledge.unassigned);
        Stack<MyCard> deck = new Stack<>();
        Stack<MyCard> discard = (Stack<MyCard>) knowledge.discardPile.clone();

        if(players.get(1).size()+unassigned.size()<GameRules.baseCardsPerHand) throw new AssertionError("Can't fill a hand I known information.");

        while(players.get(1).size()<GameRules.baseCardsPerHand){
            // TODO: Add a way to modify how the decks are generated and store the probability of the resulting hand
            // Here we can modify this to pick cards based of set probabilities
            players.get(1).add(unassigned.remove(rd.nextInt(unassigned.size()-1)));
        }
        // TODO: Modify this to add the size of the deck, to limit the depth of the tree and probably add a speed increase
        // Should probably be a class held variable to be fair
        deck.addAll(unassigned);
        deck.addAll(knowledge.deck);
        unassigned = new ArrayList<>();
        Game.shuffleList(rd, 500, deck);
        return new CardsInfo(players, deck, unassigned, discard);
    }
    protected double getRoundValue(Result result){
        if(result.winner==null){
            return 0.5;
        }
        return 1-result.winner;
    }
    /**
     * Used to avoid MCTS from exploring the one card it needs that could be at the top of the deck,
     * but isn't necessarily.
     * Rolls to nearest integer.
     *
     * @param node node that needs to be averaged out
     */
    protected void averageOutDeckPicks(MCTSNode node){
        if(node.isLeaf() || node.children.get(0).action.getStep()!= Step.Pick){
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