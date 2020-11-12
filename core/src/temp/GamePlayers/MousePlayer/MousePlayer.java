package temp.GamePlayers.MousePlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.CardVisualInfo;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;
import temp.Graphics.Style;

// NEEDS GRAPHICS TO WORK
public class MousePlayer extends GamePlayer {

    public MousePlayer(PlayerRenderer renderer){
        super(renderer);
    }
    public MousePlayer(){
        this(new PlayerRenderer());
    }
    @Override
    public Boolean knockOrContinue() {
        Gdx.input.setInputProcessor(new MouseHandler());
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return null;
    }

    @Override
    public MyCard discardCard() {
        return null;
    }

    @Override
    public void otherPlayerDiscarded(DiscardAction discardAction) {

    }

    @Override
    public void otherPlayerPicked(PickAction pickAction) {

    }

    @Override
    public void render(SpriteBatch batch, Style renderStyle) {
        renderer.init(renderStyle,handLayout);
        CardVisualInfo hovered = renderer.getCard(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY());
        if(hovered!=null){
            hovered.width*=1.2f;
            hovered.height*=1.2f;
        }
        renderer.render(batch,renderStyle,handLayout);
        //TODO render knock and continue buttons
    }
}