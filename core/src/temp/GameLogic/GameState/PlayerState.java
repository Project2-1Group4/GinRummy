package temp.GameLogic.GameState;

import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

import java.util.List;

/**
 * Anything that wants to play the game needs to inherit this.
 * Holds the game state. Only modifiable by executor. Visible by all.
 */
// Can simply extend HandLayout/be replaced by HandLayout where it's used
public class PlayerState {

    protected HandLayout handLayout;

    public PlayerState() {
        handLayout = new HandLayout();
    }

    // SETTERS
    // Quality of life methods
    protected boolean removeUnusedCard(MyCard card) {
        return handLayout.removeUnusedCard(card);
    }

    protected boolean removeCard(MyCard card) {
        return handLayout.removeCard(card);
    }

    // GETTERS
    // Returns copies to avoid the changing of the inner state outside of package
    public List<MyCard> viewHand() {
        return handLayout.viewAllCards();
    }

    public List<MyCard> viewUnusedCards() {
        return handLayout.viewUnusedCards();
    }

    public List<Meld> viewMelds() {
        return handLayout.viewMelds();
    }

    public HandLayout viewHandLayout() {
        return handLayout.deepCopy();
    }

    public int getDeadwood(){
        return handLayout.getDeadwood();
    }

    public int getNumberOfCardsInDeadwood(){
        return handLayout.getNumberOfCardsInDeadwood();
    }
    public String toString() {
        return handLayout.toString();
    }

    public PlayerState copy() {
        PlayerState pState = new PlayerState();
        pState.handLayout = handLayout.deepCopy();
        return pState;
    }
}
