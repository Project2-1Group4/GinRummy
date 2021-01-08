package temp.Extra.PostGameInformation;

import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.States.RoundState;

import java.util.Arrays;
import java.util.List;

// Modify based on what you wanna measure
public class Result {
    public final Integer winner;
    public final List<HandLayout> finalLayouts;
    public final int nbOfTurns;
    public final int[] pointsWon;
    public final RoundState r;

    public Result(RoundState round) {
        this.r = round;
        this.finalLayouts = Arrays.asList(round.layouts());
        this.winner = round.winner();
        this.nbOfTurns = round.turnsPlayed();
        this.pointsWon = round.points();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Round ended after ").append(nbOfTurns).append(" turns.\n");
        sb.append("Winner is ").append(winner==null? winner : "Player "+winner).append(" with hand:\n");
        sb.append(winner==null?"null" : finalLayouts.get(winner));
        for (int i = 0; i < finalLayouts.size(); i++) {
            if(winner!=null && !winner.equals(i)){
                sb.append("\nPlayer ").append(i).append(":\n").append(finalLayouts.get(i));
            }
        }
        return sb.toString();
    }

    public String simpleString() {
        return Arrays.toString(pointsWon);
    }
    public static String getScores(List<Result> r){
        StringBuilder sb = new StringBuilder();
        int[] points = new int[r.get(0).pointsWon.length];
        sb.append("Start: ").append(Arrays.toString(points)).append("\n");
        for (int i = 0; i < r.size(); i++) {
            for (int j = 0; j < points.length; j++) {
                points[j]+= r.get(i).pointsWon[j];
            }
            sb.append("Round ").append(i+1).append(": ");
            sb.append(Arrays.toString(points));
            sb.append("\n");
        }
        return sb.toString();
    }
}
