package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.MyCard;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Does MCTS on x amount of created perfect information games
public class MCTSV1 extends MCTS{

    private final int simulations = 100; // Nb of perfect games simulated

    @Override
    protected void monteCarloTreeSearch(MCTSNode root, Knowledge knowledge){
        List<Knowledge> states = generateStates();
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
    private List<Knowledge> generateStates(){
        Knowledge knowledge = unpackMemory();
        List<Knowledge> states = new ArrayList<>();
        for (int i = 0; i < simulations; i++) {
            List<MyCard> hand = new ArrayList<>(knowledge.otherPlayer);
            List<MyCard> added = new ArrayList<>();
            while(hand.size()<= GameRules.baseCardsPerHand){
                int index = rd.nextInt(knowledge.unknown.size());
                added.add(knowledge.unknown.get(index));
                hand.add(knowledge.unknown.remove(index));
            }
            states.add(new Knowledge(viewHand(),hand,new ArrayList<>(knowledge.unknown),null, (Stack<MyCard>) discardMemory.clone()));
            knowledge.unknown.addAll(added);
        }
        return states;
    }

    @Override
    protected boolean rollOut(Knowledge state){
        return rollout(new basicGreedyTest(), new basicGreedyTest(),state, rd.nextInt());
    }
}
