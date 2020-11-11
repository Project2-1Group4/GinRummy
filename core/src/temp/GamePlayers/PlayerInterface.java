package temp.GamePlayers;

import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

import java.util.List;

/**
 * Can be moved to player (maybe should because it's kinda useless)
 */
public interface PlayerInterface {
    /**
     * @return true if knock, false if continue, null if no decision
     */
    Boolean knockOrContinue();

    /**
     * @return true if deck, false if discard, null if no decision
     */
    Boolean pickDeckOrDiscard(boolean deckEmpty, MyCard topOfDiscard);

    /**
     * @return true if confirmed, false if not done
     */
    HandLayout confirmLayout();

    /**
     *  Layoff 1 card at a time because idk how to implement in another way
     *
      * @param knockerMelds melds of knocker
     * @return null if nothing, layoff if card to layoff, layoff with only nulls
     */
    Layoff layOff(List<Meld> knockerMelds);

    /**
     * @return null if no card has been chosen, otherwise the card you want to discard
     */
    MyCard discardCard();

    /**
     * Called every time a new round starts to let the player know to reset it's memory
     */
    void newRound();
}
