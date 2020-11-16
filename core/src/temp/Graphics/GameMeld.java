package temp.Graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MyCard;

import java.util.List;

public class GameMeld {

    public List<GameCard> cards;
    public GameMeld(List<GameCard> cards){
        this.cards = cards;
    }
    public void render(SpriteBatch batch, Style style){
        for (GameCard card : cards) {
            card.render(batch,style);
        }
    }

    public void setSize(float[] dimensions) {
        for (GameCard card : cards) {
            card.size = dimensions;
        }
    }

    public void setCenterPosition(float x, float y) {
        for (GameCard card : cards) {
            card.centerPosition.set(x,y);
            y-= card.size[1]*0.5f;
        }
    }

    public boolean has(MyCard card){
        for (GameCard gameCard : cards) {
            if(gameCard.isSame(card)){
                return true;
            }
        }
        return false;
    }

    public GameCard get(MyCard card){
        for (GameCard gameCard : cards) {
            if(gameCard.isSame(card)){
                return gameCard;
            }
        }
        return new GameCard(card);
    }

    @Override
    public String toString() {
        return cards.toString();
    }

    public GameCard getHovered(float x, float y) {
        for (int i = cards.size()-1; i >=0; i--) {
            if(cards.get(i).isHovered(x,y)){
                return cards.get(i);
            }
        }
        return null;
    }
}
