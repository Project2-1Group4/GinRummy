package temp.Extra.GA;

import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GamePlayers.GamePlayer;

// Modify based on what you wanna measure
public class Result {
    public final GamePlayer player1;
    public final GamePlayer player2;
    public final GamePlayer winner;
    public final HandLayout player1Layout;
    public final HandLayout player2Layout;
    public final int nbOfTurns;

    public Result(GamePlayer player1, GamePlayer player2, GamePlayer winner,
                  HandLayout player1Layout, HandLayout player2Layout, int nbOfTurns) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.player1Layout = player1Layout;
        this.player2Layout = player2Layout;
        this.nbOfTurns = nbOfTurns;

    }
}
