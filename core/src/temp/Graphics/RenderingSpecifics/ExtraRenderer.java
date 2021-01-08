package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.Game;
import temp.Graphics.Style;

public class ExtraRenderer implements Renderer {
    @Override
    public void render(SpriteBatch batch, Style style, Game curState) {
        BitmapFont font = style.getFont();
        font.setColor(Color.BLACK);
        int y = Gdx.graphics.getHeight() - 20;
        font.draw(batch, "Round " + curState.getRoundNumber() + " turn " + curState.getTurnNumber(), 20, y);
        y -= 20;
        font.draw(batch, "Player " + curState.getPlayerIndex(), 20, y);
        y -= 20;
        font.draw(batch, curState.getStep().question, 20, y);
        y -= 20;
        font.draw(batch, Float.toString(curState.getCurTime()), 20, y);
        int[] scores = curState.getPoints();
        font.draw(batch, "SCORES", Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 20);
        for (int i = 0; i < scores.length; i++) {
            font.draw(batch, "Player " + i + ": " + scores[i], Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 20 * (i + 2));
        }
    }
}