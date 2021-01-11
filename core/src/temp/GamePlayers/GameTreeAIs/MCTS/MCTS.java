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
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.RoundState;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.MemoryPlayer;
import temp.GameRules;

import java.util.*;

//TODO explore different exploration function for MCTS
//TODO explore different ways of evaluating a rollout
//TODO MCTS build to allow customizing MCTS at runtime (for testing what hyper param are best)
public abstract class MCTS extends MemoryPlayer{

    protected static final int baseRolloutsPerSim = 500;
    protected static final int baseRolloutsPerNode = 2;
    protected static final double baseExplorationParam = 1.4;

    public boolean debugmcts = false;
    public boolean print = false;

    protected final int rolloutsPerNode; // Should be =1 unless you rollout at least somewhat randomly
    protected final int rolloutsPerSimulation; // Higher = deeper search. For stopping condition
    protected final double explorationParam;

    protected final Random rd; // For seeding
    protected boolean simpleKnocking = true;
    protected int rollouts;

    public MCTS(int rolloutsPerSimulation, int rolloutsPerNode,double explorationParam , Integer seed){
        if(seed==null){
            rd = new Random();
        }
        else{
            rd = new Random(seed);
        }
        this.rolloutsPerSimulation = rolloutsPerSimulation;
        this.rolloutsPerNode = rolloutsPerNode;
        this.explorationParam =explorationParam;
    }
    public MCTS(Integer seed){
        this(baseRolloutsPerSim, baseRolloutsPerNode, baseExplorationParam, seed);
    }
    public MCTS(){
        this(null);
    }

