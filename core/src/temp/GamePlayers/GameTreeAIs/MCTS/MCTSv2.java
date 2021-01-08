package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.RoundState;

// Does MCTS on imperfect information we have
public class MCTSv2 extends MCTS{

    public MCTSv2(int seed){
        super(seed);
    }
    public MCTSv2(){
        super();
    }
    @Override
    protected double rollout(RoundState state) {
        /*
        TODO generate x (<- rolloutsPerNode?) different worlds with given state and rollout each one of them
        bc can't simulate gameplay without knowing where everything is!
         */
        return 0;
    }
}
