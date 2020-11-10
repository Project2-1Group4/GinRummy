package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;

public class PickAction extends Action {
    public final boolean deck;
    public PickAction( int actorIndex, boolean deck) {
        super(State.StepInTurn.Pick, actorIndex);
        this.deck = deck;
    }

    @Override
    public String toString() {
        return baseString()+" picked from deck? "+deck;
    }
}
