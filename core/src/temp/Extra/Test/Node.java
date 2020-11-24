package temp.Extra.Test;

import temp.GameLogic.GameActions.Action;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public final int depth;
    public final Node parent;
    public final Action action;
    public List<Node> children;
    public Node(int depth, Node parent, Action action){
        this.depth = depth;
        this.parent = parent;
        this.action = action;
        children = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Depth ").append(depth);
        if(parent!=null) {
            if(parent.action==null){
                sb.append(" Parent root ");
            }else {
                sb.append(" Parent action ").append(parent.action);
            }
            sb.append(" Action ").append(action)
                    .append(" Children ").append(children.size());
        }else{
            sb.append("Root");
        }
        return sb.toString();
    }

    /**
     * Prints the whole tree starting from this node
     * @param depth start with 0.
     */
    public void printLeaves(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
        System.out.println("depth "+this.depth+" "+action);
        for (Node child : children) {
            child.printLeaves(depth + 1);
        }
    }

    /**
     * Prints the tree in the form of a directory
     * @param depth you want to go lower than this node (ex: depth = 2, and this.depth = 1 then print from this node to depth = 3)
     * @param curDepth starts at 0. Used to keep \t consistent
     */
    public void printLeavesTo(int depth, int curDepth){
        if(curDepth==depth+1){
            return;
        }
        for (int i = 0; i < curDepth; i++) {
            System.out.print("\t");
        }
        System.out.println("depth "+this.depth+" "+action);
        for (Node child : children) {
            child.printLeavesTo(depth, curDepth+1);
        }
    }

    /**
     * Returns the width of the tree when you go *depth* deeper from this node
     * @param depth you want to go to starting from this current node
     * @return width of the tree given the starting node (this)
     */
    public int treeWidthAtDepth(int depth){
        if(depth==0){
            return 0;
        }
        int width = children.size();
        for (Node child : children) {
            width += child.treeWidthAtDepth(depth - 1);
        }
        return width;
    }
}
