package temp.GameLogic.GameActions;

import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.States.RoundState;

// IMMUTABLE
public class DiscardAction extends Action {
    public final MyCard card;

    public DiscardAction(int playerIndex, MyCard card) {
        super(Step.Discard, playerIndex);
        this.card = card;
    }

    @Override
    protected boolean specificSame(Object other) {
        return card.same(((DiscardAction) other).card);
    }

    @Override
    public boolean specificCanDo(RoundState state) {
        return MyCard.has(state.cards(playerIndex), card);
    }

    @Override
    protected void specificDo(RoundState state) {
        state.discardPile().add(card);
        MyCard.remove(state.cards(playerIndex), card);
    }

    @Override
    protected void specificUndo(RoundState state) {
        state.cards(playerIndex).add(state.discardPile().pop());
    }

    @Override
    public String specificToString() {
        return " discarded " + card;
    }
}