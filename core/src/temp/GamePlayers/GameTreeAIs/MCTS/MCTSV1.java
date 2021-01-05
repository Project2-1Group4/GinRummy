package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Does MCTS on x amount of created perfect information games
public class MCTSV1 extends MCTS{

    @Override
    protected Action getBestAction(State.StepInTurn step){
        rollouts = 0;

        MCTSNode root = getPossibleMoves(unpackMemory());
        List<Knowledge> states = generateStates();

        for (Knowledge miniState : states) {
            monteCarloTreeSearch(root, miniState);
        }
        int best = findBestAction(root.children);
        print(root.children,best);
        return root.children.get(best).action;
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
        /*
        TODO make rollout algorithm (in MCTS class prob)
         */
        return false;
    }
}
