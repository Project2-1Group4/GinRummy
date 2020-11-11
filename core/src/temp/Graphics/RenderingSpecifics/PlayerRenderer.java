package temp.Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.Graphics.Style;

import java.util.List;

public class PlayerRenderer implements Renderer {
    private float maxWidthPerc = 0.8f;
    private float maxHeightPerc = 0.2f;
    private float percAwayFromBottom = 0.2f;

    @Override
    public void render(SpriteBatch batch, Style style, State curState) {
        HandLayout handLayout = curState.getPlayer().viewHandLayout();

        float[] dimensions = getDimensions(11, style.getWidthToHeightCard());

        renderUnused(batch, style, handLayout.viewUnusedCards(), dimensions);
        renderMelds(batch, style, handLayout.viewMelds(), dimensions);
        renderValues(batch, style,new int[]{handLayout.getValue(), handLayout.getDeadwood()});
    }

    public void renderUnused(SpriteBatch batch, Style style, List<MyCard> cards, float[] dimensions) {
        float[] p = getCardPosition(dimensions[0], percAwayFromBottom, cards.size());
        Sprite s;
        for (int i = 0; i < cards.size(); i++) {
            s = style.getCardFace(cards.get(i));
            s.setSize(dimensions[0], dimensions[1]);
            s.setPosition(p[0], p[1]);
            p[0] += dimensions[0];
            s.draw(batch);
        }
    }

    public void renderMelds(SpriteBatch batch, Style style, List<Meld> melds, float[] dimensions) {
        float[] p = new float[]{
                Gdx.graphics.getWidth() * 0.6f,
                Gdx.graphics.getHeight() * 0.8f
        };
        float x = p[0];
        Sprite s;
        for (Meld meld : melds) {
            float y = p[1];
            for (MyCard myCard : meld.viewMeld()) {
                y -= dimensions[1] * 0.5f;
                s = style.getCardFace(myCard);
                s.setSize(dimensions[0], dimensions[1]);
                s.setPosition(x, y);
                s.draw(batch);
            }
            x += dimensions[0] * 1.5;
        }
    }
    public void renderValues(SpriteBatch batch, Style style, int[] values){
        style.getFont().draw(batch,"Hand value: "+values[0]+"\nDeadwood value: "+values[1],10,40);
    }

    private float[] getDimensions(int nbOfCards, float widthToHeight) {
        float maxW = (Gdx.graphics.getWidth() * maxWidthPerc) / nbOfCards;
        float maxH = Gdx.graphics.getHeight() * maxHeightPerc;
        if (maxH * widthToHeight > maxW) {
            maxH = maxW / widthToHeight;
        } else {
            maxW = maxH * widthToHeight;
        }
        return new float[]{
                maxW,
                maxH
        };
    }

    private float[] getCardPosition(float cardWidth, float percAwayFromBottom, int cards) {
        return new float[]{
                Gdx.graphics.getWidth() * 0.5f - ((cardWidth * cards) / 2),
                Gdx.graphics.getHeight() * percAwayFromBottom
        };
    }
}