package temp.Extra.Test;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.TreeExpander;

import java.util.List;

public class test {
    public static void main(String[] args){
        State startState = new StateBuilder().build();
        startState = Executor.startNewRound(500, startState);
        Node root = new Node(0,null, null);
        recursiveTree(startState,root,0,3);
        root.print(0);
    }

    public static void recursiveTree(State curState,Node curNode, int depth, int wantedDepth){
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
