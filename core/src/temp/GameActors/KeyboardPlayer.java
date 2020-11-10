package temp.GameActors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

import java.util.List;

/**
 * Play game with keyboard
 * Space = continue, Esc = knock
 * 1 = deck, 2 = discard pile
 * 1,2,3,4,5,6,7,8,9,0 <- discard card of index-1, and 0 = 10
 */
public class KeyboardPlayer extends GameActor {
    public KeyboardPlayer() {
        super();
    }

    @Override
    public KnockAction knockOrContinue(List<KnockAction> actions) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            for (KnockAction action : actions) {
                if(!action.knock){
                    return action;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            for (KnockAction action : actions) {
                if(action.knock){
                    return action;
                }
            }
        }
        return null;
    }

    @Override
    public PickAction pickDeckOrDiscard(List<PickAction> actions) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            for (PickAction action : actions) {
                if(action.deck){
                    return action;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            for (PickAction action : actions) {
                if(!action.deck){
                    return action;
                }
            }
        }
        return null;
    }

    @Override
    public DiscardAction discardCard(List<DiscardAction> actions) {
        Integer index = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            index = 0;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            index = 1;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            index = 2;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            index = 3;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            index = 4;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            index = 5;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            index = 6;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            index = 7;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            index = 8;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            index = 9;
        }
        if(index!=null){
            for (DiscardAction action : actions) {
                if(action.card.same(allCards.get(index))){
                    return action;
                }
            }
        }
        return null;
    }

    @Override
    public LayoutConfirmationAction confirmLayout(List<LayoutConfirmationAction> actions) {
        return null;
    }

    @Override
    public LayoffAction layOff(List<LayoffAction> actions) {
        return null;
    }
}
