package temp.Extra.GA;

import temp.GamePlayers.GamePlayer;

public class GAPlayer {
    public float score;
    public final int index;
    public final GamePlayer player;

    public GAPlayer(int i, GamePlayer player) {
        index = i;
        score = 0;
        this.player = player;
    }
}
