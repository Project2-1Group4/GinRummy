package temp.Graphics;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import temp.GameLogic.MyCard;

/**
 * Where the main rendering information is stored
 */
public class Style {
    private Sprite background;
    private Sprite[][] cardFaces;
    private Sprite cardBack;
    private BitmapFont font;
    private Sprite unknownSprite;
    private float widthToHeightCard;

    protected Style(Sprite background, Sprite[][] cardFaces, Sprite cardBack, BitmapFont font, Sprite unknownSprite) {
        this.background = background;
        this.cardFaces = cardFaces;
        this.cardBack = cardBack;
        this.font = font;
        this.unknownSprite = unknownSprite;
        widthToHeightCard = cardBack.getWidth() / cardBack.getHeight();
    }

    public Sprite getBackground() {
        if (background == null) {
            return unknownSprite;
        }
        return background;
    }

    public Sprite getCardBack() {
        if (cardBack == null) {
            return unknownSprite;
        }
        return cardBack;
    }

    public BitmapFont getFont() {
        if (font == null) {
            return new BitmapFont();
        }
        return font;
    }

    public float getWidthToHeightCard() {
        return widthToHeightCard;
    }

    public Sprite getCardFace(MyCard card) {
        Sprite s = cardFaces[card.suit.index][card.rank.index];
        if (s == null) {
            return unknownSprite;
        }
        return s;
    }
}
