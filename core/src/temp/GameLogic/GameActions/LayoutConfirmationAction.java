package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;

// IMMUTABLE
public class LayoutConfirmationAction extends Action {
    public final HandLayout layout;
    public LayoutConfirmationAction(int actorIndex, HandLayout handLayout) {
        super(State.StepInTurn.LayoutConfirmation, actorIndex);
        assert handLayout.isValid();
        this.layout = handLayout;
    }

    public HandLayout viewLayout(){
        return layout.deepCopy();
    }

    @Override
    protected boolean specificSame(Action other) {
        return layout.same(((LayoutConfirmationAction)other).layout);
    }

    @Override
    public String toString() {
        return baseString()+" confirmed layout:\n"+layout;
    }
}
