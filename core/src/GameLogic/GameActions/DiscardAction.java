package GameLogic.GameActions;

import GameLogic.Entities.MyCard;
import GameLogic.Entities.Step;
import GameLogic.States.RoundState;

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
        return state.cards(playerIndex).contains(card) || state.unassigned().contains(card);
    }

    @Override
    protected void specificDo(RoundState state) {
        state.discardPile().add(card);
        if(!state.cards(playerIndex).remove(card)){
            state.unassigned().remove(card);
        }
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