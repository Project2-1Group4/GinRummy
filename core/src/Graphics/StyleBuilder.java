package Graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import GameLogic.Entities.MyCard;

// Builder class
public class StyleBuilder {
    private Sprite background;
    private final Sprite[][] cardFaces;
    private Sprite cardBack;
    private BitmapFont font;
    private final Sprite unknownSprite;

    public StyleBuilder() {
        background = new Sprite(new Texture("core/assets/pokerTable2.jpg"));
        font = new BitmapFont();
        unknownSprite = new Sprite(new Texture("core/assets/skin/comic-ui.png"));
        cardFaces = new Sprite[MyCard.Suit.values().length][MyCard.Rank.values().length];

        TextureAtlas atlas = new TextureAtlas("core/assets/carddeck.atlas");
        setTexturesUsingAtlas(atlas);
    }

    public StyleBuilder setBackground(Sprite bg) {
        this.background = bg;
        return this;
    }
    public StyleBuilder useAtlas(TextureAtlas atlas) {
        setTexturesUsingAtlas(atlas);
        return this;
    }
    public StyleBuilder setCardBack(Sprite sprite) {
        this.cardBack = sprite;
        return this;
    }
    public StyleBuilder setFont(BitmapFont font) {
        this.font = font;
        return this;
    }
    private void setTexturesUsingAtlas(TextureAtlas atlas) {
        cardBack = atlas.createSprite("back", 1);
        for (MyCard.Suit suit : MyCard.Suit.values()) {
            for (MyCard.Rank rank : MyCard.Rank.values()) {
                cardFaces[suit.index][rank.index] = atlas.createSprite(suit.value, rank.value);
            }
        }
    }
    public Style build() {
        return new Style(background, cardFaces, cardBack, font, unknownSprite);
    }
}