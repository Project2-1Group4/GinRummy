package temp.GamePlayers.MousePlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.CardVisualInfo;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;
import temp.Graphics.Style;

// NEEDS GRAPHICS TO WORK
public class MousePlayer extends GamePlayer {

    private Graphics gameGraphics;
    private CardVisualInfo hovered;

    public MousePlayer(Graphics graphics, PlayerRenderer renderer){
        super(renderer);
        this.gameGraphics = graphics;
    }
    public MousePlayer(Graphics graphics){
        this(graphics,new PlayerRenderer());
    }
    @Override
    public Boolean knockOrContinue() {
        if (hovered!=null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            //TODO if on knock button return true
            // else if on continue button return false
        }
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        if (hovered!=null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hovered.card == null) {
                return true;
            } else if (hovered.card.same(topOfDiscard)) {
                return false;
            }
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        if(hovered!=null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            for (MyCard myCard : viewHand()) {
                if(myCard.same(hovered.card)){
                    return hovered.card;
                }
            }
        }
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
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        hovered = renderer.getCard(x,y);
        if(hovered==null){
            hovered = gameGraphics.getCard(x,y);
        }
        if(hovered!=null){
            hovered.width*=1.2f;
            hovered.height*=1.2f;
        }
        renderer.render(batch,renderStyle,handLayout);
        //TODO render knock and continue buttons
    }
}