package temp.GamePlayers;

import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.States.CardsInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class MemoryPlayer extends GamePlayer {
    protected CardsInfo cardsMemory;
    protected Step step;
    protected int turn;
    protected int round;

    public MemoryPlayer() {
        round = 0;
    }

    // Game <=> Player interaction
    @Override
    public final Boolean knockOrContinue() {
        step = Step.KnockOrContinue;
        return KnockOrContinue();
    }
    public abstract Boolean KnockOrContinue();
    @Override
    public final Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        step = Step.Pick;
        return PickDeckOrDiscard(remainingCardsInDeck, topOfDiscard);
    }
    public abstract Boolean PickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard);
    @Override
    public final MyCard discardCard() {
        step = Step.Discard;
        return DiscardCard();
    }
    public abstract MyCard DiscardCard();

    // Sync game info with player
    @Override
    public void newRound(MyCard topOfDiscard) {
        super.newRound(topOfDiscard);
        List<List<MyCard>> players = new ArrayList<>();
        for (int i = 0; i < Game.numberOfPlayers(this); i++) {
            players.add(new ArrayList<MyCard>());
        }
        players.set(index, getHand());
        Stack<MyCard> discard = new Stack<>();
        discard.add(topOfDiscard);
        Stack<MyCard> unknown = MyCard.getBasicDeck();
        unknown.removeAll(players.get(index));
        unknown.remove(topOfDiscard);
        cardsMemory = new CardsInfo(players, new Stack<MyCard>(),unknown,discard);
        round++;
    }

    // Sync moves done with player knowledge
    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        boolean found = cardsMemory.players.get(discardAction.playerIndex).remove(discardAction.card);
        if(!found){
            if(discardAction.playerIndex==index) {
                System.out.println("uuh oh MemoryPlayer playerDiscarded()");
            }
            else{
                cardsMemory.unassigned.remove(discardAction.card);
            }
        }
        cardsMemory.discardPile.add(discardAction.card);
    }
    @Override
    public void playerPicked(PickAction pickAction) {
        if(pickAction.playerIndex>=cardsMemory.players.size()){
            cardsMemory.players.add(new ArrayList<MyCard>());
        }
        if(pickAction.deck){
            if(pickAction.card()!=null){
                assert pickAction.playerIndex==index;
                cardsMemory.unassigned.remove(pickAction.card());
                cardsMemory.players.get(pickAction.playerIndex).add(pickAction.card());
            }
        }
        else{
            assert pickAction.card().equals(cardsMemory.peekDiscard());
            cardsMemory.players.get(pickAction.playerIndex).add(cardsMemory.discardPile.pop());
        }
    }
    @Override
    public void executed(Action action) {
        if (action instanceof PickAction) {
            playerPicked((PickAction) action);
        } else if (action instanceof DiscardAction) {
            playerDiscarded((DiscardAction) action);
        }
    }
}