package temp;

import com.badlogic.gdx.ScreenAdapter;
import com.mygdx.game.GinRummy;
import temp.Extra.StringToGamePlayer;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.EndSignal;
import temp.GameLogic.States.GameState;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.Graphics;

import java.util.ArrayList;
import java.util.List;

public class Coordinator extends ScreenAdapter {

    private final Graphics graphics;
    private final Game game;
    private final GinRummy master;


    public Coordinator(GinRummy master) {
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