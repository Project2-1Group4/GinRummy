package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;
import temp.GamePlayers.GreedyAIs.meldBuildingGreedy;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// Does MCTS on x amount of created perfect information games
public class MCTSV1 extends MCTS{

    private final int simulations = 100; // Nb of perfect games simulated

    public MCTSV1(int seed){
        super(seed);
    }
    public MCTSV1(){
        super();
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, Knowledge knowledge){
        List<Knowledge> states = generateStates(knowledge);
        for (Knowledge miniState : states) {
            miniState.step = knowledge.step;
            miniState.turn = knowledge.turn;
            mcts(root, miniState);
        }
    }

    /**
     * Generates *simulations* amount of states.
     *
     * @return list of generated states
     */
    private List<Knowledge> generateStates(Knowledge knowledge){
        List<Knowledge> states = new ArrayList<>();
        for (int i = 0; i < simulations; i++) {
            List<MyCard> hand = new ArrayList<>(knowledge.otherPlayer);
            List<MyCard> added = new ArrayList<>();
            while(hand.size()< GameRules.baseCardsPerHand){
                int index = rd.nextInt(knowledge.unknown.size());
                added.add(knowledge.unknown.get(index));
                hand.add(knowledge.unknown.remove(index));
            }
            states.add(new Knowledge(knowledge.step, knowledge.turn, viewHand(),hand,new ArrayList<>(knowledge.unknown),null, (Stack<MyCard>) discardMemory.clone()));
            knowledge.unknown.addAll(added);
        }
        return states;
    }

    @Override
    protected void rollout(MCTSNode node, Knowledge state){
        for (int i = 0; i < rolloutsPerNode; i++) {
            node.wins += executeRollout(new ForcePlayer(), new ForcePlayer(),state, rd.nextInt());
            node.rollouts += 1;
        }
        backPropagate(node);
    }
}