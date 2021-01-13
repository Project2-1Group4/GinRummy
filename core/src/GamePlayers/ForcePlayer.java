package GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import GameLogic.Logic.Finder;
import temp.GameRules;
import Graphics.RenderingSpecifics.PlayerRenderer;
import Graphics.Style;

import java.util.List;
import java.util.Random;

// Guided randomness
public class ForcePlayer extends GamePlayer {
    public boolean onlyGin = false;
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

    // Game <=> Player interaction

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
        if(remainingCardsInDeck!=0 && topOfDiscard!=null){
            return rd.nextBoolean();
        }
        return remainingCardsInDeck != 0;
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
    public void update(List<MyCard> cards) {
        allCards = cards;
    }
    @Override
    public void render(SpriteBatch batch, Style renderingStyle, PlayerRenderer renderer) {
        if(player!=null) {
            player.render(batch, renderingStyle, renderer);
        }
    }

    // Getters

    /**
     * Chooses card random card biased by it's value in the deadwood
     * Higher deadwood value = more chance to be picked
     * @param cards list of cards to pick from
     * @return card chosen
     */
    private MyCard chooseWeightedRd(List<MyCard> cards){
        int sum_of_weight = 0;
        for (MyCard card : cards) {
            sum_of_weight += card.ginValue();
        }
        int rnd = rd.nextInt(sum_of_weight);
        for (MyCard card : cards) {
            if (rnd < card.ginValue())
                return card;
            rnd -= card.ginValue();
        }
        return null;
    }
}
