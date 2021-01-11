package temp.GameLogic.GameActions;


import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.RoundState;
import temp.GameLogic.Entities.Step;
import temp.GameRules;

public abstract class Action {
    public final Step step;
    public final int playerIndex;

    public Action(Step step, int playerIndex) {
        this.step = step;
        this.playerIndex = playerIndex;
    }

    /*
    Getters
     */
    public Step getStep(){
        return step;
    }
    public boolean equals(Object o) {
        if (this.getClass() == o.getClass()) {
            return specificSame(o);
        }
        return false;
    }
    public String toString() {
        return "Player " + playerIndex+ specificToString();
    }
    //Do
    public boolean canDo(RoundState state, boolean respectCurrentTurn){
        return ((!respectCurrentTurn || (state.turn()!=null && state.turn().playerIndex == playerIndex && state.turn().step == step) || this instanceof EndSignal) && specificCanDo(state));
    }
    public boolean doAction(RoundState state, boolean respectCurrentTurn){
        if(canDo(state, respectCurrentTurn)){
            specificDo(state);
            state.actions.add(this);
            if(respectCurrentTurn) state.turn(state.turn().getNext(state));
            return true;
        }
        if(this instanceof KnockAction){
            throw new AssertionError("lul man");
        }
        return false;
    }
    //Undo
    public boolean canUndo(RoundState state){
        return state.actions.size()!=0 && state.actions.peek().equals(this);
    }
    public boolean undoAction(RoundState state){
        if(canUndo(state)){
            state.turn(state.turn().getPrevious(state));
            specificUndo(state);
            state.actions.pop();
            return true;
        }
        return false;
    }

    /*
    Subclass specific methods
     */
    protected abstract boolean specificSame(Object other);
    protected abstract boolean specificCanDo(RoundState state);
    protected abstract void specificDo(RoundState state);
    protected abstract void specificUndo(RoundState state);
    protected abstract String specificToString();
}