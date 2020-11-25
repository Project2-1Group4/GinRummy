package temp.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.Graphics.RenderingSpecifics.*;

import java.util.HashMap;
import java.util.Map;

// Coordinates all renderers
public class Graphics {

    private static Graphics instance;

    private final OrthographicCamera camera;
    private final SpriteBatch batch;

    private final Style renderingStyle;
    private final Map<Integer, PlayerRenderer> playerRenderers;
    private Integer currentPlayer = null;
    private final Map<String, Renderer> renderers;

    // To add more graphical information, create new renderer
    // Or if player specific, override GamePlayer.render()
    public Graphics() {
        instance = this;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        renderingStyle = new StyleBuilder().build();

        renderers = new HashMap<>();
        renderers.put("Background", new BackgroundRenderer());
        renderers.put("Card", new CardRenderer());
        renderers.put("Extra", new ExtraRenderer());
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
            for (Map.Entry<String, Renderer> entry : renderers.entrySet()) {
                entry.getValue().render(batch, renderingStyle, curState);
            }
            PlayerRenderer pRenderer = playerRenderers.get(curState.getPlayerNumber());
            if (pRenderer == null) {
                pRenderer = new PlayerRenderer(curState.getPlayer());
                playerRenderers.put(curState.getPlayerNumber(), pRenderer);
            }
            currentPlayer = curState.getPlayerNumber();
            pRenderer.render(batch, renderingStyle);
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

    public PlayerRenderer getPlayerRenderer() {
        return playerRenderers.get(currentPlayer);
    }

    /**
     * Returns what is at the x,y coords on screen (Only takes deck,discard and player cards into account)
     *
     * @param x pos on screen
     * @param y pos on screen
     * @return thing that is being hovered, if nothing then null
     */
    public GameCard getHovered(float x, float y) {
        GameCard c = getPlayerRenderer().getHovered(x, y);
        return c != null ? c : ((CardRenderer) renderers.get("Card")).getHovered(x, y);
    }

    public void move(GameCard card, float x, float y) {
        getPlayerRenderer().move(card, x, y);
    }

    // NEED TO BE MOVED MAYBE
    public static float[] getSize(float[] dimension, float maxW, float maxH, float widthToHeight) {
        float[] max;
        if (dimension == null) {
            max = new float[]{
                    Gdx.graphics.getWidth() * maxW,
                    Gdx.graphics.getHeight() * maxH
            };
        } else {
            max = new float[]{
                    dimension[0] * maxW,
                    dimension[1] * maxH
            };
        }
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

    public static Graphics getInstance() {
        if (instance == null) {
            instance = new Graphics();
        }
        return instance;
    }
}