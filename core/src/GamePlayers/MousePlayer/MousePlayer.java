package GamePlayers.MousePlayer;

import GameLogic.Entities.MyCard;
import GamePlayers.GamePlayer;
import Graphics.GameCard;

import java.util.List;

// NEEDS GRAPHICS TO WORK
// Has no knock or continue button. Use in combo with keyboard
public class MousePlayer extends GamePlayer {
    protected GameCard clicked;

    public MousePlayer() {
        processor = new MouseHandler(this);
    }

    // Game <=> Player interaction

    @Override
    public Boolean knockOrContinue() {
        return null;
    }
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        if (clicked != null) {
            if (clicked.card == null) {
                return true;
            }
            if (clicked.card.equals(topOfDiscard)) {
                return false;
            }
        }
        return null;
    }
    @Override
    public MyCard discardCard() {
        if (clicked != null) {
            return clicked.card;
        }
        return null;
    }
    @Override
    public void update(List<MyCard> realLayout) {
        super.update(realLayout);
        clicked = null;
    }

}