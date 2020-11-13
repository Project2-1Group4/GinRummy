package temp.GamePlayers.MouseStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.BasicVisualInfo.CardVisualInfo;
import temp.Graphics.RenderingSpecifics.BasicVisualInfo.ContinueButtonVisualInfo;
import temp.Graphics.RenderingSpecifics.BasicVisualInfo.KnockButtonVisualInfo;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.RenderingSpecifics.BasicVisualInfo.VisualInfo;
import temp.Graphics.Style;

// NEEDS GRAPHICS TO WORK
public class MousePlayer extends GamePlayer {

    private final Graphics gameGraphics;
    private VisualInfo hovered;

    public MousePlayer(Graphics graphics){
        this.gameGraphics = graphics;
    }
    @Override
    public Boolean knockOrContinue() {
        if (hovered!=null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(hovered instanceof KnockButtonVisualInfo){
                return true;
            }
            if(hovered instanceof ContinueButtonVisualInfo){
                return false;
            }
        }
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        if (hovered!=null && hovered instanceof CardVisualInfo && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (((CardVisualInfo)hovered).card == null) {
                return true;
            } else if (((CardVisualInfo)hovered).card.same(topOfDiscard)) {
                return false;
            }
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        if(hovered!=null && hovered instanceof CardVisualInfo && ((CardVisualInfo)hovered).card  != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            for (MyCard myCard : viewHand()) {
                if(myCard.same(((CardVisualInfo)hovered).card )){
                    return ((CardVisualInfo)hovered).card ;
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
    public void render(SpriteBatch batch, Style renderStyle, PlayerRenderer renderer) {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        hovered = renderer.getCard(x,y);
        if(hovered==null){
            hovered = gameGraphics.getHovered(x,y);
        }
        if(hovered!=null){
            hovered.width*=1.2f;
            hovered.height*=1.2f;
        }

        if(kb!=null && kb.isOn(x,y)){
            hovered = kb;
            kb.hovered = true;
        }
        if(cb!=null && cb.isOn(x,y)){
            hovered = cb;
            cb.hovered = true;
        }
        renderKnockOrCont(batch,renderStyle);
    }

    private KnockButtonVisualInfo kb;
    private ContinueButtonVisualInfo cb;
    private void renderKnockOrCont(SpriteBatch batch, Style style){
        if(kb==null){
            kb = new KnockButtonVisualInfo();
        }
        if(cb==null){
            cb = new ContinueButtonVisualInfo();
        }
        kb.render(batch,style);
        cb.render(batch,style);

    }
}