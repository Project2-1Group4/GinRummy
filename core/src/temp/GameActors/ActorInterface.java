package temp.GameActors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

import java.util.List;

/**
 * Can be moved to actor (maybe should because it's kinda useless)
 */
public interface ActorInterface {

    KnockAction knockOrContinue(List<KnockAction> actions);

    PickAction pickDeckOrDiscard(List<PickAction> actions);

    DiscardAction discardCard(List<DiscardAction> actions);

    LayoutConfirmationAction confirmLayout(List<LayoutConfirmationAction> actions);

    LayoffAction layOff(List<LayoffAction> actions);

}
