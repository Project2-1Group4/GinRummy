package temp.Extra.Test;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.State;

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

    public void print(int level) {
        assert level==depth;
        for (int i = 1; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(action);
        for (Node child : children) {
            child.print(level + 1);
        }
    }
}
