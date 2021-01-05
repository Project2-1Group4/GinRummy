package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Does MCTS on imperfect information we have
public class MCTSV2 extends MCTS{

    @Override
    protected void monteCarloTreeSearch(MCTSNode root, Knowledge knowledge) {
        mcts(root,knowledge);
    }

    @Override
    protected boolean rollOut(Knowledge state) {
        /*
        TODO generate x (<- rolloutsPerNode?) different worlds with given state and rollout each one of them
        bc can't simulate gameplay without knowing where everything is!
         */
        return false;
    }
}
