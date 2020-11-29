package temp.GamePlayers;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

import java.util.List;

// Can be moved to GamePlayer, but it's nice to see the main methods a player needs to implement.
public interface PlayerInterface {
    /**
     * @return true if knock, false if continue, null if no decision
     */
    Boolean knockOrContinue();

    /**
     * @return true if deck, false if discard, null if no decision
     */
    Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard);

    /**
     * @return true if confirmed, false if not done
     */
    HandLayout confirmLayout();

    /**
     * Layoff 1 card at a time because idk how to implement in another way
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
     *
     * @param topOfDiscard
     */
    void newRound(MyCard topOfDiscard);

    /**
     * Feeds in every action done by other players to every other player with all specifics
     * If I act, this won't get called
     *
     * @param action action executed
     */
    void playerActed(Action action);

    /**
     * Feeds in the discardAction executed (including card and who did it)
     * If playerActed not overwritten then only called when other players discard
     * Otherwise never
     *
     * @param discardAction action executed
     */
    void playerDiscarded(DiscardAction discardAction);

    /**
     * Feeds in pickAction executed (including where, which card and who did it)
     * If playerActed not overwritten then only called when other players picked
     * Otherwise never
     *
     * @param pickAction action executed
     */
    void playerPicked(PickAction pickAction);

    /**
     * Called after this players action has been executed
     *
     * @param action executed
     */
    void executed(Action action);
}
