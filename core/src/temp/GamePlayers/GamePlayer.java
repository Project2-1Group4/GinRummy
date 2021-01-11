package temp.GamePlayers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.Layoff;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public abstract class GamePlayer implements PlayerInterface {

    protected List<MyCard> allCards;
    protected HandLayout handLayout;
    protected InputProcessor processor;

    public int index;

    // Player methods

    /**
     * Called:
     * -when cards have been distributed
     * -after having picked
     * -after having discarded
     * -after having decided to knock (or not)
     *
     * @param cards current cards the game has saved
     */
    public void update(List<MyCard> cards) {
        allCards = cards;
        handLayout = Finder.findBestHandLayout(allCards);
    }
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
    @Override
    public HandLayout confirmLayout() {
        return getBestMelds();
    }
    @Override
    public List<Layoff> layOff(List<Meld> knockerMelds) {
        return automaticLayoff(knockerMelds);
    }

    // "Eyes"/Listeners/Sensing Organ >.>

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

    // Getters

    public InputProcessor getProcessor() {
        return processor;
    }
    public List<MyCard> getHand() {
        return allCards;
    }
    public HandLayout viewHandLayout() {
        return handLayout;
    }
    public List<Meld> viewMelds() {
        return handLayout.melds();
    }
    public List<MyCard> viewUnusedHand() {
        return handLayout.unused();
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
    public List<Layoff> automaticLayoff(List<Meld> knockerMelds) {
        return Finder.findAllPossibleLayoffs(allCards, knockerMelds);
    }
}