package temp.Extra.Tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {

    public static void write(List<GameInfo> results){
        try(PrintWriter gamewriter = new PrintWriter(new File("game_info.csv"))){

            StringBuilder sbGame = new StringBuilder();
            sbGame.append("RoundNumber");
            sbGame.append(',');
            sbGame.append("FinalRound");
            sbGame.append(',');
            sbGame.append("WinningPlayer");
            sbGame.append(',');
            sbGame.append("ScorePlayer0");
            sbGame.append(',');
            sbGame.append("ScorePlayer1");
            sbGame.append(',');
            sbGame.append("DeadWoodValueP0");
            sbGame.append(',');
            sbGame.append("DeadWoodValueP1");
            sbGame.append(',');
            sbGame.append("CardsInDeadwoodP0");
            sbGame.append(',');
            sbGame.append("CardsInDeadwoodP1");
            sbGame.append('\n');

            sbGame.append(endGameDecoder(results));

            gamewriter.write(sbGame.toString());
            System.out.println("done with the games");

        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(PrintWriter roundwriter = new PrintWriter(new File("round_info.csv"))){
            StringBuilder sb = new StringBuilder();

            sb.append("Winner");
            sb.append(',');
            sb.append("ScorePlayer0");
            sb.append(',');
            sb.append("ScorePlayer1");
            sb.append(',');
            sb.append("DeadwoodPlayer0");
            sb.append(',');
            sb.append("DeadwoodPlayer1");
            sb.append(',');
            sb.append("CardsInDeadwoodP0");
            sb.append(',');
            sb.append("CardsInDeadwoodP1");
            sb.append('\n');

            sb.append(roundDecoder(results));

            roundwriter.write(sb.toString());

            System.out.println("done with the rounds");

        } catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(PrintWriter deadwoodAndTime = new PrintWriter(new File("deadwood_info.csv"))){
            StringBuilder sb = new StringBuilder();
            /*
            In this case there's not really much order to the data
            It's just gonna be used for a histogram to see how the hand improves over time
            So that's why there's no labels
             */

            sb.append(deadwoodDecoder(results));
            deadwoodAndTime.write(sb.toString());

            System.out.println("done with the deadwood and time");

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
            sb.append(info.roundInfos.get(info.roundInfos.size()-1).csvString());
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
                sb.append(roundEnd.csvString());
                sb.append('\n');

            }

        }

        return sb.toString();
    }

    // I have to actually think about this one, so I'll leave it for tomorrow properly
    public static String deadwoodDecoder(List<GameInfo> results){
        StringBuilder sb = new StringBuilder();

        // First I'll store all of the information of player 1
        // I go through the list once, first getting all of the info of the deadwood
        // And then all of the info of the time

        // And then once that's done go through the list again getting all of the info of player 2
        sb.append("Player 1 Deadwood: \n");
        for(GameInfo info: results){
            List<List<int[]>> p1DeadInfo = info.deadwoodOverTurns.get(0);
            sb.append(listOfListIntDecoder(p1DeadInfo));
        }

        sb.append("Player 1 Time: \n");

        for(GameInfo info: results){
            List<List<double[]>> p1TimeInfo = info.timesOverTurns.get(0);
            sb.append(listOfListDoubleDecoder(p1TimeInfo));
        }



        // Putting a line of separation between the two players
        sb.append("\n");

        sb.append("Player 2: \n");

        sb.append("Player 2 Deadwood: \n");
        for(GameInfo info: results){
            List<List<int[]>> p2DeadInfo = info.deadwoodOverTurns.get(1);
            sb.append(listOfListIntDecoder(p2DeadInfo));
        }

        sb.append("Player 2 Time: \n");

        for(GameInfo info: results){
            List<List<double[]>> p2TimeInfo = info.timesOverTurns.get(1);
            sb.append(listOfListDoubleDecoder(p2TimeInfo));
        }

        return sb.toString();
    }

    public static String listOfListIntDecoder(List<List<int[]>> listOfList){

        StringBuilder sb = new StringBuilder();
        for(List<int[]> roundRes : listOfList){

            // Then for each round we look at each turn
            for(int[] turns: roundRes){
                // Data is organized in this way:
                // int[0] = deadwood value, int[1] = nb of cards in deadwood
                // In this method we're only going to focus on the deadwood value
                // That way we can see how it changes as the game goes on

                sb.append(turns[0]);
                sb.append(",");

            }
            // Done to remove the last comma
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");

        }

        return sb.toString();
    }

    public static String listOfListDoubleDecoder(List<List<double[]>> listOfList){

        StringBuilder sb = new StringBuilder();
        for(List<double[]> roundRes : listOfList){

            // Then for each round we look at each turn
            for(double[] turns: roundRes){
                // Data is structured like this:
                //  float[0] = time for picking,
                //  float[1] = time for discarding,
                //  float[2] = time for knocking,
                //  float[3] = time for layout confirmation,
                //  float[4] = time of layoff

                // So what we'll do is just get the sum of all of the times
                // It's not as accurate, as you lose whether a certain moment is a bottleneck or not
                // But it makes it a lot easier to read the information

                double totalTime = 0;
                for(double doub : turns){
                    totalTime+=doub;
                }

                sb.append(totalTime);
                sb.append(",");

            }
            // Done to remove the last comma
            sb.deleteCharAt(sb.length()-1);

            sb.append("\n");
        }

        return sb.toString();
    }
}
