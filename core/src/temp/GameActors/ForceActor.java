package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
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
    public KnockAction knockOrContinue(List<KnockAction> actions) {
        if(actions.size()==0){
            return null;
        }
        for (KnockAction action : actions) {
            if(!action.knock){
                return action;
            }
        }
        return actions.get(0);
    }

    @Override
    public PickAction pickDeckOrDiscard(List<PickAction> actions) {
        if(actions.size()==0){
            return null;
        }
        for (PickAction action : actions) {
            if(action.deck){
                return action;
            }
        }
        return actions.get(0);
    }

    @Override
    public DiscardAction discardCard(List<DiscardAction> actions) {
        if(actions.size()==0){
            return null;
        }
        return actions.get(0);
    }
}
