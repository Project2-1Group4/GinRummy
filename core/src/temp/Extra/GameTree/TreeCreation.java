package temp.Extra.GameTree;

import temp.Extra.GameTree.Bot.Bot;
import temp.Extra.GameTree.Bot.BotMemory;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MyCard;
import temp.GameLogic.TreeExpander;
import temp.GamePlayers.CombinePlayer;

import java.util.*;

public class TreeCreation {
    /* Base code. To make it more "viable" to for a bot:
     -if it's the players turn, only add the best move
     -save probabilities in node class
     -have an array of currently unknown cards also pass through
     ^(replace List of unknown in pickActions method with array[][] or w/e you're using as unknown cards
    */

    public static void main(String[] args) {
        State startState = new StateBuilder()
                .setSeed(11)
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .addPlayer(CombinePlayer.getBaseCombinePlayer())
                .build();
        startState = Executor.startNewRound(500, startState);

        BFSNode root = timedLimitedDepthBFS(startState, 7,120);
        System.out.println(root.nodesUntilDepth(8));
        System.out.println(Arrays.toString(root.widthsAtDepths(8)));

        /*Bot b = new Bot();
        Node root = b.DFS(6);
        System.out.println(Arrays.toString(root.widthsAtDepths(6)));*/
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// ONLY WORKS WITH PERFECT INFORMATION //////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns root node once wantedDepth reached and completed
     * @param curState current game state
     * @param wantedDepth wanted depth to stop the search
     * @return root node of tree
     */
    public static Node limitedDFS(State curState, int wantedDepth){
        Node root = new Node(0,null,null);
        limitedDFS(curState,root,0,wantedDepth);
        return root;
    }
    private static void limitedDFS(State curState, Node curNode, int depth, int wantedDepth) {
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
            limitedDFS(newState, child, depth + 1, wantedDepth);
            curNode.children.add(child);
        }
    }

    /**
     * Returns root node once wantedDepth reached and completed OR timeLimit reached
     * @param curState current game state
     * @param wantedDepth wanted depth to stop the search
     * @param timeLimit time allotted to complete search in seconds
     * @return root node of tree
     */
    public static Node timedLimitedDFS(State curState, int wantedDepth, double timeLimit){
        Node root = new Node(0,null,null);
        timedLimitedDFS(curState,root,0,wantedDepth,System.currentTimeMillis(),timeLimit);
        return root;
    }
    private static void timedLimitedDFS(State curState, Node curNode, int depth, int wantedDepth, long s, double timeLimit) {
        if (curNode.action instanceof KnockAction) {
            if (((KnockAction) curNode.action).knock) {
                return;
            }
        }
        if (depth == wantedDepth || timeLimit<=(System.currentTimeMillis()-s)/(double)1000) {
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
            timedLimitedDFS(newState, child, depth + 1, wantedDepth,s,timeLimit);
            curNode.children.add(child);
        }
    }

    /**
     * Returns root node once wantedDepth reached and completed
     * @param curState current game state
     * @param wantedDepth wanted depth to stop thre search at
     * @return root node of tree
     */
    public static BFSNode limitedDepthBFS(State curState, int wantedDepth){
        BFSNode root = new BFSNode(0,null,null);
        Queue<BFSNode> queue = new LinkedList<>();
        queue.add(root);
        while(queue.peek()!=null){
            BFSNode v = queue.poll();
            if(v.depth==wantedDepth){
                continue;
            }
            if(v.state==null){
                if(v.parent==null){
                    v.state = curState;
                }else{
                    State state = ((BFSNode)v.parent).state.copy();
                    Executor.execute(v.action,state);
                    v.state = state;
                }
                if (v.action instanceof KnockAction) {
                    if (((KnockAction) v.action).knock) {
                        continue;
                    }
                }
                State newState = v.state;
                List<Action> possibleActions = (List<Action>) TreeExpander.getPossibleActions(newState);
                if(newState.getStep()== State.StepInTurn.Pick){
                    possibleActions = pickActions(possibleActions, newState.getDeck(), newState.getPlayerNumber());
                }
                for (Action action : possibleActions) {
                    BFSNode child = new BFSNode(v.depth + 1, v, action);
                    v.children.add(child);
                }
            }
            for(Node child: v.children) {
                BFSNode c = (BFSNode)child;
                if(!c.discovered){
                    c.discovered = true;
                    queue.add(c);
                }
            }
        }
        return root;
    }

    /**
     * Returns root node once wantedDepth reached completed OR once time limit reached
     * @param curState current game state
     * @param wantedDepth wanted depth to stop the search at
     * @param timeLimit time limit in seconds
     * @return root node of tree
     */
    public static BFSNode timedLimitedDepthBFS(State curState, int wantedDepth, double timeLimit){
        final long s = System.currentTimeMillis();
        BFSNode root = new BFSNode(0,null,null);
        Queue<BFSNode> queue = new LinkedList<>();
        queue.add(root);
        while(queue.peek()!=null){
            if(timeLimit<=(System.currentTimeMillis()-s)/(double)1000){
                break;
            }
            BFSNode v = queue.poll();
            if(v.depth==wantedDepth){
                continue;
            }
            if(v.state==null){
                if(v.parent==null){
                    v.state = curState;
                }else{
                    State state = ((BFSNode)v.parent).state.copy();
                    Executor.execute(v.action,state);
                    v.state = state;
                }
                if (v.action instanceof KnockAction) {
                    if (((KnockAction) v.action).knock) {
                        continue;
                    }
                }
                State newState = v.state;
                List<Action> possibleActions = (List<Action>) TreeExpander.getPossibleActions(newState);
                if(newState.getStep()== State.StepInTurn.Pick){
                    possibleActions = pickActions(possibleActions, newState.getDeck(), newState.getPlayerNumber());
                }
                for (Action action : possibleActions) {
                    BFSNode child = new BFSNode(v.depth + 1, v, action);
                    v.children.add(child);
                }
            }
            for(Node child: v.children) {
                BFSNode c = (BFSNode)child;
                if(!c.discovered){
                    c.discovered = true;
                    queue.add(c);
                }
            }
        }
        return root;
    }

    /**
     * All possible picks using unknown cards assuming they're on top of deck
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
