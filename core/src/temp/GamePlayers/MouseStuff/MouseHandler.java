package temp.GamePlayers.MouseStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import temp.Graphics.GameCard;
import temp.Graphics.Graphics;

public class MouseHandler extends InputAdapter {
    private final MousePlayer owner;
    public MouseHandler(MousePlayer owner){
        this.owner = owner;
    }
    private GameCard hovered;
    private GameCard held;
    private GameCard moved;

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.R){
            Graphics.getInstance().getPlayerRenderer().reset();
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if(button == Input.Buttons.LEFT){
            held = Graphics.getInstance().getHovered(screenX,screenY);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if(button == Input.Buttons.LEFT){
            if(held != moved) {
                GameCard c = Graphics.getInstance().getHovered(screenX,screenY);
                if (c != null && c.isSame(held)) {
                    owner.clicked = c;
                }
            }
            moved = null;
            held = null;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        screenY = Gdx.graphics.getHeight() - screenY;
        if(held!=null){
            Graphics.getInstance().move(held,screenX,screenY);
            moved = held;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        screenY = Gdx.graphics.getHeight() - screenY;
        GameCard c = Graphics.getInstance().getHovered(screenX,screenY);
        if(hovered!=null && c!=null && !hovered.isSame(c)){
            hovered.hovered = false;
            c.hovered = true;
            hovered = c;
        }
        if(hovered!=null && c==null){
            hovered.hovered = false;
            hovered = null;
        }
        if(hovered==null && c!=null){
            c.hovered = true;
            hovered = c;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
