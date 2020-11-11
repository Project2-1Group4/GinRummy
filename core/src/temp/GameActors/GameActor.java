package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public abstract class GameActor implements ActorInterface {

    protected List<MyCard> allCards;
    protected HandLayout handLayout;

    public GameActor(){
    }

    /* SETTERS */
    public void update(HandLayout realLayout){

        allCards = realLayout.viewAllCards();
        handLayout = Finder.findBestHandLayout(allCards);
    }

    /* GETTERS */
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

    /**
     * To allow all actors to get this feature
     * Automatically creates best melds for given hand
     */
    public HandLayout getBestMelds() {
        return Finder.findBestHandLayout(allCards);
    }

    /**
     * To allow all actors to get this feature
     * Automatically lays the most cards off
     *
     * Shit show of a method. Dont feel like cleaning up
     *
     * @param knockerMelds list of melds of the player that knocked
     * @return layoff object
     */
    public Layoff automaticLayoff(List<Meld> knockerMelds) {

        List<MyCard> unusedCards = handLayout.viewUnusedCards();
        // For all melds
        for (Meld knockerMeld : knockerMelds) {
            Integer index = Finder.findFirstIndexThatFitsInMeld(unusedCards,knockerMeld);
            if(index!=null){
                return new Layoff(unusedCards.get(index),knockerMeld);
            }
        }
        return new Layoff(null, null);
    }

    /* EXTRA */
    public void render(SpriteBatch batch, Style renderStyle) {
        //In case subclass wants some visuals
    }

    /* ACTOR METHODS */
    @Override
    public HandLayout confirmLayout() {
        return getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return automaticLayoff(knockerMelds);
    }
}