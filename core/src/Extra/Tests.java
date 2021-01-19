package Extra;

import GameLogic.Entities.Step;
import GameLogic.Entities.Turn;
import GameLogic.Game;
import GameLogic.Logic.Finder;
import GameLogic.States.GameState;
import GameLogic.States.RoundState;
import GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean printTurns = false; // Print every action taken in cmd
    public static boolean printRounds = false; // Print Start of round and end of rounds info in cmd
    public static boolean printGames = false; // Print when game starts and end of game info in cmd
    public static boolean printPerc = false; // Print after every *nbOfGames* the win percentages

    // If set to true, will write to given folder
    public static boolean saveInterGameInfo = false; // Between games
    public static boolean saveIntraGameInfo = false; // Between rounds (within games)
    public static boolean saveInterTurnInfo = false; // Between turns

    public static void main(String[] args) {
        int nbOfGames = 1; // will play nbOfGames*nbOfPlayers games (alternating who starts)
        Integer seed = null; // Set seed
        GamePlayer[] players = new GamePlayer[]{
                // Add AIs
        };
        String[] playerNames = new String[]{
                // Set AI names (to id them separately)
        };
        String folder = "WrittenResults/"; // Set folder you want to write to (will write at least 2 files if you decide to save something)
        int[][] wins = start(folder, players, playerNames, nbOfGames, seed);

        // Prints wins for every player over all the games
        System.out.println("Game wins: "+Arrays.toString(wins[0]));
        System.out.println("Round wins: "+Arrays.toString(wins[1])); // Excludes ties
    }

    /**
     * Will play numberOfGames*numberOfPlayers games letting each AI be the starting AI.
     * If unseeded, this won't do much but if seeded, will allow to replay all rounds from the same point to allow unbiased testing.
     *
     * @param folder folder the CSV files will be written to
     * @param players AIs that will play
     * @param playerNames names of the AIs for if it saves the info to CSV files
     * @param numberOfGames number of games it plays for every player as first
     * @param seed to allow reproduction of results
     * @return the wins (int[0][player index]=games and int[1][player index]=rounds) for every player
     */
    public static int[][] start(String folder, GamePlayer[] players, String[] playerNames, int numberOfGames, Integer seed){
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
            if(saveInterTurnInfo){ interTurnInfo = new ArrayList<>(); }
            else { interTurnInfo = null;}

            // Running games
            for(int gameNb=0; gameNb <numberOfGames; gameNb++){
                if(printGames){
                    System.out.println("Game "+(gameNb+iteration*numberOfGames));
                }
                if(saveInterTurnInfo){ interTurnInfo.add(new ArrayList<List<double[][]>>()); }
                Game game = new Game(Arrays.asList(players), rd.nextInt());
                runGame(game, saveInterTurnInfo? interTurnInfo.get(interTurnInfo.size()-1):null);
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
            write(folder, playerNames, results, interTurnInfo);
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
    private static void write(String folder, String[] playerNames, List<GameState> results, List<List<List<double[][]>>> interTurnInfo){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerNames.length-1; i++) {
            sb.append(playerNames[i]).append("_");
        }
        sb.append(playerNames[playerNames.length-1]);
        String mainTitle = sb.toString();
        if(saveInterGameInfo){
            CSVWriter.write(CSVWriter.endOfGames(results),folder, mainTitle+"_InterGame");
        }
        if(saveIntraGameInfo){
            CSVWriter.write(CSVWriter.endOfRounds(results),folder, mainTitle+"_IntraGame");
        }
        if(saveInterTurnInfo){
            CSVWriter.write(CSVWriter.endOfTurns(interTurnInfo),folder, mainTitle+"_InterTurn");
        }
    }
    private static void runGame(Game game, List<List<double[][]>> interTurnInfo){
        game.print(printTurns, printRounds, printGames);
        Turn turn =  null;
        int roundIndex = 0;
        int turnIndex = 0;
        int deadwood = 0;
        long interStart = 0;
        long intraStart = 0;
        while(!game.gameEnded()){
            intraStart = System.nanoTime();
            if(saveInterTurnInfo){
                turn = game.turn();
                roundIndex = game.roundNumber()-1;
                turnIndex = game.turnNumber();
                if(game.turn().step == Step.Pick) {
                    interStart = System.nanoTime();
                }

            }
            game.continueGame();
            double stepTime = (System.nanoTime() - intraStart) / (double) 1_000_000_000;
            if(printTurns){
                System.out.println("Took "+stepTime+" seconds.");
            }
            if(saveInterTurnInfo){
                assert turn!=null;
                deadwood = Finder.findBestHandLayout(game.round(roundIndex).cards(turn.playerIndex)).deadwoodValue();
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
