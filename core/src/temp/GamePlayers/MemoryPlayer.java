package temp.GamePlayers;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;

public abstract class MemoryPlayer extends GamePlayer{
    protected static final int discard = -1;
    protected static final int unknown = 0;
    // -1 = discard, 0 = unknown, player = player index
    protected int[] memory;
    public MemoryPlayer(){
        memory = new int[MyCard.Suit.values().length*MyCard.Rank.values().length];
    }

    @Override
    public void newRound(MyCard topOfDiscard) {
        super.newRound(topOfDiscard);
        memory[topOfDiscard.getIndex()] = discard;
        for (MyCard card : allCards) {
            memory[card.getIndex()] = index;
        }
    }

    @Override
    public void update(HandLayout realLayout) {
        super.update(realLayout);
    }

    @Override
    public void playerActed(Action action) {
        if(action instanceof DiscardAction){
            playerDiscarded((DiscardAction) action);
        }
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        memory[discardAction.card.getIndex()] = -1;
    }

    @Override
    public void playerPicked(PickAction pickAction) {
        if(!pickAction.deck){
            memory[pickAction.card.getIndex()] = pickAction.playerIndex;
        }
    }
}
