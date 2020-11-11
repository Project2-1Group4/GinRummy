package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

// IMMUTABLE
public class LayoffAction extends Action {
    public final MyCard card;
    public final Meld meld;
    public LayoffAction(int actorIndex, MyCard card, Meld meld) {
        super(State.StepInTurn.LayOff, actorIndex);
        this.card = card;
        this.meld = meld;
    }

    public Meld viewMeld(){
        return meld.deepCopy();
    }

    @Override
    protected boolean specificSame(Action other) {
        return card.same(((LayoffAction)other).card) && meld.same(((LayoffAction)other).meld);
    }

    @Override
    public String toString() {
        if(card!=null) {
            return baseString() + " laid off " + card;
        }
        else{
            return baseString() + " is done laying off.";
        }
    }
}