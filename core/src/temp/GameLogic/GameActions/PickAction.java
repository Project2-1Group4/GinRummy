package temp.GameLogic.GameActions;

import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.States.RoundState;

// IMMUTABLE
public class PickAction extends Action {
    public final boolean deck;
    private MyCard card;

    public PickAction(int playerIndex, boolean deck, MyCard card) {
        super(Step.Pick, playerIndex);
        this.deck = deck;
        this.card = card;
    }

    public MyCard card(){
        return card;
    }

    @Override
    protected boolean specificSame(Object other) {
        if (deck != ((PickAction) other).deck) {
            return false;
        }
        if((card==null && ((PickAction) other).card!=null) || (card!=null && ((PickAction) other).card==null)){
            return false;
        }
        if(deck &&
                ((card==null && ((PickAction) other).card==null)||
                (card.same(((PickAction) other).card)))){
            return true;
        }
        return card.same(((PickAction) other).card);
    }

    @Override
    public boolean specificCanDo(RoundState state) {
        return (deck && state.deckSize()!=0 && (card==null || card.same(state.peekDeck())))
                || (!deck && card!=null && state.discardPile().size() != 0 && card.same(state.peekDiscard()));
    }

    @Override
    protected void specificDo(RoundState state) {
        if(deck) card = state.peekDeck();
        state.cards(playerIndex).add(deck? state.deck().pop():state.discardPile().pop());
    }

    @Override
    protected void specificUndo(RoundState state) {
        MyCard.remove(state.cards(playerIndex),card);
        if (deck) {
            state.deck().add(card);
        } else {
            state.discardPile().add(card);
        }
    }

    @Override
    public String specificToString() {
        if (deck && card ==null) {
            return " picked from deck.";
        }else if(deck){
            return " picked "+card+" from deck";
        }else {
            return " picked " + card + " from discard.";
        }
    }
}