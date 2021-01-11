package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Game;
import temp.Graphics.Graphics;
import temp.Graphics.Style;

public class BackgroundRenderer implements Renderer {
    @Override
    public void render(SpriteBatch batch, Style style, Game curState) {
        Sprite bg = style.getBackground();
        float[] size = Graphics.getSize(null, -1, -1, bg.getWidth() / bg.getHeight());
        bg.setSize(-size[0], -size[1]);
        float[] p = Graphics.centerSpriteOn(bg, Gdx.graphics.getWidth() / (float) 2, Gdx.graphics.getHeight() / (float) 2);
        bg.setPosition(p[0], p[1]);
        bg.draw(batch);
    }
}
