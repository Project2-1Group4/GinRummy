package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.LayoffAction;
import temp.GameLogic.GameActions.LayoutConfirmationAction;
import temp.GameLogic.GameActions.PickAction;
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

    protected int actorIndex;
    protected List<MyCard> allCards;
    protected HandLayout handLayout;

    public GameActor(){
    }

    @Override
    public LayoutConfirmationAction confirmLayout(List<LayoutConfirmationAction> actions) {
        LayoutConfirmationAction bestLayout = null;
        for (LayoutConfirmationAction action : actions) {
            if(bestLayout==null){
                bestLayout=action;
            }
            if(bestLayout.layout.getDeadwood()>action.layout.getDeadwood()){
                bestLayout = action;
            }
        }
        return bestLayout;
    }

    @Override
    public LayoffAction layOff(List<LayoffAction> actions) {
        if(actions.size()!=0){
            return actions.get(0);
        }
        return null;
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

    public void update(HandLayout realLayout, int actorIndex){
        this.actorIndex = actorIndex;
        allCards = realLayout.viewAllCards();
        handLayout = Calculator.getBestMelds(allCards);
    }
}
