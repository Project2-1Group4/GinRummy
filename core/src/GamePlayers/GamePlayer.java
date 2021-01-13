package GamePlayers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import GameLogic.GameActions.Action;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.Layoff;
import GameLogic.Logic.Finder;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import Graphics.RenderingSpecifics.PlayerRenderer;
import Graphics.Style;

import java.util.List;

// Extend to make new type of player
public abstract class GamePlayer implements PlayerInterface {

    protected List<MyCard> allCards;
    protected HandLayout handLayout;
    protected InputProcessor processor;

    public int index;

    // Game <=> Player interaction

    @Override
    public void update(List<MyCard> cards) {
        allCards = cards;
        handLayout = Finder.findBestHandLayout(allCards);
    }
    @Override
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