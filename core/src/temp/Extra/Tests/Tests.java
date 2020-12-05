package temp.Extra.Tests;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GamePlayers.AIs.basicGreedyTest;
import temp.GamePlayers.AIs.meldBuildingGreedy;
import temp.GamePlayers.GamePlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//TODO prob something with the laying off
public class Tests {

    public static boolean print = false;

    public static void main(String[] args) {
        GameLogic logic = new GameLogic(true, true);
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(true),
                new basicGreedyTest(true)
        };
        int games = 1; // Set nb of games
        Integer seed = 0; // Set seed

        List<GameInfo> results = runGames(logic, players, games, seed);
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results);
        }
        // Do what you want with results

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

    public static boolean print = true;

    public static List<GameInfo> runGames(GameLogic logic, GamePlayer[] players, int numberOfGames, Integer seed){
        Random rd;
        if(seed!=null) {
            rd = new Random(seed);
        }else {
            rd = new Random();
        }
        List<GameInfo> results = new ArrayList<>();
        for(int i=0; i <numberOfGames; i++){
             results.add(runGame(logic, players,rd.nextInt()));
        }
        return results;
    }

    public static GameInfo runGame(GameLogic logic, GamePlayer[] players, int seed) {
        StateBuilder builder = new StateBuilder()
                .setSeed(seed);
        for(int i=0; i<players.length;i++){
            builder.addPlayer(players[i]);
        }
        State curState = builder.build();

        curState = logic.startGame(curState);

        List<List<List<int[]>>> deadwood = new ArrayList<>();
        List<List<List<double[]>>> times = new ArrayList<>();
        List<EndOfRoundInfo> roundInfo = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            times.add(new ArrayList<List<double[]>>());
            times.get(i).add(new ArrayList<double[]>());
            deadwood.add(new ArrayList<List<int[]>>());
            deadwood.get(i).add(new ArrayList<int[]>());
        }

        double[] turnTimes = new double[State.StepInTurn.values().length];
        State prevState = curState;
        int prevRound = curState.getRound();
        int prevPlayer = curState.getPlayerIndex();
        State.StepInTurn prevStep = curState.getStep();
        while (!curState.endOfGame()) {

            long s = System.currentTimeMillis();
            curState = logic.update(curState);
            double t = (System.currentTimeMillis()-s)/(float) 1000;

            if(prevStep != curState.getStep()){
                /*

                If you want to test something every step do it here

                 */
                turnTimes[prevStep.index] = t;
                prevStep = curState.getStep();
            }
            if(prevPlayer != curState.getPlayerIndex()){
                /*

                If you want to test something every turn do it here

                 */
                HandLayout bestLayout = Finder.findBestHandLayout(curState.getPlayerStates().get(prevPlayer).viewHand());
                int[] deadwoodInfo = new int[]{
                        bestLayout.getDeadwood(),
                        bestLayout.getNumberOfCardsInDeadwood()
                };
                if(prevPlayer < times.size()){
                    times.get(prevPlayer).get(prevRound).add(turnTimes);
                    deadwood.get(prevPlayer).get(prevRound).add(deadwoodInfo);
                }

                if(print){
                    System.out.println("Player index "+prevPlayer);
                    System.out.println("Times: "+Arrays.toString(turnTimes));
                    System.out.println("Deadwood value="+deadwoodInfo[0]+" Cards in deadwood="+deadwoodInfo[1]);
                }
                turnTimes = new double[State.StepInTurn.values().length];
                prevPlayer = curState.getPlayerIndex();
            }
            if(prevRound != curState.getRound()){
                /*

                If you want to test something every round do it here

                 */
                roundInfo.add(new EndOfRoundInfo(prevState, false));
                for (int i = 0; i < players.length; i++) {
                    times.get(i).add(new ArrayList<double[]>());
                    deadwood.get(i).add(new ArrayList<int[]>());
                }
                prevState = curState;
                prevRound = curState.getRound();
            }
        }
        roundInfo.add(new EndOfRoundInfo(curState,true));
        return new GameInfo(roundInfo, times, deadwood);
    }
}
