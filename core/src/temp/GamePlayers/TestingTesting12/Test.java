package temp.GamePlayers.TestingTesting12;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MemoryPlayer;

public class Test extends MemoryPlayer {
    public Test() {
    }

    @Override
    public Boolean knockOrContinue() {
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return null;
    }

    @Override
    public MyCard discardCard() {
        return null;
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        super.playerDiscarded(discardAction);
        stateChanged(discardAction);
    }

    @Override
    public void playerPicked(PickAction pickAction) {
        super.playerPicked(pickAction);
        stateChanged(pickAction);
    }

    private void stateChanged(Action action) {
        System.out.println(action);
        for (int i = 0; i < memory.length; i++) {
            if (i != 0 && i % 13 == 0) {
                System.out.println();
            }
            System.out.print(memory[i] + " ");
        }
        System.out.println("\n");
    }
}