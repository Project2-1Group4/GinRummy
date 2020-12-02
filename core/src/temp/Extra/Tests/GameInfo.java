package temp.Extra.Tests;

import temp.GameLogic.GameState.State;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
    // list hierarchy = .get(playerIndex).get(roundIndex).get(turnIndex)
    public final List<List<List<int[]>>> deadwoodOverTurns;
    public final List<List<List<float[]>>> timesOverTurns;
    // .get(roundIndex)
    public final List<EndOfRoundInfo> roundInfos;
    public final State finalState;


    public GameInfo(State finalState,List<EndOfRoundInfo> roundInfo, List<List<List<float[]>>> times, List<List<List<int[]>>> deadwood){
        this.finalState = finalState;
        this.deadwoodOverTurns= deadwood;
        this.timesOverTurns = times;
        this.roundInfos = roundInfo;
    }
}
