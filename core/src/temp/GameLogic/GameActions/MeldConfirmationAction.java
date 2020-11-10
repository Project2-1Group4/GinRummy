package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;

public class MeldConfirmationAction extends Action {
    public MeldConfirmationAction(int actorIndex) {
        super(State.StepInTurn.MeldConfirmation, actorIndex);
    }

    @Override
    public String toString() {
        return baseString()+" confirmed melds.";
    }
}
