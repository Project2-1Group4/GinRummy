package Extra;

import Extra.PostGameInformation.CSVWriterV2;
import GameLogic.Entities.Step;
import GameLogic.Entities.Turn;
import GameLogic.Game;
import GameLogic.Logic.Finder;
import GameLogic.States.GameState;
import GameLogic.States.RoundState;
import GamePlayers.GamePlayer;
import GamePlayers.GameTreeAIs.MCTS.MCTSv1;
import GamePlayers.GreedyAIs.basicGreedyTest;
import GamePlayers.RandomPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean printTurns = false; // Print every action taken in cmd
    public static boolean printRounds = false; // Print Start of round and end of rounds info in cmd
    public static boolean printGames = true; // Print when game starts and end of game info in cmd
    public static boolean printPerc = false;

    public static boolean saveInterGameInfo = true; // Between games
    public static boolean saveIntraGameInfo = true; // Between rounds (within rounds)
    public static boolean saveInterTurnInfo = true; // Between turns
    public static boolean saveIntraTurnInfo = false; // Between steps TODO doesnt work

    public static void main(String[] args) {

        int nbOfGames = 25; // will play nbOfGames*nbOfPlayers games (alternating who starts)
        Integer seed = 3; // Set seed

        // 1.4, 1.6, 3
        int[] values = new int[]{
                1
        };
        String val = "ResultsToAnalyze/MCTS/greedy_rollout";
        for (int value : values) {
            MCTSv1 b = new MCTSv1(0);
            // Base values are 10, null, 200, 1, 1
            b.set(10, null, 200, 1, 1);
            GamePlayer[] players = new GamePlayer[]{
                    b,
                    new basicGreedyTest()
                    //new RandomPlayer()
            };
            String[] playerNames = new String[]{
                    "MCTSv1",
                    "basicGreedy"
            };
            String folder = val+value+"/";
            int[][] r = start(folder, players, playerNames, nbOfGames, seed);
            int totalGames=0;
            int totalRounds=0;
            for (int i = 0; i < r[0].length; i++) {
                totalGames+= r[0][i];
                totalRounds+= r[1][i];
            }
            System.out.println("MCTSv1 with "+val+" at "+value);
            System.out.println("Game wins: "+r[0][0]+" out of "+totalGames);
            System.out.println("Round wins: "+r[1][0]+" out of "+totalRounds+" ("+(r[1][0]/(double)totalRounds)+"%)");
        }

    }

    public static int[][] start(String folder, GamePlayer[] players, String[] playerNames, int numberOfGames, Integer seed){
        // wins[0] = games, wins[1] = rounds
        int[][] wins = new int[2][players.length];
        int nbOfPlayers = players.length;
        for (int iteration = 0; iteration < players.length; iteration++) {
            Random rd;
            if(seed!=null) { rd = new Random(seed); }
            else { rd = new Random(); }

            int[] gameWins = new int[players.length];
            int[] roundWins = new int[players.length];
            // List init
            List<GameState> results = new ArrayList<>();
            List<List<List<double[][]>>> interTurnInfo;
            List<List<List<double[][][]>>> intraTurnInfo;
            if(saveInterTurnInfo){ interTurnInfo = new ArrayList<>(); }
            else { interTurnInfo = null;}
            if(saveIntraTurnInfo){ intraTurnInfo = new ArrayList<>();}
            else { intraTurnInfo = null;}

            // Running games
            for(int gameNb=0; gameNb <numberOfGames; gameNb++){
                if(printGames){
                    System.out.println("Game "+(gameNb+iteration*numberOfGames));
                }
                if(saveInterTurnInfo){ interTurnInfo.add(new ArrayList<List<double[][]>>()); }
                if(saveIntraTurnInfo){ intraTurnInfo.add(new ArrayList<List<double[][][]>>());}
                Game game = new Game(Arrays.asList(players), rd.nextInt());
                runGame(game, saveInterTurnInfo? interTurnInfo.get(interTurnInfo.size()-1):null, saveIntraTurnInfo? intraTurnInfo.get(intraTurnInfo.size()-1):null);
                results.add(game.gameState);
                for (RoundState round : game.gameState.rounds) {
                    if(round.winner()!=null) {
                        roundWins[round.winner()]++;
                    }
                }
            }
            for (GameState result : results) {
                gameWins[result.getHighestScoreIndex()]++;
            }
            for (int i = 0; i < wins[0].length; i++) {
                int indexAfterIteration = (i-iteration)%nbOfPlayers;
                if (indexAfterIteration < 0) {
                    indexAfterIteration += nbOfPlayers;
                }
                wins[0][i]+=gameWins[indexAfterIteration];
                wins[1][i]+=roundWins[indexAfterIteration];
            }
            if(printPerc) {
                printWinPercentage(gameWins, roundWins, playerNames);
            }
            write(folder, playerNames, results, interTurnInfo, intraTurnInfo);
            shuffleForward(players, playerNames);
        }
        return wins;
    }

    private static void shuffleForward(GamePlayer[] p, String[] n){
        GamePlayer p1 = p[0];
        System.arraycopy(p, 1, p, 0, p.length - 1);
        p[p.length-1] = p1;
        String n1 = n[0];
        System.arraycopy(n, 1, n, 0, n.length-1);
        n[n.length-1] = n1;
    }
    private static void printWinPercentage(int[] gameWins, int[] roundWins, String[] playerNames){
        int games =0;
        int rounds=0;
        for (int gameWin : gameWins) {
            games+=gameWin;
        }
        for (int roundWin : roundWins) {
            rounds+=roundWin;
        }
        System.out.println(Arrays.toString(gameWins)+" game wins out of "+games);
        System.out.println(Arrays.toString(roundWins)+" round  wins out of "+rounds);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerNames.length; i++) {
            System.out.println(playerNames[i]+" game win%: "+(gameWins[i]/(double)games));
            sb.append(playerNames[i]).append(" round win%: ").append(roundWins[i] / (double) rounds).append("\n");
        }
        System.out.println(sb.toString());
    }
    private static void write(String folder, String[] playerNames, List<GameState> results, List<List<List<double[][]>>> interTurnInfo, List<List<List<double[][][]>>> intraTurnInfo){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerNames.length-1; i++) {
            sb.append(playerNames[i]).append("_");
        }
        sb.append(playerNames[playerNames.length-1]);
        String mainTitle = sb.toString();
        if(saveInterGameInfo){
            CSVWriterV2.write(CSVWriterV2.endOfGames(results),folder, mainTitle+"_InterGame");
        }
        if(saveIntraGameInfo){
            CSVWriterV2.write(CSVWriterV2.endOfRounds(results),folder, mainTitle+"_IntraGame");
        }
        if(saveInterTurnInfo){
            CSVWriterV2.write(CSVWriterV2.endOfTurns(interTurnInfo),folder, mainTitle+"_InterTurn");
        }
        if(saveIntraTurnInfo){
            CSVWriterV2.write(CSVWriterV2.endOfSteps(intraTurnInfo),folder, mainTitle+"_IntraTurn");
        }
    }

    public static void runGame(Game game, List<List<double[][]>> interTurnInfo, List<List<double[][][]>> intraTurnInfo ){
        game.print(printTurns, printRounds, printGames);
        Turn turn =  null;
        int roundIndex = 0;
        int turnIndex = 0;
        int deadwood = 0;
        long interStart = 0;
        long intraStart = 0;
        while(!game.gameEnded()){
            intraStart = System.nanoTime();
            if(saveIntraTurnInfo || saveInterTurnInfo){
                turn = game.turn();
                roundIndex = game.roundNumber()-1;
                turnIndex = game.turnNumber();
                if(saveInterTurnInfo) {
                    if(game.turn().step == Step.Pick){
                        interStart = System.nanoTime();
                    }
                }
            }
            game.continueGame();
            double stepTime = (System.nanoTime() - intraStart) / (double) 1_000_000_000;
            if(printTurns){
                System.out.println("Took "+stepTime+" seconds.");
            }
            if(saveIntraTurnInfo || saveInterTurnInfo){
                assert turn!=null;
                deadwood = Finder.findBestHandLayout(game.round(roundIndex).cards(turn.playerIndex)).deadwoodValue();
            }
            if(saveIntraTurnInfo) {
                if(turn.step == Step.Pick || turn.step == Step.Discard || turn.step == Step.KnockOrContinue) {
                    if (intraTurnInfo.size() != roundIndex + 1) {
                        intraTurnInfo.add(new ArrayList<double[][][]>());
                    }
                    if (intraTurnInfo.get(roundIndex).size() != turnIndex + 1) {
                        intraTurnInfo.get(roundIndex).add(new double[game.numberOfPlayers()][3][2]);
                    }

                    //.get(round).get(turn) = double[][][]
                    // where double[player][step][0] = time, double[player][step][1] = deadwood afterwards
                    intraTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][turn.step.index][0] = stepTime;
                    intraTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][turn.step.index][1] = deadwood;
                }
            }
            if(saveInterTurnInfo){
                if(turn.step == Step.KnockOrContinue) {
                    if (interTurnInfo.size() != roundIndex + 1) {
                        interTurnInfo.add(new ArrayList<double[][]>());
                    }
                    if (interTurnInfo.get(roundIndex).size() != turnIndex + 1) {
                        interTurnInfo.get(roundIndex).add(new double[game.numberOfPlayers()][2]);
                    }
                    // .get(round).get(turn) = double[][]
                    // where double[player][0] = time, double[player][1] = deadwood
                    double turnTime = (System.nanoTime() - interStart) / (double)  1_000_000_000;
                    interTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][0] = turnTime;
                    interTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][1] = deadwood;
                }
            }
        }
    }
}
