package temp.GamePlayers;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GameTreeAIs.MCTS.KnowledgeBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class MemoryPlayer extends GamePlayer {
    protected static final int discard = -1;
    // -1 = discard, 0 = unknown, player = player index
    protected int[][] memory;
    protected Stack<MyCard> discardMemory;
    protected State.StepInTurn step;
    protected int turn;
    protected int round;

    public MemoryPlayer() {
        memory = new int[MyCard.Suit.values().length][MyCard.Rank.values().length];
        discardMemory = new Stack<>();
        round = 0;
    }

    protected KnowledgeBase unpackMemory(){
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
        return new KnowledgeBase(step, 0, viewHand(), otherPlayer, new ArrayList<MyCard>(), unknown, (Stack<MyCard>) discardMemory.clone());
    }

    @Override
    public final Boolean knockOrContinue() {
        step = State.StepInTurn.KnockOrContinue;
        return KnockOrContinue();
    }

    public abstract Boolean KnockOrContinue();

    @Override
    public final Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        step = State.StepInTurn.Pick;
        return PickDeckOrDiscard(remainingCardsInDeck, topOfDiscard);
    }

    public abstract Boolean PickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard);

    @Override
    public final MyCard discardCard() {
        step = State.StepInTurn.Discard;
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
    public void update(HandLayout realLayout) {
        super.update(realLayout);
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        set(discardAction.card, discard);
    }

    @Override
    public void playerPicked(PickAction pickAction) {
        if (!pickAction.deck || pickAction.card!=null) {
            set(pickAction.card, pickAction.playerIndex);
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