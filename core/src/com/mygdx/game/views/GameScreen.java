package com.mygdx.game.views;

import Extra.StringToGamePlayer;
import GameLogic.Entities.MyCard;
import GameLogic.Game;
import GameLogic.GameActions.Action;
import GameLogic.GameActions.EndSignal;
import GameLogic.States.GameState;
import GamePlayers.GamePlayer;
import Graphics.Graphics;
import com.badlogic.gdx.ScreenAdapter;
import com.mygdx.game.GinRummy;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {

    private final Graphics graphics;
    private final Game game;
    private final GinRummy master;


    public GameScreen(GinRummy master) {
        this.master = master;
        List<GamePlayer> players = new ArrayList<>();
        players.add(StringToGamePlayer.getPlayer(master.name1));
        players.add(StringToGamePlayer.getPlayer(master.name2));
        game = new Game(players, new GameState(players.size(),MyCard.getBasicDeck(),null));
        graphics = new Graphics();
    }

    @Override
    public void show() {

    }

    public void gameEnded() {
        game.remove();
        if(game.gameWinner()==0){
            master.winner = "Player 1 "+master.name1;
        }
        else if(game.gameWinner()==1){
            master.winner = "Player 2 "+master.name2;
        }
        master.changeScreen(GinRummy.END);
    }

    /**
     * Main logic loop. Everything goes from here.
     *
     * @param delta time difference between previous render loop and now
     */
    @Override
    public void render(float delta) {
        if (game != null) {
            Action a = game.update(delta);
            if(a instanceof EndSignal && ((EndSignal) a).endOfGame){
                gameEnded();
            }
            graphics.render(game);
        }
    }

    @Override
    public void resize(int width, int height) {
        graphics.resize(width, height);
    }
}