    //Interface methods. Methods that get called by game itself.
    @Override
    public Boolean KnockOrContinue() {
        if(simpleKnocking){
            if (this.handLayout.deadwoodValue() <= 10){
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
        if(print){
            System.out.println("MCTS Pick");
        }
        PickAction action = (PickAction) getBestAction(Step.Pick);
        return action==null? null : action.deck;
    }
    @Override
    public MyCard DiscardCard() {
        if(print){
            System.out.println("MCTS Discard");
        }
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
        RoundState state = new RoundState(cardsMemory, new Turn(step, index));
        MCTSNode root = ExpandNode(new MCTSNode(null, null, this), state);

        monteCarloTreeSearch(root, state.cards());

        int best = findBestAction(root.children);
        if(print) {
            System.out.println(cardsMemory);
            System.out.println("Moves:");
            print(root, best);
        }
        return root.children.get(best).action;
    }

    // Move generation

    /**
     * Gets all possible moves given current knowledge (pretty much state).
     *
     * @param state current state of the game
     * @return list of possible moves
     */
    protected MCTSNode ExpandNode(MCTSNode parent, RoundState state){
        List<Action> moves = null;
        if(state.turn().step == Step.Pick){
            moves = getPickMoves(state);
        }
        else if(state.turn().step == Step.Discard){
            moves = getDiscardMoves(state);
        }
        else if(state.turn().step == Step.KnockOrContinue){
            moves = getKnockMoves(state);
        }
        if(moves!=null) {
            for (Action move : moves) {
                parent.children.add(new MCTSNode(parent, move, this));
            }
        }
        return parent;
    }
    private List<Action> getPickMoves(RoundState state){
        List<Action> pick = new ArrayList<>();
        if(state.deck().size()!=0){
            pick.add(new PickAction(state.getPlayerIndex(), true, state.deck().get(state.deck().size()-1)));
        }
        else{
            pick.add(new PickAction(state.getPlayerIndex(), true, null));
        }
        if(state.discardPile().size()!=0){
            pick.add(new PickAction(state.getPlayerIndex(), false, state.discardPile().peek()));
        }
        return pick;
    }
    private List<Action> getDiscardMoves(RoundState state){
        List<Action> discard = new ArrayList<>();
        if(state.getPlayerIndex()==index){
            for (MyCard card : state.cards(state.getPlayerIndex())) {
                discard.add(new DiscardAction(state.getPlayerIndex(), card));
            }
        }
        else{
            for (MyCard card : state.cards(state.getPlayerIndex())) {
                discard.add(new DiscardAction(state.getPlayerIndex(), card));
            }
            for (MyCard card : state.unassigned()) {
                discard.add(new DiscardAction(state.getPlayerIndex(), card));
            }
        }
        return discard;
    }
    private List<Action> getKnockMoves(RoundState state){
        List<Action> knock = new ArrayList<>();
        //TODO what to do when other player's turn and not perfect information
        if(state.getPlayerIndex()== index || state.hasPerfectInformation()) {
            HandLayout handLayout = Finder.findBestHandLayout(state.cards(state.getPlayerIndex()));
            if (handLayout.deadwoodValue() <= GameRules.minDeadwoodToKnock) {
                knock.add(new KnockAction(state.getPlayerIndex(), true, handLayout));
            }
        }
        knock.add(new KnockAction(state.getPlayerIndex(), false, null));
        return knock;
    }

    // Intermediate

    /**
     * For now, for MCTSV1 to generate random game states and apply MCTS to every game state generated.
     *
     * @param root node with all possible moves as children
     */
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge) {
        mcts(root,new RoundState(knowledge, new Turn(step, index)));
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
        return executeRollout(new ForcePlayer(rd.nextInt()), new ForcePlayer(rd.nextInt()),state);
    }

    // Main Monte Carlo Tree Simulation

    /**
     * Executes the MCTS on given knowledge.
     *
     * @param root containing all possible moves as children
     * @param state imagined state of game
     */
    protected void mcts(MCTSNode root, RoundState state){
        assert state.numberOfPlayers()==Game.numberOfPlayers(this);
        rollouts = 0;
        while(!stopCondition()) {
            if(debugmcts) {
                System.out.println("\nLoop " + (rollouts/rolloutsPerNode)+", rollouts "+rollouts);
            }
            RoundState s = new RoundState(state);
            // Explore
            MCTSNode node = explore(root, s);
            if(debugmcts) {
                System.out.println("\nExplored to "+node);
            }
            // Expand
            ExpandNode(node, s);
            if(debugmcts){
                System.out.println("\nExpanded:");
                print(node, null);
            }
            // Simulate
            for (int i = 0; i < rolloutsPerNode; i++) {
                node.wins += rollout(new RoundState(s));
                node.rollouts++;
                rollouts++;
                if(stopCondition()){
                    break;
                }
            }
            if(debugmcts) {
                System.out.println("\nRolled out "+rolloutsPerNode+" times:\n"+node);
            }
            // Back propagate
            node.backPropagate();
            if(debugmcts) {
                System.out.println("\nBack propagated");
            }
        }
    }
    /**
     * When to stop MCTS.
     * Can be based on time, memory, number of rollouts, depth, etc...
     *
     * @return true if stop, false if not
     */
    protected boolean stopCondition(){
        return rollouts>= rolloutsPerSimulation;
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
        while (!node.isLeaf()){
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
        g.remove();
        return getRoundValue(result);
    }
    /**
     * Finds index of best action.
     *
     * @param actions list to be searched
     * @return index of best action
     */
    protected int findBestAction(List<MCTSNode> actions){
        int best=0;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < actions.size(); i++) {
            if(max<=actions.get(i).rollouts+actions.get(i).value()){
                max = actions.get(i).rollouts+actions.get(i).value();
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
        CardsInfo c = new CardsInfo(knowledge);
        Game.shuffleList(rd, 500, c.unassigned);
        for (int i = 0; i < c.players.size(); i++) {
            while(c.players.get(i).size()<GameRules.baseCardsPerHand){
                // TODO: Add a way to modify how the decks are generated and store the probability of the resulting hand
                // Here we can modify this to pick cards based of set probabilities
                c.players.get(i).add(c.unassigned.remove(0));
            }
        }
        // TODO: Modify this to add the size of the deck, to limit the depth of the tree and probably add a speed increase
        // Should probably be a class held variable to be fair
        c.deck.addAll(c.unassigned);
        c.unassigned.clear();
        return c;
    }
    protected double getRoundValue(Result result){
        if(result.winner==null){
            return 0.5;
        }
        return result.winner==index? 1 : 0;
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