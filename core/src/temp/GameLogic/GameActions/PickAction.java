package temp.GameLogic.GameActions;

import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

public class PickAction extends Action {
    public final boolean deck;
    public final MyCard card;
    public PickAction(int actorIndex, boolean deck, MyCard card) {
        super(State.StepInTurn.Pick, actorIndex);
        this.deck = deck;
        this.card = card;
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
