package GamePlayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.Layoff;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;

import java.util.List;

/**
 * Play game with keyboard
 * Space = continue, Esc = knock
 * 1 = deck, 2 = discard pile
 * 1,2,3,4,5,6,7,8,9,0 <- discard card of index-1, and 0 = 10
 */
public class KeyboardPlayer extends GamePlayer {

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
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
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
        List<MyCard> cards = viewUnusedHand();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && cards.size() >= 1) {
            return cards.get(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) && cards.size() >= 2) {
            return cards.get(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) && cards.size() >= 3) {
            return cards.get(2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) && cards.size() >= 4) {
            return cards.get(3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) && cards.size() >= 5) {
            return cards.get(4);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6) && cards.size() >= 6) {
            return cards.get(5);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) && cards.size() >= 7) {
            return cards.get(6);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) && cards.size() >= 8) {
            return cards.get(7);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9) && cards.size() >= 9) {
            return cards.get(8);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) && cards.size() >= 10) {
            return cards.get(9);
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
    public HandLayout confirmLayout() {
        return null;
    }

    @Override
    public List<Layoff> layOff(List<Meld> knockerMelds) {
        return null;
    }
}
