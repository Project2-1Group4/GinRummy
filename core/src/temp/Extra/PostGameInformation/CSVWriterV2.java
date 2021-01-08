package temp.Extra.PostGameInformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriterV2 {
    public static void write(List<GameInfo> results, String directory, String fileName){
        File dir = new File(directory);
        dir.mkdirs();
        fileName = directory+fileName;
        /*
        PER ROUND INFO
         */
        try(PrintWriter gameWriter = new PrintWriter(new File(fileName+"_endOfGame.csv"))){
            StringBuilder sb = new StringBuilder();
            sb.append("GameNumber"+',');
            sb.append("RoundNumber"+',');
            sb.append("WinningPlayer"+',');
            for (int i = 0; i < results.get(0).roundInfos.get(0).scores.length; i++) {
                sb.append("ScorePlayer").append(i).append(',');
            }
            for (int i = 0; i < results.get(0).roundInfos.get(0).scores.length; i++) {
                sb.append("DeadwoodPlayer").append(i).append(',');
            }
            for (int i = 0; i < results.get(0).roundInfos.get(0).scores.length; i++) {
                sb.append("CardsInDeadwoodPlayer").append(i).append(',');
            }
            sb.append("FinalRound");
            sb.append('\n');
            sb.append(endOfRoundDecoder(results));
            gameWriter.write(sb.toString());

        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*
        PER TURN INFO
         */
        try(PrintWriter gameWriter = new PrintWriter(new File(fileName+"_gamesInfo.csv"))){
            StringBuilder sb = new StringBuilder();
            sb.append("GameNumber"+',');
            sb.append("RoundNumber"+',');
            sb.append("TurnNumber"+',');
            sb.append("PlayerNumber"+',');
            sb.append("DeadwoodValue"+',');
            sb.append("CardsInDeadwood"+',');
            sb.append("PickTime"+',');
            sb.append("DiscardTime"+',');
            sb.append("KnockingTime"+',');
            sb.append("LayoutConfirmTime"+',');
            sb.append("LayoffTime");
            sb.append('\n');
            for (int i = 0; i < results.size(); i++) {
                sb.append(decodeGame(i,results.get(i)));
            }
            gameWriter.write(sb.toString());
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String endOfRoundDecoder(List<GameInfo> results){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i<results.size();i++){
            for (EndOfRoundInfo roundInfo : results.get(i).roundInfos) {
                sb.append(i).append(',');
                sb.append(roundInfo.csvString());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public static String decodeGame(int game, GameInfo result){
        StringBuilder sb = new StringBuilder();
        List<List<List<int[]>>> d = result.deadwoodOverTurns;
        List<List<List<double[]>>> t = result.timesOverTurns;
        for (int round = 0; round < d.size(); round++) {
            for (int turn = 0; turn < d.get(round).size(); turn++) {
                for (int player = 0; player < d.get(round).get(turn).size(); player++) {
                    sb.append(game).append(',');
                    sb.append(round).append(',');
                    sb.append(turn).append(',');
                    sb.append(player).append(',');
                    sb.append(d.get(round).get(turn).get(player)[0]).append(',');
                    sb.append(d.get(round).get(turn).get(player)[1]).append(',');
                    sb.append(t.get(round).get(turn).get(player)[0]).append(',');
                    sb.append(t.get(round).get(turn).get(player)[1]).append(',');
                    sb.append(t.get(round).get(turn).get(player)[2]).append(',');
                    sb.append(t.get(round).get(turn).get(player)[3]).append(',');
                    sb.append(t.get(round).get(turn).get(player)[4]);
                    sb.append('\n');
                }
            }
        }
        return sb.toString();
    }
}
