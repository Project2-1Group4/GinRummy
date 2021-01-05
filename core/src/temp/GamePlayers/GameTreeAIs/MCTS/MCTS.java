package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MemoryPlayer;
import temp.GameRules;

import java.util.List;
import java.util.Random;

public abstract class MCTS extends MemoryPlayer{

    public static final double explorationParam = 1.2;
    protected final int rolloutsPerNode = 1; // Should be =1 unless you rollout at least somewhat randomly
    protected final int maximumAmountOfRollouts = 1000; // For stopping condition
    protected final Random rd; // For seeding

    protected int rollouts = 0;

    public MCTS(int seed){
        rd = new Random(seed);
    }
    public MCTS(){
        rd = new Random();
    }

    @Override
    public Boolean knockOrContinue() {
        KnockAction action = (KnockAction) getBestAction(State.StepInTurn.KnockOrContinue);
        return action==null? null : action.knock;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        PickAction action = (PickAction) getBestAction(State.StepInTurn.Pick);
        return action==null? null : action.deck;
    }

    @Override
    public MyCard discardCard() {
        DiscardAction action = (DiscardAction) getBestAction(State.StepInTurn.Discard);
        return action==null? null : action.card;
    }

    /**
     * Executes the MCTS given a perfect information state.
     *
     * @param root containing all possible moves as children
     * @param state imagined state of game
     */
    protected void monteCarloTreeSearch(MCTSNode root, Knowledge state){
        while(!stopCondition()){
            MCTSNode n = root;
            while(n.children.isEmpty()){
                n = n.getChildToExplore(rollouts);
                state.execute(n.action);
                if(state.step== State.StepInTurn.Pick && n.children.size()>2){
                    averageOutDeckPicks(n);
                }
            }
            n.children.addAll(getPossibleMoves(state).children);
            rollOut(state);
            while(n!=root){
                state.undo(n.action);
                n = n.parent;
            }
        }
    }

    /**
     * Gets all possible moves given current knowledge (pretty much state).
     *
     * @param knowledge current knowledge of the game
     * @return list of possible moves
     */
    protected MCTSNode getPossibleMoves(Knowledge knowledge){
        MCTSNode root = new MCTSNode(null, null);
        if(knowledge.step == State.StepInTurn.Pick){
            getPickMoves(root, knowledge);
        }
        else if(knowledge.step == State.StepInTurn.Discard){
            getDiscardMoves(root, knowledge);
        }
        else if(knowledge.step == State.StepInTurn.KnockOrContinue){
            getKnockMoves(root, knowledge);
        }
        return root;
    }

    private void getPickMoves(MCTSNode root, Knowledge knowledge){
        if(knowledge.deck!=null){
            root.children.add(new MCTSNode(root,new PickAction(knowledge.turn, true, knowledge.deck.get(knowledge.deck.size()-1))));
        }
        else{
            for (int i = 0; i < knowledge.unknown.size(); i++) {
                root.children.add(new MCTSNode(root,new PickAction(knowledge.turn, true, knowledge.unknown.get(i))));
            }
        }
        if(knowledge.discard.size()!=0){
            root.children.add(new MCTSNode(root, new PickAction(knowledge.turn, false, knowledge.discard.peek())));
        }
    }

    private void getDiscardMoves(MCTSNode root, Knowledge knowledge){
        if(knowledge.turn == 0){
            for (MyCard card : knowledge.player) {
                root.children.add(new MCTSNode(root, new DiscardAction(knowledge.turn, card)));
            }
        }
        else{
            if(knowledge.deck!=null) {
                for (MyCard card : knowledge.otherPlayer) {
                    root.children.add(new MCTSNode(root, new DiscardAction(knowledge.turn, card)));
                }
            }
            else{
                for (MyCard card : knowledge.otherPlayer) {
                    root.children.add(new MCTSNode(root, new DiscardAction(knowledge.turn, card)));
                }
                for (MyCard card : knowledge.unknown) {
                    root.children.add(new MCTSNode(root, new DiscardAction(knowledge.turn, card)));
                }
            }
        }
    }

    private void getKnockMoves(MCTSNode root, Knowledge knowledge){
        //TODO what to do when other player's turn?
        if(knowledge.turn==0 || knowledge.deck!=null) {
            HandLayout handLayout = new HandLayout(knowledge.turn==0? knowledge.player : knowledge.otherPlayer);
            if (handLayout.getDeadwood() <= GameRules.minDeadwoodToKnock) {
                root.children.add(new MCTSNode(root, new KnockAction(knowledge.turn, true, handLayout)));
            }
        }
        root.children.add(new MCTSNode(root, new KnockAction(knowledge.turn, false, handLayout)));
    }

    /**
     * Used to avoid MCTS from exploring the one card it needs that could be at the top of the deck,
     * but isn't necessarily
     *
     * @param node node that needs to be averaged out
     */
    private void averageOutDeckPicks(MCTSNode node){
        assert node.action instanceof PickAction;
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
                child.rollouts = rollouts / child.children.size();
                child.wins = wins / child.children.size();
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
        return rollouts<=maximumAmountOfRollouts;
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
            if(max<=actions.get(i).value()){
                max = actions.get(i).value();
                best = i;
            }
        }
        return best;
    }

    /**
     * Returns best action found through MCTS.
     *
     * @param step current step
     * @return best action
     */
    protected abstract Action getBestAction(State.StepInTurn step);

    /**
     * Does rollout part of MCTS (play till end).
     * Can be done either randomly or with a some other algorithm (BasicGreedy or ForcePlayer?).
     *
     * @param state that needs to be played out
     * @return true if this player wins, false if other wins
     */
    protected abstract boolean rollOut(Knowledge state);

    /**
     * Helper method. Prints. To be deleted.
     *
     * @param actions list of actions that can be done now
     * @param chosen index of action that has been chosen
     */
    protected void print(List<MCTSNode> actions, int chosen){
        System.out.println("Chose "+chosen+" out of:\n");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println("\t"+i+". "+actions.get(i).action);
        }
    }
}