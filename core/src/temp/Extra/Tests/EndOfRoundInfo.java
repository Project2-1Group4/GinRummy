package temp.Extra.Tests;

import temp.GameLogic.GameState.PlayerState;
import temp.GameLogic.GameState.State;

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
}
