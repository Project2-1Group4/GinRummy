package temp.GamePlayers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public abstract class GamePlayer implements PlayerInterface {

    private static int player = 0;

    private static int getPlayer() {
        player++;
        return player;
    }

    protected List<MyCard> allCards;
    protected HandLayout handLayout;
    protected InputProcessor processor;
    public final int index;

    public GamePlayer() {
        index = getPlayer();
    }

    /* SETTERS */

    /**
     * Called:
     * -when cards have been distributed
     * -after having picked
     * -after having discarded
     * -after having decided to knock (or not)
     *
     * @param realLayout current hand layout being considered by the game
     */
    public void update(HandLayout realLayout) {
        allCards = realLayout.viewAllCards();
        handLayout = Finder.findBestHandLayout(allCards);
    }

    /* GETTERS */

    public InputProcessor getProcessor() {
        return processor;
    }

    // All views. IDK why
    public List<MyCard> viewHand() {
        return new ArrayList<>(allCards);
    }

    public HandLayout viewHandLayout() {
        return handLayout.deepCopy();
    }

    public List<Meld> viewMelds() {
        return handLayout.viewMelds();
    }

    public List<MyCard> viewUnusedHand() {
        return handLayout.viewUnusedCards();
    }

    /**
     * To allow all players to get this feature
     * Automatically creates best melds for given hand
     */
    public HandLayout getBestMelds() {
        return Finder.findBestHandLayout(allCards);
    }

    /**
     * To allow all players to get this feature
     * Automatically lays the most cards off
     * <p>
     * Shit show of a method. Dont feel like cleaning up
     *
     * @param knockerMelds list of melds of the player that knocked
     * @return layoff object
     */
    public Layoff automaticLayoff(List<Meld> knockerMelds) {
        List<MyCard> unusedCards = handLayout.viewUnusedCards();
        // For all melds
        for (Meld knockerMeld : knockerMelds) {
            Integer index = Finder.findFirstIndexThatFitsInMeld(unusedCards, knockerMeld);
            if (index != null) {
                return new Layoff(unusedCards.get(index), knockerMeld);
            }
        }
        return new Layoff(null, null);
    }

    /* EXTRA */
    public void render(SpriteBatch batch, Style renderStyle, PlayerRenderer renderer) {
        // In case you want to render extra
    }

    /**
     * Called at the start of every round once the cards have been distributed
     *
     * @param topOfDiscard current top of discard
     */
    @Override
    public void newRound(MyCard topOfDiscard) {
    }

    /* PLAYER METHODS */
    @Override
    public HandLayout confirmLayout() {
        return getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return automaticLayoff(knockerMelds);
    }

    @Override
    public void playerActed(Action action) {
        if (action.playerIndex != index) {
            if (action instanceof PickAction) {
                playerPicked((PickAction) action);
            } else if (action instanceof DiscardAction) {
                playerDiscarded((DiscardAction) action);
            }
        }
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
    }

    @Override
    public void playerPicked(PickAction pickAction) {
    }

    @Override
    public void executed(Action action) {

    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "handLayout=" + handLayout +
                '}';
    }
}