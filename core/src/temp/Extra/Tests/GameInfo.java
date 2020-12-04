package temp.Extra.Tests;

import temp.GameLogic.GameState.State;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    // list hierarchy = .get(playerIndex).get(roundIndex).get(turnIndex)
    // int[0] = deadwood value, int[1] = nb of cards in deadwood
    public final List<List<List<int[]>>> deadwoodOverTurns;
    // float[0] = time for picking,
    // float[1] = time for discarding,
    // float[2] = time for knocking,
    // float[3] = time for layout confirmation,
    // float[4] = time of layoff
    public final List<List<List<float[]>>> timesOverTurns;
    // .get(roundIndex)
    public final List<EndOfRoundInfo> roundInfos;
    public final EndOfRoundInfo finalState;

    public GameInfo(State finalState,List<EndOfRoundInfo> roundInfo, List<List<List<float[]>>> times, List<List<List<int[]>>> deadwood){
        this.finalState = new EndOfRoundInfo(finalState, true);
        this.deadwoodOverTurns= deadwood;
        this.timesOverTurns = times;
        this.roundInfos = roundInfo;
    }
}
