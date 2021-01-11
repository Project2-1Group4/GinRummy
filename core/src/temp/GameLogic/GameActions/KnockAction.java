package temp.GameLogic.GameActions;

import temp.GameLogic.Entities.Step;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.States.RoundState;
import temp.GameRules;

// IMMUTABLE
public class KnockAction extends Action {
    public final boolean knock;
    private final HandLayout knockLayout;

    public KnockAction(int playerIndex, boolean knock, HandLayout knockLayout) {
        super(Step.KnockOrContinue, playerIndex);
        this.knock = knock;
        this.knockLayout = knockLayout;
    }

    public HandLayout viewLayout() {
        return knockLayout.deepCopy();
    }

    @Override
    protected boolean specificSame(Object other) {
        KnockAction o = (KnockAction) other;
        if (o.knock != knock) {
            return false;
        }
        if (!knock) {
            return true;
        }
        if(knockLayout==null && o.knockLayout==null){
            return true;
        }
        if(knockLayout==null || o.knockLayout==null){
            return false;
        }
        return knockLayout.same(o.knockLayout);
    }

    @Override
    public boolean specificCanDo(RoundState state) {
        return !knock ||(state.cards(playerIndex).size() >= GameRules.baseCardsPerHand &&
                Finder.findBestHandLayout(state.cards(playerIndex)).deadwoodValue()<= GameRules.minDeadwoodToKnock);
    }

    @Override
    protected void specificDo(RoundState state) {
        if(knock) {
            state.knocker(playerIndex);
        }
    }

    @Override
    protected void specificUndo(RoundState state) {
        state.knocker(null);
    }

    @Override
    public String specificToString() {
        if (!knock) {
            return " didn't knock.";
        }else {
            return " knocked.";
        }
    }
}