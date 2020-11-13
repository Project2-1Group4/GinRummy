package temp.Graphics.RenderingSpecifics.BasicVisualInfo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import temp.Graphics.Style;

public abstract class VisualInfo {
    public Vector2 pos = new Vector2();
    public float width;
    public float height;

    public abstract void render(SpriteBatch batch, Style style);

    protected float[] getPosition(){
        return new float[]{
                pos.x-width/2,
                pos.y-height/2
        };
    }

    protected String baseString(){
        return "pos "+pos+" size ("+width+","+height+")";
    }
}
