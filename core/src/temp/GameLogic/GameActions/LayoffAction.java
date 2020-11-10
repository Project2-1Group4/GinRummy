package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

//TODO don't know how to formulate this
public class LayoffAction extends Action {
    public final MyCard card;
    public LayoffAction(int actorIndex, MyCard card) {
        super(State.StepInTurn.LayOff, actorIndex);
        this.card = card;
    }

    @Override
    public String toString() {
        return baseString()+" laid off "+card;
    }
}
