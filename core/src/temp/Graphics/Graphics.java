package temp.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.Graphics.RenderingSpecifics.*;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.PlayerRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the rendering
 */
public class Graphics {

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Style renderingStyle;
    private List<Renderer> renderers;

    /**
     * To add more graphical information, create new renderer
     */
    public Graphics() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        renderingStyle = new StyleBuilder().build();
        renderers = new ArrayList<>();
        renderers.add(new BackgroundRenderer());
        renderers.add(new DeckRenderer());
        renderers.add(new DiscardRenderer());
        renderers.add(new ExtraRenderer());
    }

    /**
     * No need to modify this because all "real" rendering is done in a Renderer subclass
     *
     * @param curState
     */
    public void render(State curState) {
        Gdx.gl.glClearColor(0, 0, 0, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        if (curState != null) {
            for (Renderer renderer : renderers) {
                renderer.render(batch, renderingStyle, curState);
            }
            curState.getPlayer().render(batch, renderingStyle);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        camera.setToOrtho(false);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        batch.setProjectionMatrix(camera.combined);
        camera.update();
    }

    public static float[] centerSpriteOn(Sprite sprite, float x, float y) {
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        return new float[]{
                x - w / 2,
                y - h / 2
        };
    }

    public static float[] getDimensions(float widthToHeight, float percW, float percH) {
        float[] max = new float[]{
                Gdx.graphics.getWidth() * percW,
                Gdx.graphics.getHeight() * percH
        };
        if (max[1] * widthToHeight > max[0]) {
            max[1] = max[0] / widthToHeight;
        } else {
            max[0] = max[1] * widthToHeight;
        }
        return max;
    }
}