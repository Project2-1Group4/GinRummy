package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;

import java.util.ArrayList;
import java.util.List;

public class MCTSNode {
    public final Action action;
    public final List<MCTSNode> children;
    public int wins=0;
    public int rollouts =0;

    public MCTSNode(Action action){
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
        return value()+MCTS.explorationParam*Math.sqrt(Math.log(rolloutsDone)/(float) rollouts);
    }

    /**
     * Gives child node with highest exploration value.
     *
     * @param rolloutsDone total nb of rollouts done
     * @return child node that should be explored
     */
    public MCTSNode getExploredChildNode(int rolloutsDone){
        MCTSNode node = null;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < children.size(); i++) {
            double explorationValue = children.get(i).explorationValue(rolloutsDone);
            if(max<=explorationValue){
                node = children.get(i);
                max = explorationValue;
            }
        }
        return node;
    }
}
