
package com.mygdx.game.views;

import cardlogic.Card;
import cardlogic.CardBatch;
import cardlogic.SetOfCards;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.CardGame;
import com.mygdx.game.GinRummy;

public class MainScreen implements Screen{

    private GinRummy parent;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;
    private ModelBatch modelBatch;


    private SetOfCards deck;
    //private SetOfCards cardsPlayer1;
   // private SetOfCards cardsPlayer2;
    private Card tempCurrent;
    private CardBatch cardsPlayer1;
    private CardBatch cardsPlayer2;
    private CardBatch current;

    TextureAtlas atlas;

    //OrthographicCamera cam;
    CameraInputController camController;
    PerspectiveCamera cam3D;

    public final static float CARD_WIDTH = 1f;
    public final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;
    public final static float MINIMUM_VIEWPORT_SIZE = 10f;


    public MainScreen(GinRummy ginRummy){
        parent = ginRummy;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

    }

    @Override
    public void show() {
        modelBatch= new ModelBatch();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Skin skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));

        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("pokerTable2.jpg"));

        /*
        Seriously, why is this texture atlas used so many times in so many different parts
        I don't think it needs to be initiallized so many times.
        At the very least it shouldn't affect SetOfCards so directly
         */
        atlas = new TextureAtlas("carddeck.atlas");
        Material material = new Material(TextureAttribute.createDiffuse(atlas.getTextures().first()),
                new BlendingAttribute(false, 1f), FloatAttribute.createAlphaTest(0.5f));
        cardsPlayer1 = new CardBatch(material, false);
        cardsPlayer2 = new CardBatch(material, false);
        /*
        cardsPlayer1 = new SetOfCards(false);
        cardsPlayer2 = new SetOfCards(false);

         */
        deck = new SetOfCards(true);
        current = new CardBatch(material, false);

        // creating handout cards for player 1
        for(int i = 0; i<10; i++){
            createCard((-5 + i),3, cardsPlayer1);
        }

        // creating handout cards for player 2
        for(int i = 0; i<10; i++){
            createCard((-5 + i),-3, cardsPlayer2);
        }

        // top card of pile

        tempCurrent = deck.drawTopCard();
        tempCurrent.transform.translate(-1,0,0);
        tempCurrent.turn();
        current.addCard(tempCurrent);

        cam3D = new PerspectiveCamera();
        camController = new CameraInputController(cam3D);
        Gdx.input.setInputProcessor(camController);
    }


    public void createCard(float x, float y, CardBatch cards){
        Card card = deck.drawTopCard();
        card.transform.translate(x,y,0);
        card.setPointX(x);
        card.setPointY(y);
        cards.addCard(card);
    }

    @Override
    public void render(float delta) {
        final float delta1 = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor( 1f, 1f, 1f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        batch.draw(background,0,0);
        batch.end();

        tempCurrent.transform.rotate(Vector3.Y, 90 * delta1);
        // rendering cards in field
        modelBatch.begin(cam3D);
        /*for (int i = 0; i< cardsPlayer1.getCardSetSize(); i++){
            modelBatch.render(cardsPlayer1.getCard(i));
        }

        for (int i = 0; i< cardsPlayer2.getCardSetSize(); i++){
            modelBatch.render(cardsPlayer2.getCard(i));
        }

         */

        modelBatch.render(cardsPlayer1);
        modelBatch.render(cardsPlayer2);
        modelBatch.render(current);

        modelBatch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        camController.update();

        Vector3 touchPoint = new Vector3();

        if (Gdx.input.justTouched()) {
            cam3D.project(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            Card temp = cardsPlayer1.getCard(0);
            System.out.println("click coordinates: " + touchPoint.x + "and " + touchPoint.y);
            if(touchPoint.x >= temp.getPointX() - 0.5 * CARD_WIDTH && touchPoint.x <= temp.getPointX() + 0.5 * CARD_WIDTH && touchPoint.y >= temp.getPointY() - 0.5 * CARD_HEIGHT && touchPoint.y <= temp.getPointY() + 0.5 * CARD_HEIGHT){
                System.out.println("Yeah it worked!");
            }
            /*for(int i = 0; i< cardsPlayer1.size(); i++){
                Card temp = cardsPlayer1.getCard(i);
                if (temp.getOriginX() touchPoint.x, touchPoint.y) {
                    System.out.print(cardsPlayer1.getCard(i));
            }

            }

             */

        }
    }

    @Override
    public void resize(int width, int height) {
        float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam3D.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);

        cam3D.viewportWidth = width;
        cam3D.viewportHeight = height;
        cam3D.position.set(0, 0, distance);
        cam3D.lookAt(0, 0, 0);
        cam3D.update();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        atlas.dispose();

    }


}

