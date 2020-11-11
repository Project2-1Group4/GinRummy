package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MyCard;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.BasicPlayerRenderer;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;

// NEEDS GRAPHICS TO WORK
public class MousePlayer extends GamePlayer {

    public MousePlayer(PlayerRenderer renderer){
        super(renderer);
    }
    public MousePlayer(){
        this(new BasicPlayerRenderer());
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