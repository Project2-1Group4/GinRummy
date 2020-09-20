package com.mygdx.game.views;

import cardlogic.Card;
import cardlogic.SetOfCards;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.CardGame;
import com.mygdx.game.GinRummy;

public class MainScreen implements Screen{

    private GinRummy parent;
    private Stage stage;
    private CardGame model;
    private SpriteBatch batch;
    private Texture background;

    SetOfCards deck;
    SetOfCards cardsPlayer1;
    SetOfCards cardsPlayer2;
    Card current;

    SpriteBatch spriteBatch;
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
        model = new CardGame();
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Skin skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));

        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("pokerTable2.jpg"));

        spriteBatch = new SpriteBatch();
        atlas = new TextureAtlas("carddeck.atlas");

        cardsPlayer1 = new SetOfCards(false);
        cardsPlayer2 = new SetOfCards(false);
        deck = new SetOfCards(true);

        // creating handout cards for player 1
        for(int i = 0; i<10; i++){
            createCard((-5 + i),3, cardsPlayer1);
        }

        // creating handout cards for player 2
        for(int i = 0; i<10; i++){
            createCard((-5 + i),-3, cardsPlayer2);
        }

        // top card of pile
        current = deck.drawTopCard();
        current.setPosition(-1,0);
        current.turn();

        cam3D = new PerspectiveCamera();
        camController = new CameraInputController(cam3D);
        Gdx.input.setInputProcessor(camController);
    }


    public void createCard(float x, float y, SetOfCards cards){
        Card card = deck.drawTopCard();
        card.setPosition(x,y);
        cards.addCard(card);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 1f, 1f, 1f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        batch.begin();
        batch.draw(background, 0,0);
        batch.end();

        // rendering cards in field
        spriteBatch.begin();
        for (int i = 0; i< cardsPlayer1.getCardSetSize(); i++){
            cardsPlayer1.getCard(i).draw(spriteBatch);
        }

        for (int i = 0; i< cardsPlayer2.getCardSetSize(); i++){
            cardsPlayer2.getCard(i).draw(spriteBatch);
        }

        current.draw(spriteBatch);

        spriteBatch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        spriteBatch.setProjectionMatrix(cam3D.combined);
        camController.update();
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
        spriteBatch.dispose();
        atlas.dispose();
    }
}