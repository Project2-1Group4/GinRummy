
package com.mygdx.game.views;

import cardlogic.Card;
import cardlogic.CardBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GinRummy;
import gameHandling.Gamev2;

public class MainScreen implements Screen{

    private GinRummy parent;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;
    private ModelBatch modelBatch;

    private Label label3;
    private Label label4;
    private CardBatch deck;

    private int roundCount = 1;
    private Card discardFirst;
    private CardBatch discardPile;
    private CardBatch cardsPlayer1;
    private CardBatch cardsPlayer2;


    private TextureAtlas atlas;
    private Material material;
    CameraInputController camController;
    PerspectiveCamera cam3D;

    private Gamev2 game;

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

        TextButton knockButton = new TextButton("KNOCK", skin);
        knockButton.setTransform(true);
        knockButton.setScale(0.75f);
        TextButton passButton = new TextButton("PASS", skin);
        passButton.setTransform(true);
        passButton.setScale(0.75f);
        /*
        Seriously, why is this texture atlas used so many times in so many different parts
        I don't think it needs to be initiallized so many times.
        At the very least it shouldn't affect SetOfCards so directly
         */
        atlas = new TextureAtlas("carddeck.atlas");
        material = new Material(TextureAttribute.createDiffuse(atlas.getTextures().first()),
                new BlendingAttribute(false, 1f), FloatAttribute.createAlphaTest(0.5f));
        cardsNewRound();


        game = new Gamev2(parent.name1, parent.name2, cardsPlayer1, cardsPlayer2, deck, discardPile);

        Label label1 = new Label("Player 1: " + parent.name1, skin);
        Label label2 = new Label( "Player2: " + parent.name2, skin);

        label3 = new Label("Score = " + game.player1.getScore(), skin);
        label4 = new Label("Score = " + game.player2.getScore(), skin);

        stage.addActor(label1);
        label1.setFontScale(2);
        label1.setPosition(70,430);

        stage.addActor(knockButton);
        knockButton.setPosition(400, 220);
        stage.addActor(passButton);
        passButton.setPosition(525, 220);

        stage.addActor(label2);
        label2.setFontScale(2);
        label2.setPosition(70,140);
        stage.addActor(label3);
        label3.setFontScale(1.5f);
        label3.setPosition(420, 265);
        stage.addActor(label4);
        label4.setFontScale(1.5f);
        label4.setPosition(420, 200);

        passButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.print("PASS");

