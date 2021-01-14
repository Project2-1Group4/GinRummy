package Extra.PostGameInformation;

import GameLogic.Entities.MyCard;
import GameLogic.States.RoundState;

import java.util.Arrays;
import java.util.List;

public class EndOfRoundInfo {
    public final int round;
    public final Integer winner;
    public final boolean finalRound;
    public final int[] scores;
    public final int[] deadwoodValues;
    public final int[] numberOfCardsInDeadwood;

    public EndOfRoundInfo(RoundState state,int roundNumber, boolean finalRound){
        this.round = roundNumber;
        this.finalRound = finalRound;
        winner = state.winner();
        scores = state.points();
        List<List<MyCard>> stateList = state.allPlayerCards();
        deadwoodValues = new int[stateList.size()];
        numberOfCardsInDeadwood = new int[stateList.size()];
        for (int i = 0; i < state.layouts().length; i++) {
            deadwoodValues[i] = state.layouts()[i].deadwoodValue();
            numberOfCardsInDeadwood[i] = state.layouts()[i].cardsInDeadwood();
        }
    }

    /**
     * @return R = round, F = is final round, S = scores,
     * DV = deadwood values, D# = nb of cards in deadwood
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("R ")
                .append(round)
                .append(" F ")
                .append(finalRound)
                .append(" Winner ").append(winner);
        sb.append("\nS ").append(Arrays.toString(scores));
        sb.append("\nDV ").append(Arrays.toString(deadwoodValues));
        sb.append("\nD# ").append(Arrays.toString(numberOfCardsInDeadwood));
        return sb.toString();

    }

    /*
    I modified the toString method to be a bit simpler, as I wanted to just use a version that would work for the CSV file at the end
    It's also why it has the less clear format, where everything is just separated by a comma

    The idea of the format is this:
    -Round
    -Final Round (which is a boolean)
    -Winner
    -Score player 0
    -Score player 1
    -Deadwood player 0
    -Deadwood player 1
    -Number of cards in deadwood p0
    -Number of cards in deadwood p1
     */

    public String csvString() {
        StringBuilder s = new StringBuilder();
        s.append(round).append(',');
        s.append(winner!=null? winner:"Null").append(',');
        for (int score : scores) {
            s.append(score).append(',');
        }
        for (int deadwoodValue : deadwoodValues) {
            s.append(deadwoodValue).append(',');
        }
        for (int i : numberOfCardsInDeadwood) {
            s.append(i).append(',');
        }
        s.append(finalRound);
        return s.toString();
    }
}
