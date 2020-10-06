
package com.mygdx.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GinRummy;

public class MenuScreen implements Screen{

    private GinRummy parent;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;

    public MenuScreen(GinRummy ginRummy){
        parent = ginRummy;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        //table.setDebug(true);
        stage.addActor(table);
        Skin skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));

        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background.jpg"));

        Label label1 = new Label("Player 1: ", skin);
        label1.setFontScale(2.5f);
        final TextField name1 = new TextField("", skin);
        stage.addActor(name1);
        Label label2 = new Label("Player 2: ", skin);
        label2.setFontScale(2.5f);
        final TextField name2 = new TextField("", skin);
        stage.addActor(name2);
        TextButton playButton = new TextButton("PLAY!", skin);

        table.add(label1).fillX().uniformX();
        table.add(name1).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(label2).fillX().uniformX();
        table.add(name2).fillX().uniformX();
        table.row();
        table.add(playButton).fillX().uniformX();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.name1 = name1.getText();
                parent.name2 = name2.getText();
                parent.changeScreen(GinRummy.APPLICATION);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 1f, 1f, 1f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
        batch.begin();
        batch.draw(background, 0,0);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        // TODO Auto-generated method stub
    }
}

