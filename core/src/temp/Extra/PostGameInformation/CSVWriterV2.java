package temp.Extra.PostGameInformation;

import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.States.GameState;
import temp.GameLogic.States.RoundState;

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

    // Get CSV format of the end of every round

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
        sb.append(r.winner()).append(',').append(r.turnsPlayed());
        for (int i = 0; i < r.layouts().length; i++) {
            sb.append(',').append(r.points()[i]);
            deadwood.append(',').append(r.layouts()[i].deadwoodValue());
        }
        return sb.toString() + deadwood.toString();
    }

    // Get CSV format of the end of every game

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
    /**
     * Game, Rounds, P0 Score, ..., Pi Score, Winner
     * @param g games
     * @return csv format
     */
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
    /**
     * Rounds, P0 Score, ..., Pi Score, Winner
     * @param g game
     * @return csv format
     */
    private static String onlyEndOfGame(GameState g){
        StringBuilder sb = new StringBuilder();
        sb.append(g.getRoundNumber()).append(',');
        for (int point : g.points) {
            sb.append(point).append(',');
        }
        sb.append(g.winner());
        return sb.toString();
    }
}