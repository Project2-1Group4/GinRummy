package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GameRules;
import temp.Graphics.Style;

import java.util.List;

/**
 * Used when player takes too long on their turn.
 * <p>
 * Prioritizes deck always
 * Discards first unused card
 * Only knocks on Gin
 */
public class ForcePlayer extends GamePlayer {
    private boolean onlyGin = false;
    private GamePlayer player;

    public ForcePlayer(GamePlayer player) {
        this.player = player;
    }

    @Override
    public HandLayout viewHandLayout() {
        return player.viewHandLayout();
    }

    @Override
    public List<MyCard> viewHand() {
        return player.viewHand();
    }

    @Override
    public void render(SpriteBatch batch, Style renderingStyle) {
        player.render(batch, renderingStyle);
    }

    @Override
    public Boolean knockOrContinue() {
        if(onlyGin) {
            return player.handLayout.viewUnusedCards().size() == 0;
        }
        return player.handLayout.getDeadwood()<= GameRules.minDeadwoodToKnock;
    }

    @Override
    public Boolean pickDeckOrDiscard(boolean deckEmpty, MyCard topOfDiscard) {
        return !deckEmpty;
    }

    @Override
    public HandLayout confirmLayout() {
        return player.getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return player.automaticLayoff(knockerMelds);
    }

    @Override
    public MyCard discardCard() {
        return player.handLayout.viewUnusedCards().get(0);
    }
}
