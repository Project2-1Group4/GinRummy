package GamePlayers.GameTreeAIs.MCTS;

import GameLogic.GameActions.Action;
import GameLogic.GameActions.PickAction;

import java.util.ArrayList;
import java.util.List;

public class MCTSNode {

    private final MCTS creator;

    public final Action action;
    public final MCTSNode parent;
    public final int depth;
    public final List<MCTSNode> children;
    public double wins=0;
    public double rollouts =0;

    public MCTSNode(MCTSNode parent, Action action, MCTS creator){
        this.creator = creator;
        this.parent = parent;
        depth = parent==null? 0 : parent.depth+1;
        this.action = action;
        children = new ArrayList<>();
    }

    // Getters

    public boolean isLeaf() {
        return children.size()==0;
    }
    public boolean isValid(){
        int r = 0;
        for (MCTSNode child : children) {
            r+=child.rollouts;
        }
        r+= isRoot() || isLeaf()? 0: creator.rolloutsPerNode;
        return r==rollouts;
    }
    public boolean isRoot(){
        return parent == null;
    }
    public boolean equals(Object o){
        if(action==null){
            return o instanceof MCTSNode && ((MCTSNode) o).depth==depth && ((MCTSNode) o).action==null;
        }
        return o instanceof MCTSNode && depth==((MCTSNode) o).depth && action.equals(((MCTSNode) o).action);
    }
    public int subtreeSize(){
        int subtreeSize = 0;
        for (MCTSNode child : children) {
            if(child.rollouts!=0) {
                subtreeSize++;
                subtreeSize += child.subtreeSize();
            }
        }
        return subtreeSize;
    }
    public int subtreeDepth(){
        return actualDepth()-depth;
    }
    private int actualDepth(){
        if(rollouts==0) {
            return depth;
        }
        int curMax = depth;
        for (MCTSNode child : children) {
            int depth = child.actualDepth();
            if(depth>curMax){
                curMax = depth;
            }
        }
        return curMax;
    }
    /**
     * Gives value of node.
     *
     * @return value of node
     */
    public double value() {
        return rollouts !=0? wins/ rollouts : 0;
    }
    /**
     * Gives exploration value of node.
     * The bigger it is the most likely this node gets explored.
     *
     * @param rolloutsDone total nb of rollouts done
     * @return exploration value of node
     */
    public double explorationValue(int rolloutsDone){
        return rollouts !=0? value()+creator.explorationParam*Math.sqrt(Math.log(rolloutsDone)/(float) rollouts) : Double.MAX_VALUE;
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
                node = children.get(creator.rd.nextInt(children.size()-1));
            }while(!((PickAction)(node.action)).deck);
        }
        return node;
    }
    public String toString(int maxDepth){
        return toString(maxDepth, 1);
    }
    private String toString(int maxDepth, int depthTraversed){
        StringBuilder sb = new StringBuilder();
        sb.append("Depth ").append(depth)
                .append(". Action \"").append(action)
                .append("\" has a score of ").append(wins)
                .append("/").append(rollouts).append("\n");
        if(depth==maxDepth){
            return sb.toString();
        }
        for (int i = 0; i < children.size(); i++) {
            for (int tabs = 0; tabs < depthTraversed; tabs++) {
                sb.append("\t");
            }
            sb.append(i).append(". ").append(children.get(i).toString(maxDepth, depthTraversed+1));
        }
        return sb.toString();
    }

    // Setters

    public void backPropagate(){
        backPropagate(rollouts, wins);
    }
    private void backPropagate(double rollouts, double wins){
        if(!isRoot()){
            parent.rollouts+=rollouts;
            parent.wins+=wins;
            parent.backPropagate(rollouts, wins);
        }
    }
    public boolean merge(MCTSNode o){
        if(equals(o)){
            boolean found = false;
            for (int i = 0; i < o.children.size(); i++) {
                for (int j = i; j < children.size(); j++) {
                    if(children.get(j).merge(o.children.get(i))){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    children.add(o.children.get(i));
                }
            }
            rollouts+= o.rollouts;
            wins+= o.wins;
            return true;
        }
        return false;
    }
}