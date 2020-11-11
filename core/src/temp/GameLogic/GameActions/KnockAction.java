package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;

// IMMUTABLE
public class KnockAction extends Action {
    public final boolean knock;
    private final HandLayout knockLayout;
    public KnockAction(int actorIndex, boolean knock, HandLayout knockLayout) {
        super(State.StepInTurn.KnockOrContinue, actorIndex);
        this.knock = knock;
        if(knock){
            assert knockLayout!=null;
        }
        this.knockLayout = knockLayout;
    }

    public HandLayout viewLayout(){
        return knockLayout.deepCopy();
    }

    @Override
    protected boolean specificSame(Action other) {
        KnockAction o = (KnockAction)other;
        if(o.knock!=knock){
            return false;
        }
        if(!knock){
            return true;
        }
        return knockLayout.same(o.knockLayout);
    }

    @Override
    public String toString() {
        if(!knock){
            return baseString()+" didn't knock.";
        }else if(knockLayout.getDeadwood()==0){
            return baseString()+" called gin with:\n"+knockLayout;
        }else {
            return baseString() + " knocked with:\n" + knockLayout;
        }
    }
}