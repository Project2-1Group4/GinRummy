package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GameRules;
import temp.Graphics.Style;

import java.util.List;

/**
 * Used when actor takes too long on their turn.
 * <p>
 * Prioritizes deck always
 * Discards first unused card
 * Only knocks on Gin
 */
public class ForceActor extends GameActor {
    private boolean onlyGin = false;
    private GameActor actor;

    public ForceActor(GameActor actor) {
        this.actor = actor;
    }

    @Override
    public HandLayout viewHandLayout() {
        return actor.viewHandLayout();
    }

    @Override
    public List<MyCard> viewHand() {
        return actor.viewHand();
    }

    @Override
    public void render(SpriteBatch batch, Style renderingStyle) {
        actor.render(batch, renderingStyle);
    }

    @Override
    public Boolean knockOrContinue() {
        if(onlyGin) {
            return actor.handLayout.viewUnusedCards().size() == 0;
        }
        return actor.handLayout.getDeadwood()<= GameRules.minDeadwoodToKnock;
    }

    @Override
    public Boolean pickDeckOrDiscard(boolean deckEmpty, MyCard topOfDiscard) {
        return !deckEmpty;
    }

    @Override
    public HandLayout confirmMelds() {
        return actor.getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return actor.automaticLayoff(knockerMelds);
    }

    @Override
    public MyCard discardCard() {
        return actor.handLayout.viewUnusedCards().get(0);
    }
}
