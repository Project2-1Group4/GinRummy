package GameLogic.GameActions;

import GameLogic.States.RoundState;

public class EndSignal extends Action{
    public final boolean endOfGame;
    public EndSignal(boolean endOfGame) {
        super(null, 0);
        this.endOfGame = endOfGame;
    }

    @Override
    protected boolean specificSame(Object other) {
        return other instanceof EndSignal && ((EndSignal) other).endOfGame == endOfGame;
    }

    @Override
    protected boolean specificCanDo(RoundState state) {
        return true;
    }

    @Override
    protected void specificDo(RoundState state) {

    }

    @Override
    protected void specificUndo(RoundState state) {

    }

    @Override
    protected String specificToString() {
        return endOfGame? " End Of Game." : " End Of Round.";
    }
}
