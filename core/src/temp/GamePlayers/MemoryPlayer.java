package temp.GamePlayers;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.States.CardsInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class MemoryPlayer extends GamePlayer {
    protected static final int discard = -1;
    // -1 = discard, 0 = unknown, player = player index
    protected int[][] memory;
    protected Stack<MyCard> discardMemory;
    protected Step step;
    protected int turn;
    protected int round;

    public MemoryPlayer() {
        memory = new int[MyCard.Suit.values().length][MyCard.Rank.values().length];
        discardMemory = new Stack<>();
        round = 0;
    }

    protected CardsInfo unpackMemory(){
        List<MyCard> otherPlayer = new ArrayList<>();
        List<MyCard> unknown = new ArrayList<>();
        for (int suit = 0; suit < memory.length; suit++) {
            for (int rank = 0; rank < memory[suit].length; rank++) {
                if(memory[suit][rank]==index+1 || (memory[suit][rank] == 1 && index != 1)){
                    otherPlayer.add(new MyCard(suit,rank));
                }
                if(memory[suit][rank]==0){
                    unknown.add(new MyCard(suit,rank));
                }
            }
        }
        List<List<MyCard>> players = new ArrayList<>();
        players.add(viewHand());
        players.add(otherPlayer);
        return new CardsInfo(players, new Stack<MyCard>(), unknown, (Stack<MyCard>) discardMemory.clone());
    }

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

    @Override
    public void newRound(MyCard topOfDiscard) {
        super.newRound(topOfDiscard);
        set(topOfDiscard, discard);
        for (MyCard card : allCards) {
            set(card, index);
        }
        round++;
    }

    @Override
    public void update(List<MyCard> realLayout) {
        super.update(realLayout);

        // So the idea of this method is to transform all cards that were previously in the hand as unknown
        // And then go through the handlayout again to update the current cards
        // There are probably more efficient ways of doing this
        // But given there's no distinction done between discard card or something else, this feels like the easiest way

        // IMPORTANT NOTE: I'm assuming that if a card is no longer in the hand that it was discarded
        // This should hold no matter what, but still it's important to make note of that assumption

        updateMemory(this.handLayout);

    }

    /*
    So the AI was having issues remembering it got an 11 card and keeping that information in memory
    So I'm modifying the update method to update the internal matrix
     */

    public void updateMemory(HandLayout layout){
        for(int i=0; i<memory.length;i++){
            for(int j = 0; j<memory[0].length;j++){
                // I'm 90% sure it's this.index, but still I want to add a note to make sure I'm not fucking up
                // But what I'm doing here is setting all cards in memory as discard cards
                // Then I clean up and update which cards are now in the hand
                if(memory[i][j] == this.index){
                    memory[i][j] = -1;
                }

            }

        }

        for(MyCard card: layout.viewAllCards()){
            memory[card.suit.index][card.rank.index] = this.index;
        }

    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        set(discardAction.card, discard);
    }

    @Override
    public void playerPicked(PickAction pickAction) {
        if (!pickAction.deck || pickAction.card()!=null) {
            set(pickAction.card(), pickAction.playerIndex);
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

    private void set(MyCard card, int id){
        if(id==discard){
            discardMemory.add(card);
        }
        else if(card.same(discardMemory.peek())){
            discardMemory.pop();
        }
        memory[card.suit.index][card.rank.index] = id;
    }
}