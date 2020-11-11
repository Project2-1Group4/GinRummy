package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

public class MousePlayer extends GamePlayer {
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

    @Override
    public void render(SpriteBatch batch, Style renderStyle) {
        super.render(batch, renderStyle);
    }
}