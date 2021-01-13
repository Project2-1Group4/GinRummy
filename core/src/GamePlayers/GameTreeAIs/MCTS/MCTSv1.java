package GamePlayers.GameTreeAIs.MCTS;

import GameLogic.Entities.Turn;
import GameLogic.States.CardsInfo;
import GameLogic.States.RoundState;

// Does MCTS for time given on simulations
public class MCTSv1 extends MCTS{

    public int simulations = 10;

    public MCTSv1(Integer seed){
        super(seed);
    }
    public MCTSv1(){
        this(null);
    }

    public void set(int simulations, Double secPerSim, Integer rolloutsPerSim, int rolloutsPerNode, double explorationParam){
        this.simulations = simulations;
        super.set(secPerSim, rolloutsPerSim, rolloutsPerNode, explorationParam);
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge){
        for (int i = 0; i < simulations; i++) {
            RoundState generated = completeUnknownInformation(knowledge, new Turn(step, index));
            MCTSNode generatedRoot = ExpandNode(new MCTSNode(null, null, this), generated);
            mcts(generatedRoot, generated);
            root.merge(generatedRoot);
        }
        //Can check what the best deck pick would be here.
    }
}