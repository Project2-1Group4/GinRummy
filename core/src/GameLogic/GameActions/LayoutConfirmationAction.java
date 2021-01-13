package GameLogic.GameActions;

import GameLogic.Entities.Step;
import GameLogic.Entities.HandLayout;
import GameLogic.States.RoundState;

// IMMUTABLE
public class LayoutConfirmationAction extends Action {
    public final HandLayout layout;

    public LayoutConfirmationAction(int playerIndex, HandLayout handLayout) {
        super(Step.LayoutConfirmation, playerIndex);
        assert handLayout.isValid();
        this.layout = handLayout;
    }

    // Getters

    public HandLayout layout() {
        return layout.copy();
    }
    @Override
    protected boolean specificSame(Object other) {
        return layout.equals(((LayoutConfirmationAction) other).layout);
    }
    @Override
    public boolean specificCanDo(RoundState state) {
        return layout.isValid();
    }
    @Override
    public String specificToString() {
        return " confirmed layout:\n" + layout;
    }

    // Setters

    @Override
    protected void specificDo(RoundState state) {
        state.layouts()[playerIndex] = layout;
    }
    @Override
    protected void specificUndo(RoundState state) {
        state.layouts()[playerIndex] = null;
    }

}