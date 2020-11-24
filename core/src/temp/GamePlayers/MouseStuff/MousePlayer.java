package temp.GamePlayers.MouseStuff;

import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.Graphics.GameCard;

// NEEDS GRAPHICS TO WORK
// Has no knock or continue. Dont feel like implementing. Just use in combo with keyboard
public class MousePlayer extends GamePlayer {


    protected GameCard clicked;

    public MousePlayer(){
        processor = new MouseHandler(this);
    }

    @Override
    public Boolean knockOrContinue() {
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        if(clicked!=null){
            if(clicked.card==null){
                return true;
            }
            if(clicked.card.same(topOfDiscard)){
                return false;
            }
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        if(clicked!=null){
            return clicked.card;
        }
        return null;
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {

    }

    @Override
    public void playerPicked(PickAction pickAction) {

    }

    @Override
    public void update(HandLayout realLayout) {
        super.update(realLayout);
        clicked = null;
    }

}