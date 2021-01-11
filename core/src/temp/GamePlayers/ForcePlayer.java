package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.Layoff;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Logic.Finder;
import temp.GameRules;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.Style;

import java.util.List;
import java.util.Random;

/**
 * Used when player takes too long on their turn.
 * <p>
 * Prioritizes deck always
 * Discards first unused card
 * Only knocks on Gin
 */
public class ForcePlayer extends GamePlayer {
    private final boolean onlyGin = false;
    private final Random rd;
    public final GamePlayer player;

    public ForcePlayer(GamePlayer player, Integer seed) {
        if(seed==null){
            rd = new Random();
        }
        else {
            rd = new Random(seed);
        }
        this.player = player;
        this.handLayout = player.handLayout;
        this.allCards = player.allCards;
    }
    public ForcePlayer(Integer seed){
        if(seed==null){
            rd = new Random();
        }
        else {
            rd = new Random(seed);
        }
        player = null;
    }

    @Override
    public void update(List<MyCard> cards) {
        allCards = cards;
    }

    @Override
    public void render(SpriteBatch batch, Style renderingStyle, PlayerRenderer renderer) {
        if(player!=null) {
            player.render(batch, renderingStyle, renderer);
        }
    }

    @Override
    public Boolean knockOrContinue() {
        handLayout = Finder.findBestHandLayout(allCards);
        if (onlyGin) {
            return handLayout.unused().size() == 0;
        }
        return handLayout.deadwoodValue() <= GameRules.minDeadwoodToKnock;
    }
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return rd.nextBoolean();
    }

    @Override
    public HandLayout confirmLayout() {
        return getBestMelds();
    }

    @Override
    public MyCard discardCard() {
        handLayout = Finder.findBestHandLayout(allCards);
        List<MyCard> unused = handLayout.unused();
        if(unused.size()==0){
            List<Meld> melds = handLayout.melds();
            for (Meld meld : melds) {
                if (meld.size() > 3) {
                    return meld.cards().get(0);
                }
            }
        }
        return chooseWeightedRd(unused);
    }
    @Override
    public void playerDiscarded(DiscardAction discardAction) {

    }
    @Override
    public void playerPicked(PickAction pickAction) {

    }

    private MyCard chooseWeightedRd(List<MyCard> cards){
        int sum_of_weight = 0;
        for(int i=0; i<cards.size(); i++) {
            sum_of_weight += cards.get(i).ginValue();
        }
        int rnd = rd.nextInt(sum_of_weight);
        for(int i=0; i<cards.size(); i++) {
            if(rnd < cards.get(i).ginValue())
                return cards.get(i);
            rnd -= cards.get(i).ginValue();
        }
        return null;
    }
}
