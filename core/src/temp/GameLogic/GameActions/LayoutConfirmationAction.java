package temp.GameLogic.GameActions;

import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.States.RoundState;

// IMMUTABLE
public class LayoutConfirmationAction extends Action {
    public final HandLayout layout;

    public LayoutConfirmationAction(int playerIndex, HandLayout handLayout) {
        super(Step.LayoutConfirmation, playerIndex);
        assert handLayout.isValid();
        this.layout = handLayout;
    }

    public HandLayout viewLayout() {
        return layout.deepCopy();
    }

    @Override
    protected boolean specificSame(Object other) {
        return layout.same(((LayoutConfirmationAction) other).layout);
    }

    @Override
    public boolean specificCanDo(RoundState state) {
        return layout.isValid();
    }

    @Override
    protected void specificDo(RoundState state) {
        state.layouts()[playerIndex] = layout;
    }

    @Override
    protected void specificUndo(RoundState state) {
        state.layouts()[playerIndex] = null;
    }

    @Override
    public String specificToString() {
        return " confirmed layout:\n" + layout;
    }
}