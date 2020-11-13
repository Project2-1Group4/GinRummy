package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.GameRules;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.CardVisualInfo;
import temp.Graphics.Style;

public class DeckRenderer implements Renderer {

    public CardVisualInfo visualInfo;
    private float percAwayFromLeft = 0.2f;
    private float percAwayFromBottom = 0.5f;
    private float maxPercOfScreen = 0.2f;

    @Override
    public void render(SpriteBatch batch, Style style, State curState) {
        if (curState.getDeckSize()!=0) {
            if(visualInfo==null){
                setInfo(style);
            }
            visualInfo.render(batch,style);
            setInfo(style);
        }
    }

    private void setInfo(Style style){
        visualInfo = new CardVisualInfo(null);
        float[] size = Graphics.getDimensions(style.getWidthToHeightCard(), GameRules.cardMaxWidthPercentage, GameRules.cardMaxHeightPercentage);
        visualInfo.width = size[0];
        visualInfo.height = size[1];
        visualInfo.pos.set( Gdx.graphics.getWidth() * percAwayFromLeft, Gdx.graphics.getHeight() * percAwayFromBottom);
    }

    public boolean isOn(int x, int y){
        if(visualInfo!=null) {
            return visualInfo.isOn(x, y);
        }
        return false;
    }
}