package temp.GamePlayers.MouseStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.Graphics.RenderingSpecifics.PlayerRenderers.VisualInfo;
import temp.Graphics.Style;

public class ContinueButton extends VisualInfo {

    public boolean hovered = false;
    public boolean isOn(int x, int y){
        float[] botLeft = new float[]{pos.x-width/2,pos.y-height/2};
        float[] topRight = new float[]{pos.x+width/2,pos.y+height/2};
        return x>botLeft[0] && x<topRight[0] && y>botLeft[1] && y<topRight[1];
    }

    @Override
    public void render(SpriteBatch batch, Style style) {
        float[] p = getPosition();
        BitmapFont font = style.getFont();
        if(hovered) {
                font.setColor(new Color(0.4f, 0.4f, 0.4f, 1));
        }
        font.draw(batch,"Continue",p[0],pos.y);

        width = Gdx.graphics.getWidth()*0.1f;
        height = Gdx.graphics.getHeight()*0.05f;
        pos.x = (Gdx.graphics.getWidth()+20)/(float)2;
        pos.y = Gdx.graphics.getHeight()*0.8f;
        hovered = false;
    }
}
