package temp.GamePlayers;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MouseStuff.MousePlayer;
import temp.Graphics.Graphics;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class CombinePlayer extends GamePlayer{
    private List<GamePlayer> handlers;
    public CombinePlayer(GamePlayer player1, GamePlayer player2){
        handlers = new ArrayList<>();
        processor = new InputMultiplexer();
        addPlayer(player1);
        addPlayer(player2);
    }

    public void addPlayer(GamePlayer player){
        handlers.add(player);
        if(player.processor!=null) {
            ((InputMultiplexer) processor).addProcessor(player.processor);
        }
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
    public void update(HandLayout realLayout, int index) {
        super.update(realLayout,index);
        for (GamePlayer handler : handlers) {
            handler.update(realLayout,index);
        }
    }

    @Override
    public void render(SpriteBatch batch, Style renderStyle, PlayerRenderer renderer) {
        for (GamePlayer handler : handlers) {
            handler.render(batch,renderStyle, renderer);
        }
    }

    @Override
    public void newRound() {
        super.newRound();
        for (GamePlayer handler : handlers) {
            handler.newRound();
        }
    }

    public static CombinePlayer getBaseCombinePlayer(){
        return new CombinePlayer(new KeyboardPlayer(),new MousePlayer());
    }
}
