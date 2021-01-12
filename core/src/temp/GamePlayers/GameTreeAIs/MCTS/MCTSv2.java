package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.States.RoundState;
import temp.GamePlayers.ForcePlayer;

// Does MCTS on imperfect information we have.
// Rollout is performed on randomly generated perfect info world
public class MCTSv2 extends MCTS{

    public MCTSv2(int seed){
        super(seed);
    }
    public MCTSv2(){
        super();
    }
    @Override
    protected double rollout(RoundState state) {
        RoundState simulation = completeUnknownInformation(state.cards(), state.turn());
        return executeRollout(new ForcePlayer(rd.nextInt()), new ForcePlayer(rd.nextInt()), simulation);
    }
}
