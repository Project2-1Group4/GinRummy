package temp.GamePlayers.MouseStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.CardVisualInfo;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.VisualInfo;
import temp.Graphics.Style;

// NEEDS GRAPHICS TO WORK
//TODO REDO WHOLE GRAPHICS THING. FUCKED IT UP
public class MousePlayer extends GamePlayer {

    private Graphics gameGraphics;
    private VisualInfo hovered;

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
            if(hovered instanceof KnockButton){
                return true;
            }
            if(hovered instanceof ContinueButton){
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

    private KnockButton kb;
    private ContinueButton cb;
    private void renderKnockOrCont(SpriteBatch batch, Style style){
        if(kb==null){
            kb = new KnockButton();
        }
        if(cb==null){
            cb = new ContinueButton();
        }
        kb.render(batch,style);
        cb.render(batch,style);

    }
}