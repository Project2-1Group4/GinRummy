package temp.GamePlayers.GameTreeAIs.MCTS;

// Does MCTS on imperfect information we have
public class MCTSV2 extends MCTS{

    public MCTSV2(int seed){
        super(seed);
    }
    public MCTSV2(){
        super();
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, Knowledge knowledge) {
        mcts(root,knowledge);
    }

    @Override
    protected void rollout(MCTSNode node, Knowledge state) {
        /*
        TODO generate x (<- rolloutsPerNode?) different worlds with given state and rollout each one of them
        bc can't simulate gameplay without knowing where everything is!
         */
    }
}
