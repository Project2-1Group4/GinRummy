package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

import java.util.List;
import java.util.Stack;

public class MiniState {
    public final List<MyCard> player;
    public final List<MyCard> otherPlayer;
    public final List<MyCard> deck;
    public final Stack<MyCard> discard;

    public MiniState(List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> deck, Stack<MyCard> discard) {
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.deck = deck;
        this.discard = discard;
    }


    public void undo(){
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
