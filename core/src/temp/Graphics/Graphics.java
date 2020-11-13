package temp.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.Graphics.RenderingSpecifics.*;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.RenderingSpecifics.BasicVisualInfo.VisualInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Coordinates all renderers
public class Graphics {

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Style renderingStyle;
    private DeckRenderer deckRenderer;
    private DiscardRenderer discardRenderer;
    private Map<Integer, PlayerRenderer> playerRenderers;
    private List<Renderer> renderers;

    // To add more graphical information, create new renderer
    // Or if player specific, override GamePlayer.render()
    public Graphics() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        renderingStyle = new StyleBuilder().build();

        deckRenderer = new DeckRenderer();
        discardRenderer = new DiscardRenderer();
        renderers = new ArrayList<>();
        renderers.add(new BackgroundRenderer());
        renderers.add(new ExtraRenderer());
        playerRenderers = new HashMap<>();
    }

    /**
     * No need to modify this because all "real" rendering is done in other classes
     *
     * @param curState current game state
     */
    public void render(State curState) {
        Gdx.gl.glClearColor(0, 0, 0, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        if (curState != null) {
            for (Renderer renderer : renderers) {
                renderer.render(batch, renderingStyle, curState);
            }
            deckRenderer.render(batch,renderingStyle,curState);
            discardRenderer.render(batch,renderingStyle,curState);
            PlayerRenderer pRenderer = playerRenderers.get(curState.getPlayerNumber());
            if(pRenderer==null){
                pRenderer = new PlayerRenderer();
                playerRenderers.put(curState.getPlayerNumber(),pRenderer);
            }
            pRenderer.render(batch,renderingStyle,curState.getPlayer());
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

    /**
     * Returns what is at the x,y coords on screen (Only takes deck,discard and player cards into account)
     *
     * @param x pos on screen
     * @param y pos on screen
     * @return thing that is being hovered, if nothing then null
     */
    public VisualInfo getHovered(int x, int y){
        if(deckRenderer.isOn(x,y)){
            return deckRenderer.visualInfo;
        }else{
            if(discardRenderer.isOn(x,y)){
                return discardRenderer.visualInfo;
            }
        }
        return null;
    }

    // NEED TO BE MOVED MAYBE
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

    public static float[] centerSpriteOn(Sprite sprite, float x, float y) {
        float w = sprite.getWidth();
        float h = sprite.getHeight();
        return new float[]{
                x - w / 2,
                y - h / 2
        };
    }
}