package Graphics.RenderingSpecifics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import GameLogic.Game;
import Graphics.Style;

/**
 * Allows to render specific things without cluttering Graphics class
 */
public interface Renderer {
    void render(SpriteBatch batch, Style style, Game curState);
}
