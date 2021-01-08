package temp.Extra;

import temp.Extra.PostGameInformation.CSVWriter;
import temp.Extra.PostGameInformation.CSVWriterV2;
import temp.Extra.PostGameInformation.EndOfRoundInfo;
import temp.Extra.PostGameInformation.GameInfo;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.GameState;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MCTS.MCTSv1;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean print = false;
    private static boolean saveTurnInfo = false;

    public static void main(String[] args) {
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(),
                new basicGreedyTest(),
        };
        int games = 5000; // Set nb of games
        Integer seed = 5; // Set seed

        List<GameState> results = runGames(players, games, seed);

        int[] wins = new int[players.length];
        for (GameState result : results) {
            wins[result.getHighestScoreIndex()]++;
        }
        System.out.println(Arrays.toString(wins)+" wins out of "+games);
        //CSVWriterV2.write(results, "Results/Delete this/","n");
    }

    public static List<GameState> runGames(GamePlayer[] players, int numberOfGames, Integer seed){
        Random rd;
        if(seed!=null) {
            rd = new Random(seed);
        }else {
            rd = new Random();
        }
        List<GameState> results = new ArrayList<>();
        for(int i=0; i <numberOfGames; i++){
            if(i%500==0){
                System.out.println("Game "+i);
            }
            results.add(runGame(players,rd.nextInt()));
        }
        return results;
    }

    public static GameState runGame(GamePlayer[] players, int seed) {
        Game game = new Game(Arrays.asList(players.clone()), seed);

        /*List<List<List<int[]>>> deadwood = new ArrayList<>();
        List<List<List<double[]>>> times = new ArrayList<>();
        List<EndOfRoundInfo> roundInfo = new ArrayList<>();

        double[] turnTimes = new double[Step.values().length];
        int prevTurn = -1;
        int prevRound = -1;
        int prevPlayer = curState.getPlayerIndex();
        Step prevStep = curState.getStep();
        boolean newRound = false;
        boolean newTurn = false;
        boolean newPlayer = false;
        boolean newStep = false;
        while (!curState.gameEnded()) {
            long s = System.currentTimeMillis();
            Action a = curState.continueGame();
            double t = (System.currentTimeMillis()-s)/(float) 1000;

            if(prevRound != curState.getRoundNumber()){
                System.out.println("Round "+curState.getRound());

                //If you want to test something every round do it here

                if(saveTurnInfo) {
                    times.add(new ArrayList<List<double[]>>());
                    deadwood.add(new ArrayList<List<int[]>>());
                }
                roundInfo.add(new EndOfRoundInfo(curState.getRound(),curState.getRoundNumber(), false));
                newRound = true;
            }
            if(prevTurn != curState.getTurnNumber()){

                //If you want to test something every turn do it here

                if(saveTurnInfo) {
                    times.get(times.size() - 1).add(new ArrayList<double[]>());
                    deadwood.get(deadwood.size() - 1).add(new ArrayList<int[]>());
                }
                newTurn = true;
            }
            if(prevPlayer != curState.getPlayerIndex()){

                //If you want to test something every new player do it here


                if(saveTurnInfo) {
                    HandLayout bestLayout = Finder.findBestHandLayout(prevState.getPlayerStates().get(prevPlayer).viewHand());
                    int[] deadwoodInfo = new int[]{
                            bestLayout.getDeadwood(),
                            bestLayout.getNumberOfCardsInDeadwood()
                    };
                    while (times.get(prevRound).get(curState.getTurnNumber()).size() <= prevPlayer){
                        times.get(prevRound).get(curState.getTurnNumber()).add(new double[5]);
                        deadwood.get(prevRound).get(curState.getTurnNumber()).add(new int[2]);
                    }
                    times.get(prevRound).get(curState.getTurnNumber()).set(prevPlayer, turnTimes);
                    deadwood.get(prevRound).get(curState.getTurnNumber()).set(prevPlayer, deadwoodInfo);

                    if (print) {
                        System.out.println("Player index " + prevPlayer);
                        System.out.println("Times: " + Arrays.toString(turnTimes));
                        System.out.println("Deadwood value=" + deadwoodInfo[0] + " Cards in deadwood=" + deadwoodInfo[1]);
                    }
                    turnTimes = new double[Step.values().length];

                }
                newPlayer = true;

            }
            if(prevStep != curState.getStep()){

                //If you want to test something every step do it here

                if(saveTurnInfo) {
                    turnTimes[prevStep.index] = t;
                }
                prevStep = curState.getStep();
                newStep = true;
            }

            if(newRound){
                prevRound = curState.getRoundNumber();
                newRound = false;
            }
            if(newTurn){
                prevTurn = curState.getTurnNumber();
                newTurn = false;
            }
            if(newPlayer){
                prevPlayer = curState.getPlayerIndex();
                newPlayer = false;
            }
            if(newStep){
                prevStep = curState.getStep();
                newStep = false;
            }
        }
        roundInfo.add(new EndOfRoundInfo(curState.getRound(), curState.getRoundNumber(),true));*/
        return game.playOutGame();
    }
}
