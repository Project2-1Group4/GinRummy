package Extra;

import GamePlayers.CombinePlayer;
import GamePlayers.GamePlayer;
import GamePlayers.GameTreeAIs.BestFirstMinimaxAI;
import GamePlayers.GameTreeAIs.MCTS.MCTSv1;
import GamePlayers.GameTreeAIs.*;
import GamePlayers.GreedyAIs.basicGreedy;
import GamePlayers.GreedyAIs.meldBuildingGreedy;
import GamePlayers.KeyboardPlayer;
import GamePlayers.MousePlayer.MousePlayer;
import GamePlayers.RandomPlayer;

public class StringToGamePlayer {

    public static GamePlayer getPlayer(String s){
        s = s.toLowerCase();
        if(s.isEmpty()){
            return CombinePlayer.getBaseCombinePlayer();
        }
        else if(has(GameRules.names_basicGreedy,s)){
            return new basicGreedy();
        }
        else if(has(GameRules.names_meldGreedy, s)) {
            return new meldBuildingGreedy();
        }
        else if(has(GameRules.names_minimax, s)){
            return new MinimaxPruningAI();
        }
        else if(has(GameRules.name_best_search, s)) {
            return new BestFirstMinimaxAI();
        }
        else if(has(GameRules.names_keyboard, s)){
            return new KeyboardPlayer();
        }
        else if(has(GameRules.names_mouse, s)){
            return new MousePlayer();
        }
        else if(has(GameRules.names_random, s)){
            return new RandomPlayer();
        }
        else if(has(GameRules.names_MCTS, s)){
            return new MCTSv1();
        }
        return CombinePlayer.getBaseCombinePlayer();
    }

    private static boolean has(String possible, String received){
        String[] options = possible.split(" ");
        for (String option : options) {
            if(option.equals(received)){
                return true;
            }
        }
        return false;
    }
}