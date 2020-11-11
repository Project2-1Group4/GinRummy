package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

// IMMUTABLE
public class DiscardAction extends Action {
    public final MyCard card;
    public DiscardAction(int playerIndex, MyCard card) {
        super(State.StepInTurn.Discard, playerIndex);
        this.card=card;
    }

    @Override
    protected boolean specificSame(Action other) {
        return card.same(((DiscardAction)other).card);
    }

    @Override
    public String toString() {
        return baseString()+" discarded "+card;
    }
}