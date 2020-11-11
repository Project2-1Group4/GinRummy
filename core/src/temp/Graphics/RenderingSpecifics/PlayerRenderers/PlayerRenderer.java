package temp.Graphics.RenderingSpecifics.PlayerRenderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.Graphics.Style;

import java.util.List;

public abstract class PlayerRenderer{

    public abstract void render(SpriteBatch batch, Style renderStyle, HandLayout handLayout);
}