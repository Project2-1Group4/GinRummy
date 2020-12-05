package temp.Extra.Tests;

import temp.GameLogic.GameState.PlayerState;
import temp.GameLogic.GameState.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndOfRoundInfo {
    public final int round;
    public final Integer winner;
    public final boolean finalRound;
    public final int[] scores;
    public final int[] deadwoodValues;
    public final int[] numberOfCardsInDeadwood;

    public EndOfRoundInfo(State state, boolean finalRound){
        this.round = state.getRound();
        this.finalRound = finalRound;
        if(!finalRound) {
            winner = state.getRoundWinnerIndex();
        }else{
            winner = state.getWinnerIndex();
        }
        scores = state.getScores();
        List<PlayerState> stateList = state.getPlayerStates();
        deadwoodValues = new int[stateList.size()];
        numberOfCardsInDeadwood = new int[stateList.size()];
        for (int i = 0; i < stateList.size(); i++) {
            deadwoodValues[i] = stateList.get(i).getDeadwood();
            numberOfCardsInDeadwood[i] = stateList.get(i).getNumberOfCardsInDeadwood();
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
        return round + "," +
                finalRound + "," +
                winner + "," +
                scores[0] + "," +
                scores[1] + "," +
                deadwoodValues[0] + "," +
                deadwoodValues[1] + "," +
                numberOfCardsInDeadwood[0] + "," +
                numberOfCardsInDeadwood[1];
    }

    public static void main(String[] args){

        int[] ints = {1,2,3,4,5};
        double[] dubs = {0.5,1.0,1.5,2.0,2.5};

        List<int[]> listInt = new ArrayList<>();
        listInt.add(ints);

        List<List<int[]>> listListInt = new ArrayList<>();
        listListInt.add(listInt);

        System.out.println(CSVWriter.listOfListIntDecoder(listListInt));


    }

}
