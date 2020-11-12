package temp.Graphics.RenderingSpecifics.PlayerRenderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class MeldVisualInfo extends VisualInfo{

    public final Meld meld;
    public List<CardVisualInfo> cards;
    public MeldVisualInfo(Meld meld){
        this.meld = meld;
        cards = new ArrayList<>();
    }

    public void init(){
        float[] p = getPosition();
        for (MyCard card : meld.viewMeld()) {
            CardVisualInfo visualInfo = new CardVisualInfo(card);
            visualInfo.width = width;
            visualInfo.height = height;
            visualInfo.pos.set(p[0],p[1]);
            cards.add(visualInfo);
            p[1]-= height/2;
        }
    }

    public void render(SpriteBatch batch, Style style){
        for (CardVisualInfo card : cards) {
            card.render(batch,style);
        }
    }

    public CardVisualInfo getCardAt(int x, int y){
        for (int i = cards.size()-1; i >= 0; i--) {
            if(cards.get(i).isOn(x,y)){
                return cards.get(i);
            }
        }
        return null;
    }
}
