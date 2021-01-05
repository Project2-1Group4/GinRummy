package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;

import java.util.ArrayList;
import java.util.List;

public class MCTSNode {
    public final Action action;
    public final MCTSNode parent;
    public final List<MCTSNode> children;
    public int wins=0;
    public int rollouts =0;

    public MCTSNode(MCTSNode parent, Action action){
        this.parent = parent;
        this.action = action;
        children = new ArrayList<>();
    }

    /**
     * Gives value of node.
     *
     * @return value of node
     */
    public double value() {
        return rollouts !=0? wins/(double) rollouts : 0;
    }

    /**
     * Gives exploration value of node.
     * The bigger it is the most likely this node gets explored.
     *
     * @param rolloutsDone total nb of rollouts done
     * @return exploration value of node
     */
    public double explorationValue(int rolloutsDone){
        return rollouts !=0? value()+MCTS.explorationParam*Math.sqrt(Math.log(rolloutsDone)/(float) rollouts) : Double.MAX_VALUE;
    }

    /**
     * Gives child node with highest exploration value.
     *
     * @param rolloutsDone total nb of rollouts done
     * @return child node that should be explored
     */
    public MCTSNode getChildToExplore(int rolloutsDone){
        MCTSNode node = null;
        double max = Double.MIN_VALUE;
        for (MCTSNode child : children) {
            double explorationValue = child.explorationValue(rolloutsDone);
            if (max <= explorationValue) {
                node = child;
                max = explorationValue;
            }
        }
        return node;
    }
}
