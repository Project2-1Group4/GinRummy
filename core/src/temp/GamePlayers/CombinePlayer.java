package temp.GamePlayers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MouseStuff.MousePlayer;
import temp.Graphics.Graphics;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class CombinePlayer extends GamePlayer{
    private List<GamePlayer> handlers;
    public CombinePlayer(GamePlayer player1, GamePlayer player2){
        handlers = new ArrayList<>();
        handlers.add(player1);
        handlers.add(player2);
    }

    public void addPlayer(GamePlayer player){
        handlers.add(player);
    }
    @Override
    public Boolean knockOrContinue() {
        for (GamePlayer handler : handlers) {
            Boolean move = handler.knockOrContinue();
            if(move!=null){
                return move;
            }
        }
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        for (GamePlayer handler : handlers) {
            Boolean move = handler.pickDeckOrDiscard(remainingCardsInDeck,topOfDiscard);
            if(move!=null){
                return move;
            }
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        for (GamePlayer handler : handlers) {
            MyCard move = handler.discardCard();
            if(move!=null){
                return move;
            }
        }
        return null;
    }

    @Override
    public void otherPlayerDiscarded(DiscardAction discardAction) {

    }

    @Override
    public void otherPlayerPicked(PickAction pickAction) {

    }

    @Override
    public void update(HandLayout realLayout) {
        super.update(realLayout);
        for (GamePlayer handler : handlers) {
            handler.update(realLayout);
        }
    }

    @Override
    public void render(SpriteBatch batch, Style renderStyle) {
        super.render(batch, renderStyle);
        for (GamePlayer handler : handlers) {
            handler.render(batch,renderStyle);
        }
    }

    @Override
    public void newRound() {
        super.newRound();
        for (GamePlayer handler : handlers) {
            handler.newRound();
        }
    }

    public static CombinePlayer getBaseCombinePlayer(Graphics graphics){
        return new CombinePlayer(new KeyboardPlayer(),new MousePlayer(graphics));
    }
}
