package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.Graphics.GameCard;
import temp.Graphics.Graphics;
import temp.Graphics.Style;

public class CardRenderer implements Renderer{

    public GameCard deck;
    public GameCard discard;

    @Override
    public void render(SpriteBatch batch, Style style, State curState) {
        update(style);
        renderDeck(batch,style,curState.getDeckSize()==0);
        renderDiscard(batch,style, curState.peekDiscardTop());
    }

    private final float widthPerc = 0.2f;
    private final float heightPerc = 0.2f;

    private void update(Style style){
        initDeck(style.getWidthToHeightCard());
        initDiscard(style.getWidthToHeightCard());
    }

    private void initDeck(float widthToHeight){
        if(deck!=null) {
            deck.centerPosition = new Vector2(Gdx.graphics.getWidth()*0.2f,Gdx.graphics.getHeight()*0.5f);
            deck.size = Graphics.getSize(null,widthPerc,heightPerc, widthToHeight);
        }
    }

    private void initDiscard(float widthToHeight){
        if(discard!=null) {
            discard.centerPosition = new Vector2(Gdx.graphics.getWidth()*0.5f,Gdx.graphics.getHeight()*0.5f);
            discard.size = Graphics.getSize(null,widthPerc,heightPerc, widthToHeight);
        }
    }

    private void renderDeck(SpriteBatch batch, Style style, boolean deckEmpty){
        if(!deckEmpty){
            if(deck==null){
                deck = new GameCard(null);
                initDeck(style.getWidthToHeightCard());
            }
            deck.render(batch,style);
        }else if(deck!=null){
            deck = null;
        }
    }

    private void renderDiscard(SpriteBatch batch, Style style, MyCard card){
        if(card!=null){
            if(discard==null){
                discard = new GameCard(card);
                initDiscard(style.getWidthToHeightCard());
            }else if(!discard.isSame(card)){
                boolean hovered = discard.hovered;
                discard = new GameCard(card);
                discard.hovered = hovered;
                initDiscard(style.getWidthToHeightCard());
            }
            discard.render(batch,style);
        }else if(discard!=null){
            discard=null;
        }
    }

    public GameCard getHovered(float x, float y){
        if(deck!=null && deck.isHovered(x,y)){
            return deck;
        }
        if(discard!=null && discard.isHovered(x,y)){
            return discard;
        }
        return null;
    }
}
