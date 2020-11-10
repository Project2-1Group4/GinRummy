package temp.GameActors;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;

import java.util.List;

public class MousePlayer extends GameActor {
    public MousePlayer(){

    }

    @Override
    public KnockAction knockOrContinue(List<KnockAction> actions) {
        return null;
    }

    @Override
    public PickAction pickDeckOrDiscard(List<PickAction> actions) {
        return null;
    }

    @Override
    public DiscardAction discardCard(List<DiscardAction> actions) {
        return null;
    }
}