package Extra.PostGameInformation;

import GameLogic.States.GameState;
import GameLogic.States.RoundState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriterV2 {

    public static void write(String s, String directory, String fileName) {
        File dir = new File(directory);
        dir.mkdirs();
        fileName = directory+fileName;
        try(PrintWriter gameWriter = new PrintWriter(fileName+".csv")){
            gameWriter.write(s);
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeAll(List<GameState> g, String directory, String fileName){
        write(endOfRounds(g), directory, fileName+"_EndOfRounds");
        write(endOfGames(g), directory, fileName+"_EndOfGames");
    }

    // Game, Round, P0 Score, ..., Pi Score, Winner, Turns ,P0 Win, ..., Pi Win, P0 Deadwood, ..., Pi Deadwood

    public static String endOfRounds(List<GameState> g){
        return onlyEndOfRoundsTitleRow(g) + '\n' +
                onlyEndOfRounds(g);
    }

    /**
     * Game, Round, P0 Score, ..., Pi Score, Winner, Turns ,P0 Win, ..., Pi Win, P0 Deadwood, ..., Pi Deadwood
     * Assumes all games have same nb of players.
     * @param g games
     * @return csv format
     */
    private static String onlyEndOfRoundsTitleRow(List<GameState> g){
        return "Game" + ',' + onlyEndOfRoundsTitleRow(g.get(0));
    }
    private static String onlyEndOfRounds(List<GameState> g){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < g.size(); i++) {
            String[] lines = onlyEndOfRounds(g.get(i)).split("\r\n|\r|\n");
            for (String line : lines) {
                sb.append(i).append(',').append(line).append('\n');
            }
        }
        return sb.toString().trim();
    }

    /**
     * Round, P0 Score, ..., Pi Score, Winner, Turns ,P0 Win, ..., Pi Win, P0 Deadwood, ..., Pi Deadwood
     * @param g game to get title csv of
     * @return csv format
     */
    private static String onlyEndOfRoundsTitleRow(GameState g){
        StringBuilder sb = new StringBuilder();
        sb.append("Round").append(',');
        for (int i = 0; i < g.round(0).numberOfPlayers(); i++) {
            sb.append("P").append(i).append(" Score").append(',');
        }
        return sb.toString() + onlyEndOfRoundTitleRow(g.round(0));
    }
    private static String onlyEndOfRounds(GameState g) {
        StringBuilder sb = new StringBuilder();
        int[] scores = new int[g.nbOfPlayers];
        for (int i = 0; i < g.rounds.size(); i++) {
            for (int j = 0; j < scores.length; j++) {
                scores[j] += g.round(i).points()[j];
            }
            sb.append(i).append(',');
            for (int score : scores) {
                sb.append(score).append(',');
            }
            sb.append(onlyEndOfRound(g.round(i))).append('\n');
        }
        return sb.toString().trim();
    }

    /**
     * Winner, Turns ,P0 Win, ..., Pi Win, P0 Deadwood, ..., Pi Deadwood
     * @param r round
     * @return csv format title
     */
    private static String onlyEndOfRoundTitleRow(RoundState r){
        StringBuilder sb = new StringBuilder();
        sb.append("Winner").append(',').append("Turns");
        StringBuilder deadwood = new StringBuilder();
        for (int i = 0; i < r.numberOfPlayers(); i++) {
            sb.append(',').append("P").append(i).append(" Win");
            deadwood.append(',').append("P").append(i).append(" Deadwood");
        }
        sb.append(deadwood.toString());
        return sb.toString();
    }
    private static String onlyEndOfRound(RoundState r){
        StringBuilder sb = new StringBuilder();
        StringBuilder deadwood = new StringBuilder();
        sb.append(r.winner()==null? "":r.winner()).append(',').append(r.turnsPlayed());
        for (int i = 0; i < r.layouts().length; i++) {
            sb.append(',').append(r.points()[i]);
            deadwood.append(',').append(r.layouts()[i].deadwoodValue());
        }
        return sb.toString() + deadwood.toString();
    }

    // Game, Rounds, P0 Score, ..., Pi Score, Winner

    public static String endOfGames(List<GameState> g){
        return onlyEndOfGamesTitleRow(g) + '\n' +
                onlyEndOfGames(g);
    }

    /**
     * Game, Rounds, P0 Score, ..., Pi Score, Winner
     * @param g games
     * @return title for csv format
     */
    private static String onlyEndOfGamesTitleRow(List<GameState> g){
        return "Game" + ',' + onlyEndOfGameTitleRow(g.get(0));
    }
    private static String onlyEndOfGames(List<GameState> g){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < g.size(); i++) {
            sb.append(i).append(',').append(onlyEndOfGame(g.get(i))).append('\n');
        }
        return sb.toString().trim();
    }

    /**
     * Rounds, P0 Score, ..., Pi Score, Winner
     * @param g game
     * @return title for csv format
     */
    private static String onlyEndOfGameTitleRow(GameState g){
        StringBuilder sb = new StringBuilder();
        sb.append("Rounds").append(',');
        for (int i = 0; i < g.nbOfPlayers; i++) {
            sb.append("P").append(i).append(" Score").append(',');
        }
        sb.append("Winner");
        return sb.toString();
    }
    private static String onlyEndOfGame(GameState g){
        StringBuilder sb = new StringBuilder();
        sb.append(g.getRoundNumber()).append(',');
        for (int point : g.points) {
            sb.append(point).append(',');
        }
        sb.append(g.winner());
        return sb.toString();
    }

    // Game, Rounds, Turn, P0 Deadwood, ..., Pi Deadwood ,P0 Time, ..., Pi Time

    public static String endOfTurns(List<List<List<double[][]>>> l){
        return endOfTurnsTitleRow(l) + '\n' +
                onlyEndOfTurnsG(l);
    }

    /**
     * Game, Rounds, Turn, P0 Deadwood, ..., Pi Deadwood ,P0 Time, ..., Pi Time
     * @param l saves
     * @return title for csv format
     */
    private static String endOfTurnsTitleRow(List<List<List<double[][]>>> l){
        return "Game" + ',' + onlyEndOfTurnsTitleRow(l.get(0));
    }
    private static String onlyEndOfTurnsG(List<List<List<double[][]>>> l){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.size(); i++) {
            String[] lines = onlyEndOfTurns(l.get(i)).split("\r\n|\r|\n");
            for (String line : lines) {
                sb.append(i).append(',').append(line).append('\n');
            }
        }
        return sb.toString().trim();
    }

    /**
     * Rounds, Turn, P0 Deadwood, ..., Pi Deadwood ,P0 Time, ..., Pi Time
     * @param l saves
     * @return title for csv format
     */
    private static String onlyEndOfTurnsTitleRow(List<List<double[][]>> l){
        StringBuilder sb = new StringBuilder();
        sb.append("Round").append(',').append("Turn");
        StringBuilder time = new StringBuilder();
        for (int i = 0; i < l.get(0).get(0).length; i++) {
            sb.append(',').append("P").append(i).append(" Deadwood");
            time.append(',').append("P").append(i).append(" Time");
        }
        sb.append(time.toString());
        return sb.toString();
    }
    private static String onlyEndOfTurns(List<List<double[][]>> l){
        StringBuilder sb = new StringBuilder();
        for (int round = 0; round < l.size(); round++) {
            for (int turn = 0; turn < l.get(round).size(); turn++) {
                StringBuilder time = new StringBuilder();
                sb.append(round).append(',').append(turn);
                for (double[] doubles : l.get(round).get(turn)) {
                    sb.append(',').append(doubles[1]);
                    time.append(',').append(doubles[0]);
                }
                sb.append(time.toString()).append('\n');
            }
        }
        return sb.toString().trim();
    }

    // Game, Rounds, Turn,
    // P0 Post-Pick Deadwood, P0 Post-Discard Deadwood, P0 Post-Knock Deadwood, ..., Pi Post-Pick Deadwood, Pi Post-Discard Deadwood, Pi Post-Knock Deadwood,
    // P0 Pick Time, P0 Discard Time, P0 Knock Time, ..., Pi Pick Time, Pi Discard Time, Pi Knock Time

    public static String endOfSteps(List<List<List<double[][][]>>> interTurnInfo) {
        return "";
    }
}