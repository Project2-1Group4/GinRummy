package GameLogic.GameActions;

import GameLogic.Entities.Step;
import GameLogic.Entities.MyCard;
import GameLogic.States.RoundState;

// IMMUTABLE
public class PickAction extends Action {
    public final boolean deck;
    private MyCard card;

    public PickAction(int playerIndex, boolean deck, MyCard card) {
        super(Step.Pick, playerIndex);
        this.deck = deck;
        this.card = card;
    }

    // Getters

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
                (card.equals(((PickAction) other).card)))){
            return true;
        }
        return card.equals(((PickAction) other).card);
    }
    @Override
    public boolean specificCanDo(RoundState state) {
        if(deck){
            if(card==null){
                if(state.deckSize()!=0){ return true; }
            }
            else {
                if (state.deckSize() != 0 && state.peekDeck().equals(card)) { return true; }
                if (state.deckSize() == 0 && state.unassigned().contains(card)) { return true; }
            }
        }
        return !deck && card != null && state.discardPile().size() != 0 && card.equals(state.peekDiscard());
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

    // Setters

    @Override
    protected void specificDo(RoundState state) {
        if (deck && state.deckSize() == 0 && state.unassigned().contains(card)){
            state.cards(playerIndex).add(card);
            state.unassigned().remove(card);
            return;
        }
        if(deck) card = state.peekDeck();
        state.cards(playerIndex).add(deck? state.deck().pop():state.discardPile().pop());
    }
    @Override
    protected void specificUndo(RoundState state) {
        state.cards(playerIndex).remove(card);
        if (deck) {
            state.deck().add(card);
        } else {
            state.discardPile().add(card);
        }
    }
}