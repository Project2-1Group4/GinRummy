package Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import GameLogic.Entities.MyCard;

// Main rendering information stored here
public class Style {
    private final Sprite background;
    private final Sprite[][] cardFaces;
    private final Sprite cardBack;
    private final BitmapFont font;
    private final Sprite unknownSprite;
    private final float widthToHeightCard;

    protected Style(Sprite background, Sprite[][] cardFaces, Sprite cardBack, BitmapFont font, Sprite unknownSprite) {
        this.background = background;
        this.cardFaces = cardFaces;
        this.cardBack = cardBack;
        this.font = font;
        this.unknownSprite = unknownSprite;
        widthToHeightCard = cardBack.getWidth() / cardBack.getHeight();
    }

    // Getters

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
    public Sprite getCardFace(MyCard card) {
        Sprite s = cardFaces[card.suit.index][card.rank.index];
        if (s == null) {
            return unknownSprite;
        }
        return s;
    }
    public Sprite getUnknownSprite() {
        return unknownSprite;
    }
    public BitmapFont getFont() {
        if (font == null) {
            return new BitmapFont();
        }
        font.setColor(Color.BLACK);
        font.getData().setScale(1);
        return font;
    }
    public float getWidthToHeightCard() {
        return widthToHeightCard;
    }
}
