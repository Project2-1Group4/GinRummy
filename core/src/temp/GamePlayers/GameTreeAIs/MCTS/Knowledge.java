package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

import java.util.List;
import java.util.Stack;

public class Knowledge {
    public final List<MyCard> player;
    public final List<MyCard> otherPlayer;
    public final List<MyCard> deck;
    public final List<MyCard> unknown;
    public final Stack<MyCard> discard;

    public State.StepInTurn step;
    // 0 = me, 1 = other
    public int turn;

    public Knowledge(List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> deck,List<MyCard> unknown, Stack<MyCard> discard) {
        assert (deck == null && unknown != null) || (deck != null && unknown == null);
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.deck = deck;
        this.unknown = unknown;
        this.discard = discard;
    }


    public void execute(Action action){
        //TODO
    }

    public void undo(Action action){
        //TODO
    }

    private boolean remove(List<MyCard> list, MyCard card){
        for (int i = 0; i < list.size(); i++) {
            if(card.same(list.get(i))){
                list.remove(i);
                return true;
            }
        }
        return false;
    }
}
