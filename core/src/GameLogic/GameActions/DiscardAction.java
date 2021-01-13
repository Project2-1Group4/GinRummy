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

    // Getters

    @Override
    protected boolean specificSame(Object other) {
        return card.equals(((DiscardAction) other).card);
    }
    @Override
    protected boolean specificCanDo(RoundState state) {
        return state.cards(playerIndex).contains(card) || state.unassigned().contains(card);
    }
    @Override
    public String specificToString() {
        return " discarded " + card;
    }

    // Setters

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
}