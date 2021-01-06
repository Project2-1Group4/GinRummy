package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GamePlayers.ForcePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Does MCTS on x amount of created perfect information games
public class MCTSv1 extends MCTS{

    private final int simulations = 100; // Nb of perfect games simulated

    public MCTSv1(int seed){
        super(seed);
    }
    public MCTSv1(){
        super();
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, KnowledgeBase knowledge){
        System.out.println(knowledge);
        for (int i = 0; i < simulations; i++) {
            mcts(root, generateRandomWorld(knowledge));
        }
    }
}