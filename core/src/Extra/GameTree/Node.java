package Extra.GameTree;

import GameLogic.GameActions.Action;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public final int depth;
    public final Node parent;
    public final Action action;
    public List<Node> children;

    public Node(int depth, Node parent, Action action) {
        this.depth = depth;
        this.parent = parent;
        this.action = action;
        children = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Depth ").append(depth);
        if (parent != null) {
            if (parent.action == null) {
                sb.append(" Parent root ");
            } else {
                sb.append(" Parent action ").append(parent.action);
            }
            sb.append(" Action ").append(action)
                    .append(" Children ").append(children.size());
        } else {
            sb.append("Root");
        }
        return sb.toString();
    }

    /**
     * Prints the whole tree starting from this node
     */
    public void printLeaves(){
        printLeaves(0);
    }

    private void printLeaves(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
        System.out.println("depth " + this.depth + " " + action);
        for (Node child : children) {
            child.printLeaves(depth + 1);
        }
    }

    /**
     * Prints the tree in the form of a directory
     *
     * @param depth    you want to go lower than this node (ex: depth = 2, and this.depth = 1 then print from this node to depth = 3)
     */
    public void printLeavesTo(int depth){
        printLeavesTo(depth,0);
    }

    private void printLeavesTo(int depth, int curDepth) {
        if (curDepth == depth + 1) {
            return;
        }
        for (int i = 0; i < curDepth; i++) {
            System.out.print("\t");
        }
        System.out.println("depth " + this.depth + " " + action);
        for (Node child : children) {
            child.printLeavesTo(depth, curDepth + 1);
        }
    }

    /**
     * Returns the number of nodes in the tree when you go *depth* deeper from this node (including this)
     *
     * @param depth you want to go to starting from this current node
     * @return width of the tree given the starting node (this)
     */
    public int nodesUntilDepth(int depth) {
        return nodesUntilDepth(depth,0);
    }

    private int nodesUntilDepth(int depth, int nodes){
        if (children.size() == 0 || depth == 0) {
            return nodes+1;
        }
        nodes+=1;
        for (Node child : children) {
            nodes = child.nodesUntilDepth(depth - 1,nodes);
        }
        return nodes;
    }

    /**
     * Returns the number of nodes at the depth = this.depth+depth including leaf nodes that appear earlier
     * @param depth wanted
     * @return width that given depth
     */
    public int widthAtDepth(int depth){
        return widthAtDepth(depth,0);
    }

    private int widthAtDepth(int depth, int width){
        if (depth == 0) {
            return width+1;
        }
        for (Node child : children) {
            width = child.widthAtDepth(depth - 1,width);
        }
        return width;
    }

    /**
     * Returns array of width at every depth from this.depth to this.depth+depth
     * @param depth deepest
     * @return int[] array of width
     */
    public int[] widthsAtDepths(int depth){
        return widthsAtDepths(depth,0,new int[depth+1]);
    }

    private int[] widthsAtDepths(int depth, int index, int[] nodes){
        nodes[index]+=1;
        if (children.size() == 0 || depth == 0) {
            return nodes;
        }
        for (Node child : children) {
            nodes = child.widthsAtDepths(depth - 1,index+1,nodes);
        }
        return nodes;
    }
}
