package temp.GamePlayers;

import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;

public abstract class MemoryPlayer extends GamePlayer{
    // -1 = discard, 0 = unknown, player = player index+1
    int[] memory;
    public MemoryPlayer(){
        memory = new int[MyCard.Suit.values().length*MyCard.Rank.values().length];
    }

    @Override
    public void update(HandLayout realLayout,int index) {
        super.update(realLayout,index);
        for (MyCard card : allCards) {
            memory[card.getIndex()] =index+1;
        }
    }

    @Override
    public void otherPlayerDiscarded(DiscardAction discardAction) {
        memory[discardAction.card.getIndex()] = -1;
    }

    @Override
    public void otherPlayerPicked(PickAction pickAction) {
        if(!pickAction.deck){
            memory[pickAction.card.getIndex()] = pickAction.playerIndex+1;
        }
    }
}
