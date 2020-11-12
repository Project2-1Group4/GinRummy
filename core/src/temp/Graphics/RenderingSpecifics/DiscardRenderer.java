package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.CardVisualInfo;
import temp.Graphics.Style;

public class DiscardRenderer implements Renderer {

    public CardVisualInfo visualInfo;
    private float percAwayFromLeft = 0.5f;
    private float percAwayFromBottom = 0.5f;
    private float maxPercOfScreen = 0.2f;

    @Override
    public void render(SpriteBatch batch, Style style, State curState) {
        if (!curState.isDiscardEmpty()) {
            if(visualInfo==null){
                setInfo(style,curState.peekDiscardTop());
            }
            visualInfo.render(batch,style);
            setInfo(style,curState.peekDiscardTop());
        }
    }

    private void setInfo(Style style, MyCard topOfDiscard){
        visualInfo = new CardVisualInfo(topOfDiscard);
        float[] size = Graphics.getDimensions(style.getWidthToHeightCard(), maxPercOfScreen, maxPercOfScreen);
        visualInfo.width = size[0];
        visualInfo.height = size[1];
        visualInfo.pos.set(Gdx.graphics.getWidth() * percAwayFromLeft, Gdx.graphics.getHeight() * percAwayFromBottom);
    }
    public boolean isOn(int x, int y){
        if(visualInfo!=null) {
            return visualInfo.isOn(x, y);
        }
        return false;
    }
}
