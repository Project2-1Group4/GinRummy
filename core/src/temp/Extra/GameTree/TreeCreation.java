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
        recursiveTree(startState, root, 0, 7);
        System.out.println(root.treeWidthAtDepth(7));
    }

    // Base code. To make it more "viable" to for a bot:
    // -if it's the players turn, only add the best move
    // -save probabilities in node class
    // -have an array of currently unknown cards also pass through
    // ^(replace List of unknown in pickActions method with array[][] or w/e you're using as unknown cards
    public static void recursiveTree(State curState, Node curNode, int depth, int wantedDepth) {
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
            recursiveTree(newState, child, depth + 1, wantedDepth);
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
