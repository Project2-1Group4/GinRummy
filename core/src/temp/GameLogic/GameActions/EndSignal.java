package temp.GameLogic.GameActions;

import temp.GameLogic.Entities.Step;
import temp.GameLogic.States.RoundState;

public class EndSignal extends Action{
    public final boolean endOfGame;
    public EndSignal(boolean endOfGame) {
        super(null, 0);
        this.endOfGame = endOfGame;
    }

    @Override
    protected boolean specificSame(Object other) {
        return false;
    }

    @Override
    protected boolean specificCanDo(RoundState state) {
        return false;
    }

    @Override
    protected void specificDo(RoundState state) {

    }

    @Override
    protected void specificUndo(RoundState state) {

    }

    @Override
    protected String specificToString() {
        return null;
    }
}
