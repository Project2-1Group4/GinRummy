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

        System.out.println("Final Results: Number of Turns: " + nbOfTurns);
        System.out.println("P1: " + player1);
        System.out.println("P2: " + player2);

        System.out.println("Winner: " + winner);

    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(winner!= null){
            sb.append("Winner: Player ");
            GamePlayer loser;
            int winnerNb;
            if(winner.index == player1.index){
                winnerNb = 1;
                loser = player2;
            }else{
                winnerNb = 2;
                loser = player1;
            }
            sb.append(winnerNb)
                    .append(" index = ")
                    .append(winner.index)
                    .append("\nHand:\n")
                    .append(winnerNb == 1? player1Layout : player2Layout);
            sb.append("\n Loser: Player 2, index = ")
                    .append(loser.index)
                    .append("\nHand:\n")
                    .append(winnerNb == 1? player2Layout : player1Layout);
        }else{
            sb.append("No winner");
        }
        sb.append("\n").append(nbOfTurns).append(" turns");
        return sb.toString();
    }
}
