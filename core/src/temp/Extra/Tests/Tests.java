package temp.Extra.Tests;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GamePlayers.AIs.basicGreedyTest;
import temp.GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static void main(String[] args) {
        GameLogic logic = new GameLogic(true, true);
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(false),
                new basicGreedyTest(false)
        };
        int games = 1; // Set nb of games
        Integer seed = 0; // Set seed

        List<GameInfo> results = runGames(logic, players, games, seed);
        // Do what you want with results
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
        List<List<List<float[]>>> times = new ArrayList<>();
        List<EndOfRoundInfo> roundInfo = new ArrayList<>();
        for (int i = 0; i < players.length; i++) {
            times.add(new ArrayList<List<float[]>>());
            times.get(i).add(new ArrayList<float[]>());
            deadwood.add(new ArrayList<List<int[]>>());
            deadwood.get(i).add(new ArrayList<int[]>());
        }

        State.StepInTurn previousStep = curState.getStep();
        int previousPlayer = curState.getPlayerIndex();
        int previousRound = curState.getRound();
        State previousState = curState;
        long s = System.currentTimeMillis();
        float[] turnTimes = new float[State.StepInTurn.values().length];
        while (!curState.endOfGame()) {
            curState = logic.update(curState);
            if(previousRound != curState.getRound()){
                assert previousState != curState;

                /*

                If you want to test something every round do it here

                 */
                roundInfo.add(new EndOfRoundInfo(previousState, false));
                for (int i = 0; i < players.length; i++) {
                    times.get(i).add(new ArrayList<float[]>());
                    deadwood.get(i).add(new ArrayList<int[]>());
                }
                previousState = curState;
            }
            else if(previousPlayer != curState.getPlayerIndex()){
                /*

                If you want to test something every turn do it here

                 */
                int[] deadwoodInfo = new int[]{
                        curState.getPlayerStates().get(previousPlayer).getDeadwood(),
                        curState.getPlayerState().getNumberOfCardsInDeadwood()
                };
                if(previousPlayer < times.size()){
                    times.get(previousPlayer).get(previousRound).add(turnTimes);
                    deadwood.get(previousPlayer).get(previousRound).add(deadwoodInfo);
                }

                if(print){
                    System.out.println("Times: "+Arrays.toString(turnTimes));
                    System.out.println("Deadwood value="+deadwoodInfo[0]+" Cards in deadwood="+deadwoodInfo[1]);
                }
                turnTimes = new float[State.StepInTurn.values().length];
                previousPlayer = curState.getPlayerIndex();
            }
            else if(previousStep != curState.getStep()){
                /*

                If you want to test something every step do it here

                 */
                if(previousStep!=null) {
                    turnTimes[previousStep.index] = (System.currentTimeMillis()-s) / (float) 1000;
                }
                previousStep = curState.getStep();
                s = System.currentTimeMillis();
            }
        }
        return new GameInfo(curState, roundInfo, times, deadwood);
    }
}
