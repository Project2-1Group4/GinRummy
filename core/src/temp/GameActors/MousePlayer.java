package temp.GameActors;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import temp.GameLogic.MyCard;

public class MousePlayer extends GameActor {
    public MousePlayer(){

    }
    @Override
    public Boolean knockOrContinue() {
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(boolean deckEmpty, MyCard topOfDiscard) {
        return null;
    }

    @Override
    public MyCard discardCard() {
        return null;
    }
}