package temp.Extra;

import temp.Extra.PostGameInformation.*;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.GameState;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MCTS.MCTSv1;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean print = true;
    private static boolean saveTurnInfo = false;

    public static void main(String[] args) {
        GamePlayer[] players = new GamePlayer[]{
                new MCTSv1(),
                new basicGreedyTest(),
        };
        int games = 1; // Set nb of games
        Integer seed = null; // Set seed

        List<GameState> results = runGames(players, games, seed);

        int[] wins = new int[players.length];
        for (GameState result : results) {
            wins[result.getHighestScoreIndex()]++;
        }
        System.out.println(Arrays.toString(wins)+" wins out of "+games);
        for (int i = 0; i < players.length; i++) {
            System.out.println("Player "+i+" win%: "+(wins[i]/(double)games));
        }
        List<Result> r = results.get(0).toResult();
        int i=1;
        for (Result result : r) {
            System.out.println("\nRound "+i+": "+result.r+"\n");
            i++;
        }

        System.out.println(Result.getScores(r));
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
            System.out.println("Game "+i);
            results.add(runGame(players,rd.nextInt()));
        }
        return results;
    }

    public static GameState runGame(GamePlayer[] players, int seed) {
        Game game = new Game(Arrays.asList(players), seed, print);
        return game.playOutGame();
    }
}
