package temp.Graphics.RenderingSpecifics.PlayerRenderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

public class CardVisualInfo extends VisualInfo{

    public final MyCard card;

    public CardVisualInfo(MyCard card) {
        this.card = card;
    }

    public void render(SpriteBatch batch, Style style){
        Sprite s;
        if(card==null){
            s = style.getCardBack();
        }else {
            s = style.getCardFace(card);
        }
        s.setSize(width,height);
        float[] p = getPosition();
        s.setPosition(p[0],p[1]);
        s.draw(batch);
    }

    public boolean isOn(int x, int y){
        float[] botLeft = new float[]{pos.x-width/2,pos.y-height/2};
        float[] topRight = new float[]{pos.x+width/2,pos.y+height/2};
        return x>botLeft[0] && x<topRight[0] && y>botLeft[1] && y<topRight[1];
    }

    public String toString(){
        return card + " at " + pos + " with width " + width + " and height " + height;
    }
}
