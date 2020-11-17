package temp.GeneticAlgo;

public class GAPlayer {
    public float score;
    public final int index;
    public final TestPlayer player;
    public GAPlayer(int i, TestPlayer player){
        index = i;
        score = 0;
        this.player = player;
    }
}
