
package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.views.EndScreen;
import com.mygdx.game.views.GameScreen;
import com.mygdx.game.views.LoadingScreen;
import com.mygdx.game.views.MenuScreen;

public class GinRummy extends Game {

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private GameScreen mainScreen;
	private EndScreen endScreen;

	public final static int MENU = 0;
	public final static int END = 1;
	public final static int APPLICATION = 2;
	public String name1;
	public String name2;
	public String winner;

	// class to take care of screens
	@Override
	public void create () {
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);
	}
	@Override
	public void render () {
		super.render();
	}

	public void changeScreen(int screen){
		switch(screen){
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case APPLICATION:
				mainScreen = new GameScreen(this);
				this.setScreen(mainScreen);
				break;
			case END:
				if(endScreen == null) endScreen = new EndScreen(this);
				this.setScreen(endScreen);
				break;

		}
	}
	
	@Override
	public void dispose () {
	}
}


