package temp.Extra.Test;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.TreeExpander;
import temp.GamePlayers.CombinePlayer;

import java.util.List;

public class test {
    public static void main(String[] args){
        State startState = new StateBuilder()
                .setSeed(11)
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .build();
        startState = Executor.startNewRound(500, startState);
        Node root = new Node(0,null, null);
        recursiveTree(startState,root,0,14);
        System.out.println(root.treeWidthAtDepth(14));
    }

    public static void recursiveTree(State curState,Node curNode, int depth, int wantedDepth){
        if(curNode.action instanceof KnockAction){
            if(((KnockAction)curNode.action).knock){
                return;
            }
        }
        if(depth==wantedDepth){
            return;
        }
        State newState = curState.copy();
        Executor.execute(curNode.action, newState);
        List<Action> possibleActions = (List<Action>) TreeExpander.getPossibleActions(newState);
        for (Action action : possibleActions) {
            Node child = new Node(depth+1, curNode,action);
            recursiveTree(newState,child,depth+1,wantedDepth);
            curNode.children.add(child);
        }
    }
}
