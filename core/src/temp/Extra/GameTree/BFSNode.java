package temp.Extra.GameTree;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.State;


public class BFSNode extends Node{
    public boolean discovered;
    public State state;
    public BFSNode(int depth, BFSNode parent, Action action) {
        super(depth,parent,action);
        discovered = false;
        state = null;
    }
}
