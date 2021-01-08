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
    protected boolean specificSame(Action other) {
        return card.same(((DiscardAction) other).card);
    }

    @Override
    public boolean specificCanDo(RoundState state) {
        return MyCard.has(state.getCards(playerIndex), card);
    }

    @Override
    protected void specificDo(RoundState state) {
        state.discardPile().add(card);
        MyCard.remove(state.getCards(playerIndex), card);
    }

    @Override
    protected void specificUndo(RoundState state) {
        state.getCards(playerIndex).add(state.discardPile().pop());
    }

    @Override
    public String specificToString() {
        return " discarded " + card;
    }
}