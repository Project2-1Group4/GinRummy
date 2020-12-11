package temp;

import temp.GamePlayers.CombinePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MinimaxPruningAI;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;
import temp.GamePlayers.GreedyAIs.meldBuildingGreedy;
import temp.GamePlayers.KeyboardPlayer;
import temp.GamePlayers.MousePlayer.MousePlayer;
import temp.GamePlayers.RandomPlayer;

public class StringToGamePlayer {

    public static GamePlayer getPlayer(String s){
        s = s.toLowerCase();
        if(s.isEmpty()){
             return CombinePlayer.getBaseCombinePlayer();
        }
        else if(has(GameRules.names_basicGreedy,s)){
                return new basicGreedyTest();
        }
        else if(has(GameRules.names_meldGreedy, s)) {
            return new meldBuildingGreedy();
        }
        else if(has(GameRules.names_minimax, s)){
            return new MinimaxPruningAI();
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
