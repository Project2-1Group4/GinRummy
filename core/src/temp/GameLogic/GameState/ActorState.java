package temp.GameLogic.GameState;

import temp.GameLogic.MELDINGOMEGALUL.Calculator;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Anything that wants to play the game needs to inherit this.
 * Holds the game state. Only modifiable by executor. Visible by all.
 */
public class ActorState {

    protected int index;
    protected HandLayout handLayout;

    public ActorState() {
        handLayout = new HandLayout();
    }

    protected boolean removeUnusedCard(MyCard card){
        return handLayout.removeUnusedCard(card);
    }

    protected boolean removeCard(MyCard card){
        return handLayout.removeCard(card);
    }

    // Only viewing, no changing of the real internal state of actor without the use of the executor
    public List<MyCard> viewHand() {
        return handLayout.viewAllCards();
    }

    public List<MyCard> viewUnusedCards(){
        return handLayout.viewUnusedCards();
    }

    public List<Meld> viewMelds(){
        return handLayout.viewMelds();
    }

    public HandLayout viewHandLayout() {
        return handLayout.deepCopy();
    }
}
