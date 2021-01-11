package temp.GamePlayers;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.MyCard;
import temp.GamePlayers.MousePlayer.MousePlayer;
import temp.Graphics.RenderingSpecifics.PlayerRenderer;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class CombinePlayer extends GamePlayer {
    private final List<GamePlayer> handlers;

    public CombinePlayer(GamePlayer player1, GamePlayer player2) {
        handlers = new ArrayList<>();
        processor = new InputMultiplexer();
        addPlayer(player1);
        addPlayer(player2);
    }

    public void addPlayer(GamePlayer player) {
        player.index = index;
        handlers.add(player);
        if (player.processor != null) {
            ((InputMultiplexer) processor).addProcessor(player.processor);
        }
    }

    @Override
    public Boolean knockOrContinue() {
        for (GamePlayer handler : handlers) {
            Boolean move = handler.knockOrContinue();
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        for (GamePlayer handler : handlers) {
            Boolean move = handler.pickDeckOrDiscard(remainingCardsInDeck, topOfDiscard);
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    @Override
    public MyCard discardCard() {
        for (GamePlayer handler : handlers) {
            MyCard move = handler.discardCard();
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        for (GamePlayer handler : handlers) {
            handler.playerDiscarded(discardAction);
        }
    }

    @Override
    public void playerPicked(PickAction pickAction) {
        for (GamePlayer handler : handlers) {
            handler.playerPicked(pickAction);
        }
    }

    @Override
    public void executed(Action action) {
        for (GamePlayer handler : handlers) {
            handler.executed(action);
        }
    }

    @Override
    public void update(List<MyCard> realLayout) {
        super.update(realLayout);
        for (GamePlayer handler : handlers) {
            handler.update(realLayout);
        }
    }

    @Override
    public void render(SpriteBatch batch, Style renderStyle, PlayerRenderer renderer) {
        for (GamePlayer handler : handlers) {
            handler.render(batch, renderStyle, renderer);
        }
    }

    @Override
    public void newRound(MyCard topOfDiscard) {
        super.newRound(topOfDiscard);
        for (GamePlayer handler : handlers) {
            handler.newRound(topOfDiscard);
        }
    }

    public static CombinePlayer getBaseCombinePlayer() {
        return new CombinePlayer(new KeyboardPlayer(), new MousePlayer());
    }

}