package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.ActorState;
import temp.GameLogic.GameState.State;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Calculator;
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

    /**
     * To allow all actors to get this feature
     * Automatically creates best melds for given hand
     */
    public HandLayout getBestMelds() {
        return Calculator.getBestMelds(allCards);
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
            Integer index = Calculator.getFirstIndexThatFitsInMeld(unusedCards,knockerMeld);
            if(index!=null){
                return new Layoff(unusedCards.get(index),knockerMeld);
            }
        }
        return new Layoff(null, null);
    }

    @Override
    public HandLayout confirmMelds() {
        return getBestMelds();
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return automaticLayoff(knockerMelds);
    }

    public void render(SpriteBatch batch, Style renderStyle) {
        //In case subclass wants some visuals
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

    public void update(State curState){

        allCards = curState.getActorState().viewHand();
        handLayout = Calculator.getBestMelds(allCards);
    }
}
