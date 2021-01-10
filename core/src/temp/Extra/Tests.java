package temp.Extra;


import temp.Extra.PostGameInformation.CSVWriterV2;
import temp.GameLogic.Game;
import temp.GameLogic.States.GameState;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MCTS.MCTSv1;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.*;

public class Tests {

    public static boolean printTurns = false;
    public static boolean printRounds = true;
    public static boolean printGames = false;

    public static void main(String[] args) {
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(),
                //new MCTSv1(300, 1, 1.4, 10),
                new basicGreedyTest()
        };
        int games = 20; // Set nb of games
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
        CSVWriterV2.write(CSVWriterV2.endOfRounds(results), "Results/test/", "test1");
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
            if(printGames){
                System.out.println("Game "+i);
            }
            results.add(runGame(players,rd.nextInt()));
        }
        return results;
    }

    public static GameState runGame(GamePlayer[] players, int seed){
        Game game = new Game(Arrays.asList(players), seed);
        game.print(printTurns, printRounds, printGames);
        while(!game.gameEnded()){
            game.continueGame();
        }
        return game.playOutGame();
    }
}
