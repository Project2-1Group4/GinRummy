package temp.Extra.Tests;

import temp.Extra.GA.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {

    public static void write(List<GameInfo> results){
        try(PrintWriter gamewriter = new PrintWriter(new File("game_info.csv"))){

            PrintWriter roundwriter = new PrintWriter(new File("round_info.csv"));

            PrintWriter deadwoodAndTime = new PrintWriter(new File("deadwood_info.csv"));

            StringBuilder sb = new StringBuilder();
            sb.append("SolverType");
            sb.append(',');
            sb.append("PieceTypeUsed");
            sb.append(',');
            sb.append("NumberOfPiecesA");
            sb.append(',');
            sb.append("NumberOfPiecesB");
            sb.append(',');
            sb.append("NumberOfPiecesC");
            sb.append(',');
            sb.append("SolutionFound");
            sb.append(',');
            sb.append("ProgramDuration");
            sb.append('\n');

            gamewriter.write(sb.toString());
            System.out.println("done!");

        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*The idea of the format is this:
    -Winner
    -Score player 0
    -Score player 1
    -Deadwood player 0
    -Deadwood player 1
    -Number of cards in deadwood p0
    -Number of cards in deadwood p1
     */
    public static String endGameDecoder(List<GameInfo> results){
        StringBuilder sb = new StringBuilder();
        for(GameInfo info: results){
            // I assume that the last value in the roundInfos is the end of game info
            // TODO: Make sure the last value in the roundInfo is the end of game info
            sb.append(info.roundInfos.get(info.roundInfos.size()-1));
            sb.append('\n');
        }


        return sb.toString();
    }

    public static String roundDecoder(List<GameInfo> results){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<results.size();i++){
            GameInfo info = results.get(i);
            sb.append("Game: " + (i+1));
            sb.append('\n');

            for(EndOfRoundInfo roundEnd : info.roundInfos){
                sb.append(roundEnd.toString());
                sb.append('\n');

            }

        }

        return sb.toString();
    }

    // I have to actually think about this one, so I'll leave it for tomorrow properly
    public static String deadwoodDecoder(List<GameInfo> results){
        StringBuilder sb = new StringBuilder();
        for(GameInfo info: results){


        }

        return sb.toString();
    }
}
