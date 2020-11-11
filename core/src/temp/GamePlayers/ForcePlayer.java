package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GameRules;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.BasicPlayerRenderer;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;
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

    public ForcePlayer(GamePlayer player, PlayerRenderer renderer) {
        super(renderer);
        this.player = player;
        this.handLayout = player.handLayout;
        this.allCards = player.allCards;
        this.renderer = player.renderer;
    }
    public ForcePlayer(GamePlayer player) {
        this(player, new BasicPlayerRenderer());
    }

    @Override
    public HandLayout viewHandLayout() {
        return viewHandLayout();
    }

    @Override
    public List<MyCard> viewHand() {
        return viewHand();
    }

    @Override
    public void render(SpriteBatch batch, Style renderingStyle) {
        render(batch, renderingStyle);
    }

    @Override
    public Boolean knockOrContinue() {
        if(onlyGin) {
            return handLayout.viewUnusedCards().size() == 0;
        }
        return handLayout.getDeadwood()<= GameRules.minDeadwoodToKnock;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return remainingCardsInDeck!=0;
    }

    @Override
    public HandLayout confirmLayout() {
        return getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return automaticLayoff(knockerMelds);
    }

    @Override
    public MyCard discardCard() {
        return handLayout.viewUnusedCards().get(0);
    }
}
