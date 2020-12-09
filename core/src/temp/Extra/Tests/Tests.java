package temp.Extra.Tests;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MinimaxPruningAI;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean print = false;

    public static void main(String[] args) {
        GameLogic logic = new GameLogic(true, true);
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(),
                new basicGreedyTest()
        };
        int games = 5; // Set nb of games
        Integer seed = 0; // Set seed

        List<GameInfo> results = runGames(logic, players, games, seed);
        // Do what you want with results
        CSVWriter.write(results, "Results/basic_greedy_minimax");
    }

    public static List<GameInfo> runGames(GameLogic logic, GamePlayer[] players, int numberOfGames, Integer seed){
        Random rd;
        if(seed!=null) {
            rd = new Random(seed);
        }else {
            rd = new Random();
        }
        List<GameInfo> results = new ArrayList<>();
        for(int i=0; i <numberOfGames; i++){
            //System.out.println("Game "+i);
            results.add(runGame(logic, players,rd.nextInt()));
        }
        return results;
    }

    public static GameInfo runGame(GameLogic logic, GamePlayer[] players, int seed) {
        StateBuilder builder = new StateBuilder()
                .setSeed(seed);
        for (GamePlayer player : players) {
            builder.addPlayer(player);
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
        State prevState;
        int prevRound = curState.getRound();
        int prevPlayer = curState.getPlayerIndex();
        State.StepInTurn prevStep = curState.getStep();
        while (!curState.endOfGame()) {

            prevState = curState;
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
                HandLayout bestLayout = Finder.findBestHandLayout(prevState.getPlayerStates().get(prevPlayer).viewHand());
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
                prevRound = curState.getRound();
            }
        }
        roundInfo.add(new EndOfRoundInfo(curState,true));
        return new GameInfo(roundInfo, times, deadwood);
    }
}
