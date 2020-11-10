package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

import java.util.List;

/**
 * Can be moved to actor (maybe should because it's kinda useless)
 */
public interface ActorInterface {
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
    HandLayout confirmMelds();


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
}
