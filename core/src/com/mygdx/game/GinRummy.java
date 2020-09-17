package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.views.LoadingScreen;
import com.mygdx.game.views.MainScreen;
import com.mygdx.game.views.MenuScreen;

public class GinRummy extends Game {

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private MainScreen mainScreen;


	public final static int MENU = 0;
	public final static int APPLICATION = 2;

	
	@Override
	public void create () {
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);
	}

	@Override
	public void render () {
		//Gdx.gl.glClearColor(1, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	public void changeScreen(int screen){
		switch(screen){
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case APPLICATION:
				if(mainScreen == null) mainScreen = new MainScreen(this);
				this.setScreen(mainScreen);
				break;
		}
	}
	
	@Override
	public void dispose () {

	}
}
