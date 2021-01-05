package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MemoryPlayer;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MCTS extends MemoryPlayer{

    public static final double explorationParam = 1.2;
    private final int simulations = 100;
    private final int rolloutsPerNode = 1;
    private final int maximumAmountOfRollouts = 1000;
    private final Random rd;

    private int rollouts = 0;

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
     * Returns best action found through MCTS.
     *
     * @param step current step
     * @return best action
     */
    private Action getBestAction(State.StepInTurn step){
        rollouts = 0;
        MCTSNode root = new MCTSNode(null);
        root.children.addAll(getRoots(step));
        List<MiniState> states = generateStates();
        for (MiniState miniState : states) {
            monteCarloTreeSearch(root, miniState);
        }
        int best = findBestAction(root.children);
        print(root.children,best);
        return root.children.get(best).action;
    }

    /**
     * Gets all possible moves given current knowledge (pretty much state).
     *
     * @param step current step
     * @return list of possible moves
     */
    private List<MCTSNode> getRoots(State.StepInTurn step){
        List<MCTSNode> roots = new ArrayList<>();
        if(step == State.StepInTurn.Pick){
            roots.add(new MCTSNode(new PickAction(index, true, null)));
            if(discardMemory.size()!=0){
                roots.add(new MCTSNode(new PickAction(index, false, discardMemory.peek())));
            }
        }
        else if(step == State.StepInTurn.Discard){
            for (MyCard card : handLayout.viewAllCards()) {
                roots.add(new MCTSNode(new DiscardAction(index, card)));
            }
        }
        else if(step == State.StepInTurn.KnockOrContinue){
            if(handLayout.getDeadwood()<= GameRules.minDeadwoodToKnock){
                roots.add(new MCTSNode(new KnockAction(index, true, handLayout)));
            }
            roots.add(new MCTSNode(new KnockAction(index, false, handLayout)));
        }
        return roots;
    }

    /**
     * Generates *simulations* amount of states.
     *
     * @return list of generated states
     */
    private List<MiniState> generateStates(){
        List<MyCard> known = new ArrayList<>();
        List<MyCard> unknown = new ArrayList<>();
        for (int suit = 0; suit < memory.length; suit++) {
            for (int rank = 0; rank < memory[suit].length; rank++) {
                if(memory[suit][rank]==index+1 || (memory[suit][rank] == 1 && index != 1)){
                    known.add(new MyCard(suit,rank));
                }
                if(memory[suit][rank]==0){
                    unknown.add(new MyCard(suit,rank));
                }
            }
        }
        List<MiniState> states = new ArrayList<>();
        for (int i = 0; i < simulations; i++) {
            List<MyCard> hand = new ArrayList<>(known);
            List<MyCard> added = new ArrayList<>();
            while(hand.size()<=GameRules.baseCardsPerHand){
                int index = rd.nextInt(unknown.size());
                added.add(unknown.get(index));
                hand.add(unknown.remove(index));
            }
            states.add(new MiniState(viewHand(),hand,new ArrayList<MyCard>(unknown), (Stack<MyCard>) discardMemory.clone()));
            unknown.addAll(added);
        }
        return states;
    }

    /**
     * Executes the MCTS given a perfect information state.
     *
     * @param root containing all possible moves as children
     * @param state imagined state of game
     */
    private void monteCarloTreeSearch(MCTSNode root, MiniState state){
        while(!stopCondition()){
            //TODO
        }
    }

    private boolean stopCondition(){
        return rollouts<=maximumAmountOfRollouts;
    }

    /**
     * Does rollout part of MCTS (play till end).
     * Can be done either randomly or with a some other algorithm (BasicGreedy or ForcePlayer?).
     *
     * @param state that needs to be played out
     * @return true if this player wins, false if other wins
     */
    private boolean rollOut(MiniState state){
        //TODO
        rollouts++;
        return false;
    }

    /**
     * Finds index of best action.
     *
     * @param actions list to be searched
     * @return index of best action
     */
    private int findBestAction(List<MCTSNode> actions){
        int best=0;
        double max = 0;
        for (int i = 0; i < actions.size(); i++) {
            if(max<=actions.get(i).value()){
                max = actions.get(i).value();
                best = i;
            }
        }
        return best;
    }

    /**
     * Helper method. Prints. To be deleted.
     *
     * @param actions list of actions that can be done now
     * @param chosen index of action that has been chosen
     */
    private void print(List<MCTSNode> actions, int chosen){
        System.out.println("Chose "+chosen+" out of:\n");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println("\t"+i+". "+actions.get(i).action);
        }
    }
}