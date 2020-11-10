package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;

public class KnockAction extends Action {
    public final boolean knock;
    public KnockAction(int actorIndex, boolean knock) {
        super(State.StepInTurn.KnockOrContinue, actorIndex);
        this.knock = knock;
    }

    @Override
    public String toString() {
        return baseString()+" knocked? "+knock;
    }
}
