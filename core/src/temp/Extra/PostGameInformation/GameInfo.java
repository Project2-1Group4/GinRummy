package temp.Extra.PostGameInformation;

import temp.Extra.PostGameInformation.EndOfRoundInfo;

import java.util.Arrays;
import java.util.List;

public class GameInfo {
    // list hierarchy = .get(roundIndex).get(turnIndex).get(playerIndex)
    // int[0] = deadwood value, int[1] = nb of cards in deadwood
    public final List<List<List<int[]>>> deadwoodOverTurns;
    // float[0] = time for picking,
    // float[1] = time for discarding,
    // float[2] = time for knocking,
    // float[3] = time for layout confirmation,
    // float[4] = time of layoff
    public final List<List<List<double[]>>> timesOverTurns;
    // .get(roundIndex)
    public final List<EndOfRoundInfo> roundInfos;
    public GameInfo(List<EndOfRoundInfo> roundInfo, List<List<List<double[]>>> times, List<List<List<int[]>>> deadwood){
        this.deadwoodOverTurns= deadwood;
        this.timesOverTurns = times;
        this.roundInfos = roundInfo;
    }

    /**
     * @return R = round, P = player, D = deadwood,
     * T = time, I = EndOfRoundInfo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < deadwoodOverTurns.get(0).size(); j++) {
            sb.append("R ").append(j).append("\n");
            for (int i = 0; i < deadwoodOverTurns.size(); i++) {
                sb.append("P ").append(i).append(":");
                StringBuilder deadwood = new StringBuilder();
                StringBuilder time = new StringBuilder();
                assert deadwoodOverTurns.get(i).get(j).size() == timesOverTurns.get(i).get(j).size();
                for (int k = 0; k < deadwoodOverTurns.get(i).get(j).size(); k++) {
                    time.append(" ").append(k).append(Arrays.toString(timesOverTurns.get(i).get(j).get(k)));
                    deadwood.append(" ").append(k).append(Arrays.toString(deadwoodOverTurns.get(i).get(j).get(k)));
                }
                sb.append("\nD ").append(deadwood.toString());
                sb.append("\nT ").append(time.toString());
                sb.append("\n");
            }
            sb.append("I ").append(roundInfos.get(j));
            sb.append("\n\n");
        }
        return sb.toString();
    }
}
