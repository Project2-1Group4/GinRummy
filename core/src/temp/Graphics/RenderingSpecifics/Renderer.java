package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.Graphics.Style;

/**
 * Allows to render specific things without cluttering Graphics class
 */
public interface Renderer {
    void render(SpriteBatch batch, Style style, State curState);
}
