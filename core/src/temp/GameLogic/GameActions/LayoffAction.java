package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

// IMMUTABLE
public class LayoffAction extends Action {
    public final MyCard card;
    public final Meld meld;

    public LayoffAction(int playerIndex, MyCard card, Meld meld) {
        super(State.StepInTurn.LayOff, playerIndex);
        this.card = card;
        this.meld = meld;
    }

    public LayoffAction(int playerIndex, Layoff layoff) {
        this(playerIndex, layoff.card, layoff.meld);

    }

    public Meld viewMeld() {
        return meld.deepCopy();
    }

    @Override
    protected boolean specificSame(Action other) {
        LayoffAction o = (LayoffAction) other;
        if ((o.card == null || o.meld == null) && ((card == null || meld == null))) {
            return true;
        }
        else if (o.card == null || o.meld == null || card == null || meld == null) {
            return true;
        }
        return card.same(((LayoffAction) other).card) && meld.same(((LayoffAction) other).meld);
    }

    @Override
    public String toString() {
        if (card != null) {
            return baseString() + " laid off " + card;
        } else {
            return baseString() + " is done laying off.";
        }
    }
}