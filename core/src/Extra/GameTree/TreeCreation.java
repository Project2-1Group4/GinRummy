package Extra.GameTree;

import GameLogic.Entities.Step;
import GameLogic.GameActions.Action;
import GameLogic.GameActions.KnockAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.MyCard;
import GameLogic.States.CardsInfo;
import GameLogic.States.RoundState;
import temp.GameRules;

import java.util.*;

public class TreeCreation {
    /* Base code. To make it more "viable" to for a bot:
     -if it's the players turn, only add the best move
     -save probabilities in node class
     -have an array of currently unknown cards also pass through
     ^(replace List of unknown in pickActions method with array[][] or w/e you're using as unknown cards
    */

    public static void main(String[] args) {
        RoundState roundState = new RoundState(CardsInfo.getRandom(2, 1));

        Node root = limitedDFS(roundState, 7);
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
    public static Node limitedDFS(RoundState curState, int wantedDepth){
        Node root = new Node(0,null,null);
        limitedDFS(curState,root,0,wantedDepth);
        return root;
    }
    private static void limitedDFS(RoundState roundState, Node curNode, int depth, int wantedDepth) {
        if (curNode.action instanceof KnockAction) {
            if (((KnockAction) curNode.action).knock) {
                return;
            }
        }
        if (depth == wantedDepth) {
            return;
        }
        if(!curNode.action.doAction(roundState, true)){
            System.out.println("ERROR do LimitedDFS");
        }
        List<Action> possibleActions = (List<Action>) TreeExpander.getPossibleActions(roundState);
        if(roundState.turn().step == Step.Pick){
            possibleActions = pickActions(possibleActions, roundState.deck(), roundState.turn().playerIndex);
        }
        for (Action action : possibleActions) {
            Node child = new Node(depth + 1, curNode, action);
            limitedDFS(roundState, child, depth + 1, wantedDepth);
            curNode.children.add(child);
        }
        if(!curNode.action.undoAction(roundState)){
            System.out.println("ERROR undo limitedDFS");
        }
    }

    //TODO move to TreeExpander?
    /**
     * Returns a list of all possible hands the enemy player can have given what you know he has and what you don't know
     * @param saved init with null
     * @param curHand list of cards you know the enemy has
     * @param options list of cards of unknown location (deck or part of enemy init hand)
     * @return all possible hands
     */
    public static List<List<MyCard>> getPossibleHands (List<List<MyCard>> saved, List<MyCard> curHand, List<MyCard> options,int idx) {
        if(curHand.size()== GameRules.baseCardsPerHand){
            if(saved==null){
                saved = new ArrayList<>();
            }
            saved.add(curHand);
            return saved;
        }
        for(int i = idx ; i < options.size(); i++) {
            List<MyCard> newHand = new ArrayList<>(curHand);
            newHand.add(options.get(i));
            saved = getPossibleHands(saved, newHand,options,++idx);
        }
        return saved;
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
