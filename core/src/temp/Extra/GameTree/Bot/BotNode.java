package temp.Extra.GameTree.Bot;

import temp.Extra.GameTree.Node;
import temp.GameLogic.GameActions.Action;

public class BotNode extends Node {
    public final int player;
    public BotNode(int depth, Node parent, Action action, int player) {
        super(depth, parent, action);
        this.player = player;
    }
}