                if(game.player) {
                    for (int j = 0; j < cardsPlayer1.size(); j++) {
                        turnCardBack(cardsPlayer1.getCard(j));
                    }

                    for (int k = 0; k < cardsPlayer2.size(); k++) {
                        turnCardFront(cardsPlayer2.getCard(k));
                    }
                }
                else {
                    for (int j = 0; j < cardsPlayer2.size(); j++) {
                        turnCardBack(cardsPlayer2.getCard(j));
                    }
                    for (int k = 0; k < cardsPlayer1.size(); k++) {
                        turnCardFront(cardsPlayer1.getCard(k));
                    }
                }
                game.player = !game.player;
            }
        });
        knockButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("end of round");
                boolean newRound = game.knock();
                if (newRound) {
                    cardsNewRound();
                    label3.setText("Score = " + game.player1.getScore());
                    label4.setText("Score = " + game.player2.getScore());
                    game.newRound(deck, discardPile, cardsPlayer1, cardsPlayer2);
                } else {
                    parent.changeScreen(GinRummy.END);

                }
            }
        });


        cam3D = new PerspectiveCamera();
        camController = new CameraInputController(cam3D);
        //Gdx.input.setInputProcessor(camController);
    }
    public void cardsNewRound() {
        cardsPlayer1 = new CardBatch(material, false);
        cardsPlayer2 = new CardBatch(material, false);
        deck = new CardBatch(material, true);
        discardPile = new CardBatch(material, false);


        // creating handout cards for player 1
        for(int i = 0; i<10; i++){
            createCard((-5 + i),3, cardsPlayer1);
        }

        // creating handout cards for player 2
        for(int i = 0; i<10; i++){
            createCard((-5 + i),-3, cardsPlayer2);
        }
        if(roundCount%2 != 0) {
            for (int i = 0; i < cardsPlayer1.size(); i++) {
                turnCardBack(cardsPlayer1.getCard(i));
            }

        }
        else{
            for (int i = 0; i < cardsPlayer2.size(); i++) {
                turnCardBack(cardsPlayer2.getCard(i));
            }
        }
        roundCount++;
        // top card of pile
        discardFirst = deck.drawTopCard();
        discardFirst.transform.translate(0.5f,0,0);
        discardFirst.setPointX(0.5f);
        discardFirst.setPointY(0);
        discardPile.addCard(discardFirst);

        for(int i= 0; i < deck.size(); i++){
            deck.getCard(i).transform.translate(-2f,0,0);
            deck.getCard(i).setPointX(-2);
            deck.getCard(i).setPointY(0);
            turnCardBack(deck.getCard(i));
        }}

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

        //tempCurrent.transform.rotate(Vector3.Y, 180 * delta1 );

        // rendering cards in field
        modelBatch.begin(cam3D);

        modelBatch.render(cardsPlayer1);
        modelBatch.render(cardsPlayer2);
        modelBatch.render(discardPile);
        modelBatch.render(deck);

        modelBatch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        camController.update();

        Vector3 touchPoint = new Vector3();
        boolean active = false;

        if (Gdx.input.justTouched()) {
            active = true;
            cam3D.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (this.game.player) {
                for (int i = 0; i < cardsPlayer1.size(); i++) {
                    Card temp = cardsPlayer1.getCard(i);
                    float posX = temp.getPointX();
                    float posY = temp.getPointY();

                    if (touchPoint.x * 7.59 >= temp.getPointX() - 0.5 * CARD_WIDTH && touchPoint.x * 7.59 <= temp.getPointX() + 0.5 * CARD_WIDTH && touchPoint.y * 7.5 >= temp.getPointY() - 0.5 * CARD_HEIGHT && touchPoint.y * 7.5 <= temp.getPointY() + 0.5 * CARD_HEIGHT && active) {
                        System.out.println("Card " + temp.getValue());
                        System.out.println(posX + "and " + posY);

                        setLocation(temp, 0.5f, 0);
                        game.addCardToDiscard(temp);
                        setLocation(cardsPlayer1.getCard(cardsPlayer1.size() - 1), posX, posY);

                        System.out.print("card " + cardsPlayer1.getCard(cardsPlayer1.size() - 1));
                        System.out.print("player 1");

                        for (int j = 0; j < cardsPlayer1.size(); j++) {
                            turnCardBack(cardsPlayer1.getCard(j));
                        }

                        for (int k = 0; k < cardsPlayer2.size(); k++) {
                            turnCardFront(cardsPlayer2.getCard(k));
                        }


                        active = false;

                    }
                }
            } else {
                for (int i = 0; i < cardsPlayer2.size(); i++) {
                    Card temp = cardsPlayer2.getCard(i);
                    float posX = temp.getPointX();
                    float posY = temp.getPointY();
                    if (touchPoint.x * 7.59 >= temp.getPointX() - 0.5 * CARD_WIDTH && touchPoint.x * 7.59 <= temp.getPointX() + 0.5 * CARD_WIDTH && touchPoint.y * 7.5 >= temp.getPointY() - 0.5 * CARD_HEIGHT && touchPoint.y * 7.5 <= temp.getPointY() + 0.5 * CARD_HEIGHT && active) {
                        System.out.println("Card " + temp.getValue());

                        System.out.println(posX + "and " + posY);
                        setLocation(temp, 0.5f, 0);
                        game.addCardToDiscard(temp);
                        System.out.println(posX + "and new" + posY);
                        setLocation(cardsPlayer2.getCard(cardsPlayer2.size() - 1), posX, posY);


                        System.out.print("card " + cardsPlayer2.getCard(cardsPlayer2.size() - 1));
                        System.out.print("player 2");

                        for (int j = 0; j < cardsPlayer2.size(); j++) {
                            turnCardBack(cardsPlayer2.getCard(j));
                        }
                        for (int k = 0; k < cardsPlayer1.size(); k++) {
                            turnCardFront(cardsPlayer1.getCard(k));
                        }


                        active = false;

                    }
                }
            }

            if(touchPoint.x * 7.59 >= deck.getCard(deck.size()-1).getPointX() - 0.5 * CARD_WIDTH && touchPoint.x * 7.59 <= deck.getCard(deck.size()-1).getPointX() + 0.5 * CARD_WIDTH && touchPoint.y * 7.5 >= deck.getCard(deck.size()-1).getPointY() - 0.5 * CARD_HEIGHT && touchPoint.y * 7.5 <= deck.getCard(deck.size()-1).getPointY() + 0.5 * CARD_HEIGHT) {
                Card temp = deck.getCard(deck.size()-1);
                turnCardFront(temp);
                game.drawCard(true);
                if(game.player){
                    setLocation(temp, 5, 3);

                }
                else{
                    setLocation(temp, 5, -3);

                }
                System.out.print("Deck");
            }
            System.out.print(active);
            System.out.print(discardPile);
            if(touchPoint.x * 7.59 >= discardPile.getCard(discardPile.size()-1).getPointX() - 0.5 * CARD_WIDTH && touchPoint.x * 7.59 <= discardPile.getCard(discardPile.size()-1).getPointX() + 0.5 * CARD_WIDTH && touchPoint.y * 7.5 >= discardPile.getCard(discardPile.size()-1).getPointY() - 0.5 * CARD_HEIGHT && touchPoint.y * 7.5 <= discardPile.getCard(discardPile.size()-1).getPointY() + 0.5 * CARD_HEIGHT && active) {

                Card temp = discardPile.getCard(discardPile.size()-1);
                game.drawCard(false);
                if(game.player){
                    setLocation(temp, 5, 3);
                }
                else{
                    setLocation(temp, 5, -3);
                }
                System.out.print("discard");


                // vul aan!
            }
            System.out.println("click coordinates: " + touchPoint.x + "and " + touchPoint.y);

        }
    }
    public void turnCardBack (Card card){
        float z = 0.5f * Math.abs(MathUtils.sinDeg(180));
        card.transform.setToRotation(Vector3.Y, 180);
        card.transform.trn(card.getPointX(), card.getPointY(), z);
    }
    public void turnCardFront (Card card){
        float z = 0.5f * Math.abs(MathUtils.sinDeg(360));
        card.transform.setToRotation(Vector3.Y, 360);
        card.transform.trn(card.getPointX(), card.getPointY(), z);
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

    public void setLocation(Card card, float endX, float endY){
        float moveX = endX - card.getPointX();
        float moveY = endY - card.getPointY();
        System.out.println("moved:" + moveX + "and " + moveY);
        card.transform.translate(moveX, moveY, 0);
        card.setPointX(endX);
        card.setPointY(endY);
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

