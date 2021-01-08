package temp.Extra.GA;

import temp.Extra.PostGameInformation.Result;
import temp.GamePlayers.GamePlayer;

import java.util.List;
import java.util.Random;

public class FkThisUpAsMuchAsYoudLike {
    /**
     *
     * @param rd random seed to be able to replicate results
     * @param winners array of GamePlayer(s) that got awarded the most points this iteration
     * @return new GamePlayer with updated variables
     */
    public static GamePlayer mutate(Random rd, GamePlayer[] winners) {
        /*

        Create new GamePlayer and do modifications based on winners[]

         */
        return null;
    }

    /**
     * @param results = list of results of every end of game
     * @return float[] of scores GamePlayer1 (indexed at 0) and GamePlayer2 (indexed at 1)
     */
    public static float[] updateScores(List<Result> results) {
        float player1 = 0;
        float player2 = 0;
        /*

        Update player1 (index 0), player2 (index 1) based on performance in results

         */
        return new float[]{
                player1,
                player2
        };
    }

    /**
     * @return true you want to stop, false if you want do another iteration
     */
    public static boolean stopCondition(int iteration) {
        /*

        Set stopping condition

         */
        return iteration >= 500;
    }
}
