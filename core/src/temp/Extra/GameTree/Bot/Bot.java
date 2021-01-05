package temp.Extra.GameTree.Bot;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.GameLogic.TreeExpander;

import java.util.ArrayList;
import java.util.List;

// Only works on 2 player game. 0 is me, 1 is other in game tree
// Works with imperfect information
// TODO make time limited BFS that works with this
public class Bot {
    public BotMemory memory;
    public Bot(){
        memory = BotMemory.test();
    }

    public BotNode DFS(int wantedDepth){
        BotNode root = new BotNode(0,null,null,-1);
        DFS(root,memory,0,wantedDepth);
        return root;
    }

    private void DFS(BotNode curNode, BotMemory cards, int depth, int wantedDepth){
        assert cards.nbOfCards() == 52;
        if (curNode.action instanceof KnockAction) {
            if (((KnockAction) curNode.action).knock) {
                return;
            }
        }
        if (depth == wantedDepth) {
            return;
        }
        //TODO pruning somewhere around here
        List<? extends Action> possibleActions = getPossibleActions(cards);
        for (Action action : possibleActions) {
            //TODO set probability of every node
            BotNode child = new BotNode(depth + 1, curNode, action,cards.playerTurn);
            BotMemory next = cards.copy();
            next.execute(action);
            DFS(child,next, depth + 1, wantedDepth);
            curNode.children.add(child);
        }
    }

    private List<? extends Action> getPossibleActions(BotMemory cards){
        if(cards.step == State.StepInTurn.Pick){
            List<PickAction> a = TreeExpander.getPossiblePickActions(cards.playerTurn,cards.deckSize,cards.discard.size()==0?null:cards.discard.peek());
            return pickActions(a,cards.unknown, cards.playerTurn);
        }
        else if(cards.step == State.StepInTurn.KnockOrContinue){
            if(cards.playerTurn==0) {
                return TreeExpander.getPossibleKnockActions(cards.playerTurn, cards.player);
            }
            else {
                List<MyCard> c = new ArrayList<>(cards.otherPlayer);
                //TODO how to handle knocking of other player
                if(c.size() >= 7){
                    return TreeExpander.getPossibleKnockActions(cards.playerTurn, c);
                }
                List<Action> l = new ArrayList();
                l.add(new KnockAction(cards.playerTurn,false,null));
                return l;
            }
        }
        else {
            assert cards.step == State.StepInTurn.Discard;
            if(cards.playerTurn==0) {
                return TreeExpander.getPossibleDiscardActions(cards.playerTurn,cards.player);
            }else {
                List<MyCard> c = new ArrayList<>(cards.otherPlayer);
                c.addAll(cards.unknown);
                return TreeExpander.getPossibleDiscardActions(cards.playerTurn,c);
            }
        }
    }

    private static List<PickAction> pickActions(List<PickAction> possibleActions, List<MyCard> unknown, int index){
        List<PickAction> newPossible = new ArrayList<>();
        if(possibleActions.size()!=0) {
            for (PickAction possibleAction : possibleActions) {
                if (!possibleAction.deck) {
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
