package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.PickAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSNode {
    private static final Random rd = new Random(0);

    public final Action action;
    public final MCTSNode parent;
    public final int depth;
    public final List<MCTSNode> children;
    public double wins=0;
    public double rollouts =0;

    public MCTSNode(MCTSNode parent, Action action){
        this.parent = parent;
        depth = parent==null? 0 : parent.depth+1;
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
        MCTSNode node = children.get(0);
        double max = Double.MIN_VALUE;
        for (MCTSNode child : children) {
            double explorationValue = child.explorationValue(rolloutsDone);
            if (max <= explorationValue) {
                node = child;
                max = explorationValue;
            }
        }
        if(node.action instanceof PickAction && children.size()>2 && ((PickAction) node.action).deck){
            do{
                node = children.get(rd.nextInt(children.size()-1));
            }while(!((PickAction)(node.action)).deck);
        }
        return node;
    }

    public String toString(){
        return "Depth "+depth+". Action \"" + action + "\" has a score of " + wins + "/" + rollouts;
    }

    public boolean isLeaf() {
        return children.size()==0;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean same(MCTSNode mctsNode) {
        return action.same(mctsNode.action);
    }
}
