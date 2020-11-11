package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

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

    public boolean same(Action other){
        if(this.getClass()==other.getClass()){
            return specificSame(other);
        }
        return false;
    }

    protected abstract boolean specificSame(Action other);

    public abstract String toString();
}