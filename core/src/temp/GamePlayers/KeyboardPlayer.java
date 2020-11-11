package temp.GamePlayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;

import java.util.List;

/**
 * Play game with keyboard
 * Space = continue, Esc = knock
 * 1 = deck, 2 = discard pile
 * 1,2,3,4,5,6,7,8,9,0 <- discard card of index-1, and 0 = 10
 */
public class KeyboardPlayer extends GamePlayer {
    public KeyboardPlayer() {
        super();
    }

    @Override
    public Boolean knockOrContinue() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            return false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return true;
        }
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(boolean deckEmpty, MyCard topOfDiscard) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            return false;
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            return viewHand().get(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            return viewHand().get(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            return viewHand().get(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            return viewHand().get(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            return viewHand().get(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            return viewHand().get(5);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            return viewHand().get(6);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            return viewHand().get(7);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            return viewHand().get(8);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            return viewHand().get(9);
        }
        return null;
    }

    @Override
    public HandLayout confirmLayout() {
        return null;
    }

    @Override
    public Layoff layOff(List<Meld> knockerMelds) {
        return null;
    }
}
