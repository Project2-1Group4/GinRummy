package temp.Extra.Tests;

import temp.GameLogic.GameState.PlayerState;
import temp.GameLogic.GameState.State;

import java.util.List;

public class EndOfRoundInfo {
    public final Integer winner;
    public final int[] scores;
    public final int[] deadwoodValues;
    public final int[] numberOfCardsInDeadwood;

    public EndOfRoundInfo(State state){
        winner = state.getWinner();
        scores = state.getScores();
        List<PlayerState> stateList = state.getPlayerStates();
        deadwoodValues = new int[stateList.size()];
        numberOfCardsInDeadwood = new int[stateList.size()];
        for (int i = 0; i < stateList.size(); i++) {
            deadwoodValues[i] = stateList.get(i).getDeadwood();
            numberOfCardsInDeadwood[i] = stateList.get(i).getNumberOfCardsInDeadwood();
        }
    }
}
