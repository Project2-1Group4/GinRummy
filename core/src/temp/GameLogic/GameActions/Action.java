package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

public abstract class Action {
    public final State.StepInTurn step;
    public final int playerIndex;
    public Action(State.StepInTurn step, int playerIndex){
        this.step = step;
        this.playerIndex = playerIndex;
    }
    protected String baseString(){
        return "Player "+playerIndex;
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