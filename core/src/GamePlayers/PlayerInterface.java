package GamePlayers;

import GameLogic.GameActions.Action;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.Layoff;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import GameLogic.Logic.Finder;
import Graphics.RenderingSpecifics.PlayerRenderer;
import Graphics.Style;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

// Game <=> Player interaction
// Can be moved to GamePlayer, but it's nice to see the main methods a player needs to implement.
public interface PlayerInterface {

    // Syncing game state with player

    /**
     * Called every time a new round starts to let the player know to reset it's memory
     *
     * @param topOfDiscard opening discard card
     */
    void newRound(MyCard topOfDiscard);
    /**
     * Called:
     * -when cards have been distributed
     * -after having picked
     * -after having discarded
     * -after having decided to knock (or not)
     *
     * @param cards current cards the game has saved
     */
    void update(List<MyCard> cards);

    // Returns your move

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
    List<Layoff> layOff(List<Meld> knockerMelds);
    /**
     * @return null if no card has been chosen, otherwise the card you want to discard
     */
    MyCard discardCard();

    // Eyes, Sensing organ

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

    // Other

    /**
     * Called during the rendering. Override if you need to render something
     * @param batch sprite batch
     * @param renderStyle rendering textures, fonts, etc...
     * @param renderer basic player renderer
     */
    void render(SpriteBatch batch, Style renderStyle, PlayerRenderer renderer);
}
