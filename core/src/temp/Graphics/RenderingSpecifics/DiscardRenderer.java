package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.Graphics.Graphics;
import temp.Graphics.Style;

public class DiscardRenderer implements Renderer {

    private float percAwayFromLeft = 0.5f;
    private float percAwayFromBottom = 0.5f;
    private float maxPercOfScreen = 0.2f;

    @Override
    public void render(SpriteBatch batch, Style style, State curState) {
        if (!curState.isDiscardEmpty()) {
            Sprite s = style.getCardFace(curState.peekDiscardTop());
            float[] size = Graphics.getDimensions(style.getWidthToHeightCard(), maxPercOfScreen, maxPercOfScreen);
            s.setSize(size[0], size[1]);
            float[] p = Graphics.centerSpriteOn(s, Gdx.graphics.getWidth() * percAwayFromLeft, Gdx.graphics.getHeight() * percAwayFromBottom);
            s.setPosition(p[0], p[1]);
            s.draw(batch);
        }
    }
}
