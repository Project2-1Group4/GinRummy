
package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.views.EndScreen;
import com.mygdx.game.views.LoadingScreen;
import com.mygdx.game.views.MenuScreen;
import temp.Coordinator;
import temp.GameLogic.GameState.StateBuilder;
import temp.GamePlayers.CombinePlayer;

public class GinRummy extends Game {

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private Coordinator mainScreen;
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
				mainScreen = new Coordinator(this, new StateBuilder()
							.setSeed(11)
							.addPlayer(CombinePlayer.getBaseCombinePlayer())
							.addPlayer(CombinePlayer.getBaseCombinePlayer())
							.build());
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


