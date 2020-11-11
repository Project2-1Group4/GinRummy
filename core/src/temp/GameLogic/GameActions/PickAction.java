package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

// IMMUTABLE
public class PickAction extends Action {
    public final boolean deck;
    public final MyCard card;
    public PickAction(int actorIndex, boolean deck, MyCard card) {
        super(State.StepInTurn.Pick, actorIndex);
        this.deck = deck;
        this.card = card;
    }

    @Override
    protected boolean specificSame(Action other) {
        if(deck!=((PickAction)other).deck){
            return false;
        }
        if(deck){
            return true;
        }
        return card.same(((PickAction)other).card);
    }

    @Override
    public String toString() {
        if(deck) {
            return baseString() + " picked from deck.";
        }else{
            return baseString() + " picked "+card+" from discard.";
        }
    }
}