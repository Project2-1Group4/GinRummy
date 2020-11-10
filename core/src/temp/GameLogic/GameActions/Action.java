package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;

public abstract class Action {
    public final State.StepInTurn step;
    public final int actorIndex;
    public Action(State.StepInTurn step, int actorIndex){
        this.step = step;
        this.actorIndex = actorIndex;
    }
    protected String baseString(){
        return "Player "+actorIndex;
    }
    public abstract String toString();
}
