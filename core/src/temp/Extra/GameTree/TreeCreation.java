package temp.Extra.GameTree;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MyCard;
import temp.GameLogic.TreeExpander;
import temp.GamePlayers.CombinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeCreation {
    public static void main(String[] args) {
        State startState = new StateBuilder()
                .setSeed(11)
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .build();
        startState = Executor.startNewRound(500, startState);
        Node root = new Node(0, null, null);

        s = System.currentTimeMillis();
        limitedDepthFirstSearch(startState, root, 0, 7);
        System.out.println(root.nodesUntilDepth(10));
        System.out.println(Arrays.toString(root.widthsAtDepths(7)));
    }

    /* To set time limit, feed in System.currentTimeMillis() and maxTime as extra variables.
    Add check if(maxTime<=System.currentTimeMillis()-s/(double)1000) return;
     */
    static long s;
    static double time=0;
    static double maxTime = 50;

    /* Base code. To make it more "viable" to for a bot:
     -if it's the players turn, only add the best move
     -save probabilities in node class
     -have an array of currently unknown cards also pass through
     ^(replace List of unknown in pickActions method with array[][] or w/e you're using as unknown cards
    */
    public static void limitedDepthFirstSearch(State curState, Node curNode, int depth, int wantedDepth) {
        if (curNode.action instanceof KnockAction) {
            if (((KnockAction) curNode.action).knock) {
                return;
            }
        }
        if (depth == wantedDepth) {
            return;
        }
        State newState = curState.copy();
        Executor.execute(curNode.action, newState);
        List<Action> possibleActions = (List<Action>) TreeExpander.getPossibleActions(newState);
        if(newState.getStep()== State.StepInTurn.Pick){
            possibleActions = pickActions(possibleActions, newState.getDeck(), newState.getPlayerNumber());
        }
        for (Action action : possibleActions) {
            Node child = new Node(depth + 1, curNode, action);
            limitedDepthFirstSearch(newState, child, depth + 1, wantedDepth);
            curNode.children.add(child);
        }
    }

    /**
     *
     * @param possibleActions current possible actions
     * @param unknown list of cards with unknown whereabouts
     * @param index of player
     * @return all possible picks based on the players knowledge
     */
    private static List<Action> pickActions(List<Action> possibleActions, List<MyCard> unknown, int index){
        List<Action> newPossible = new ArrayList<>();
        if(possibleActions.size()!=0) {
            for (Action possibleAction : possibleActions) {
                if (!((PickAction) possibleAction).deck) {
                    newPossible.add(possibleAction);
                }
            }
        }
        for (MyCard myCard : unknown) {
            newPossible.add(new PickAction(index,true,myCard));
        }
        return newPossible;
    }
}
