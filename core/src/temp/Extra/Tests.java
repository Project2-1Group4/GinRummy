package temp.Extra;


import temp.Extra.PostGameInformation.CSVWriterV2;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.Turn;
import temp.GameLogic.Game;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.GameState;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MinimaxPruningAI;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {

    public static boolean printTurns = false; // Print every action taken in cmd
    public static boolean printRounds = false; // Print Start of round and end of rounds info in cmd
    public static boolean printGames = false; // Print when game starts and end of game info in cmd

    public static boolean saveInterGameInfo = false; //End of games
    public static boolean saveIntraGameInfo = false; //End of rounds
    public static boolean saveInterTurnInfo = true; //Info after pick,discard and knock together
    public static boolean saveIntraTurnInfo = false; //Info after pick,discard and knock

    public static void main(String[] args) {
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(),
                new MinimaxPruningAI(),
                //new basicGreedyTest()
        };
        String[] playerNames = new String[]{
                "MinimaxPruning",
                "basicGreedy"
        };
        String folder = "Results/test/";
        int nbOfGames = 1; // Set nb of games * nb of players
        Integer seed = 0; // Set seed

        start(folder ,players,playerNames,nbOfGames, seed);
    }

    public static void start(String folder, GamePlayer[] players, String[] playerNames, int numberOfGames, Integer seed){
        Random rd;
        if(seed!=null) {
            rd = new Random(seed);
        }else {
            rd = new Random();
        }
        int nbOfPlayers = players.length;
        for (int iteration = 0; iteration < players.length; iteration++) {
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
                    System.out.println("Game "+(gameNb+iteration*nbOfPlayers));
                }
                if(saveInterTurnInfo){ interTurnInfo.add(new ArrayList<List<double[][]>>()); }
                if(saveIntraTurnInfo){ intraTurnInfo.add(new ArrayList<List<double[][][]>>());}
                Game game = new Game(Arrays.asList(players), rd.nextInt());
                runGame(game, saveInterTurnInfo? interTurnInfo.get(interTurnInfo.size()-1):null, saveIntraTurnInfo? intraTurnInfo.get(intraTurnInfo.size()-1):null);
                results.add(game.gameState);
            }
            printWinPercentage(results, playerNames);
            write(folder, playerNames, results, interTurnInfo, intraTurnInfo);
            shuffleForward(players, playerNames);
        }
    }

    private static void shuffleForward(GamePlayer[] p, String[] n){
        GamePlayer p1 = p[0];
        System.arraycopy(p, 1, p, 0, p.length - 1);
        p[p.length-1] = p1;
        String n1 = n[0];
        System.arraycopy(n, 1, n, 0, n.length-1);
        n[n.length-1] = n1;
    }
    private static void printWinPercentage(List<GameState> results, String[] playerNames){
        int players = results.get(0).nbOfPlayers;
        int[] wins = new int[players];
        for (GameState result : results) {
            wins[result.getHighestScoreIndex()]++;
        }
        System.out.println(Arrays.toString(wins)+" wins out of "+results.size());
        for (int i = 0; i < players; i++) {
            System.out.println(playerNames[i]+" win%: "+(wins[i]/(double)results.size()));
        }
    }
    private static void write(String folder, String[] playerNames, List<GameState> results, List<List<List<double[][]>>> interTurnInfo, List<List<List<double[][][]>>> intraTurnInfo){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerNames.length-1; i++) {
            sb.append(playerNames[i]).append("vs");
        }
        sb.append(playerNames[playerNames.length-1]);
        String mainTitle = sb.toString();
        if(saveInterGameInfo){
            CSVWriterV2.write(CSVWriterV2.endOfGames(results),folder, mainTitle+"_IntraGame");
        }
        if(saveIntraGameInfo){
            CSVWriterV2.write(CSVWriterV2.endOfRounds(results),folder, mainTitle+"_InterGame");
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
            if(saveIntraTurnInfo || saveInterTurnInfo){
                turn = game.turn();
                roundIndex = game.roundNumber()-1;
                turnIndex = game.turnNumber();
                if(saveInterTurnInfo){
                    intraStart = System.currentTimeMillis();
                }
                if(saveIntraTurnInfo) {
                    interStart = System.currentTimeMillis();
                }
            }

            game.continueGame();

            if(saveIntraTurnInfo || saveInterTurnInfo){
                assert turn!=null;
                deadwood = Finder.findBestHandLayout(game.round(roundIndex).cards(turn.playerIndex)).deadwoodValue();
            }
            if(saveIntraTurnInfo) {
                if(intraTurnInfo.size()!=roundIndex+1){
                    intraTurnInfo.add(new ArrayList<double[][][]>());
                }
                if(intraTurnInfo.get(roundIndex).size()!=turnIndex+1){
                    intraTurnInfo.get(roundIndex).add(new double[game.numberOfPlayers()][Step.values().length][2]);
                }

                //.get(round).get(turn) = double[][][]
                // where double[player][step][0] = time, double[player][step][1] = deadwood afterwards
                double stepTime = (System.currentTimeMillis() - interStart) / (double) 1000;
                intraTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][turn.step.index][0] = stepTime;
                intraTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][turn.step.index][1] = deadwood;
            }
            if(saveInterTurnInfo && game.turn().playerIndex!=turn.playerIndex){
                if(interTurnInfo.size()!=roundIndex+1){
                    interTurnInfo.add(new ArrayList<double[][]>());
                }
                if(interTurnInfo.get(roundIndex).size()!=turnIndex+1){
                    interTurnInfo.get(roundIndex).add(new double[game.numberOfPlayers()][2]);
                }
                // .get(round).get(turn) = double[][]
                // where double[player][0] = time, double[player][1] = deadwood
                double turnTime = (System.currentTimeMillis() - intraStart) / (double) 1000;
                interTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][0] = turnTime;
                interTurnInfo.get(roundIndex).get(turnIndex)[turn.playerIndex][1] = deadwood;
            }
        }
    }
}
