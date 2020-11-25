package temp.Graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import temp.GameLogic.MyCard;

public class GameCard {
    public Vector2 centerPosition = new Vector2();
    public float[] size = new float[2];
    public boolean hovered = false;
    public boolean faceVisible = true;
    public final MyCard card;

    public GameCard(MyCard card) {
        this.card = card;
    }

    public void render(SpriteBatch batch, Style style) {
        Sprite s;
        if (faceVisible && card != null) {
            s = style.getCardFace(card);
        } else {
            s = style.getCardBack();
        }
        float[][] transform = getCurrentTransformation();
        s.setSize(transform[0][0], transform[0][1]);
        s.setPosition(transform[1][0], transform[1][1]);
        s.draw(batch);
    }

    // OUTER GETTERS
    public boolean isSame(MyCard other) {
        return card.same(other);
    }

    public boolean isSame(GameCard other) {
        if (card == null && other.card == null) {
            return true;
        }
        if (card == null || other.card == null) {
            return false;
        }
        return card.same(other.card);
    }

    public boolean isHovered(float x, float y) {
        float[][] corners = getExtremeCorners(getSize());
        return corners[0][0] < x && corners[1][0] > x && corners[0][1] < y && corners[1][1] > y;
    }

    // INNER GETTERS

    /**
     * @return float[0] = size, float[1] = position
     */
    private float[][] getCurrentTransformation() {
        float[] currentSize = getSize();
        return new float[][]{
                currentSize,
                getBottomLeft(currentSize)
        };
    }

    private float[] getSize() {
        final float hoveredSizeIncrease = 1.2f;
        if (hovered) {
            return new float[]{
                    size[0] * hoveredSizeIncrease,
                    size[1] * hoveredSizeIncrease
            };
        } else {
            return new float[]{
                    size[0],
                    size[1]
            };
        }
    }

    private float[] getBottomLeft(float[] size) {
        final float[] hoveredPositionChange = new float[]{0, 0};
        if (hovered) {
            return new float[]{
                    centerPosition.x + hoveredPositionChange[0] - size[0] * 0.5f,
                    centerPosition.y + hoveredPositionChange[1] - size[1] * 0.5f
            };
        } else {
            return new float[]{
                    centerPosition.x - size[0] * 0.5f,
                    centerPosition.y - size[1] * 0.5f
            };
        }
    }

    /**
     * float[0] = BotLeft, float[1] = TopRight
     *
     * @param size current card size
     * @return float[0] = BotLeft, float[1] = TopRight
     */
    private float[][] getExtremeCorners(float[] size) {
        return new float[][]{
                {centerPosition.x - size[0] / 2, centerPosition.y - size[1] / 2},
                {centerPosition.x + size[0] / 2, centerPosition.y + size[1] / 2}
        };
    }

    public int dst2(GameCard other) {
        float[] size = getSize();
        float[][] corners = getExtremeCorners(size);
        float[] otherSize = other.getSize();
        float[][] otherCorners = other.getExtremeCorners(otherSize);
        Vector2 dif = new Vector2(other.centerPosition).sub(centerPosition);
        if (dif.x <= 0 && dif.y <= 0) {
            return (int) new Vector2(corners[0][0], corners[0][1]).dst2(otherCorners[1][0], otherCorners[1][1]);
        } else if (dif.x <= 0 && dif.y >= 0) {
            return (int) new Vector2(corners[0][0], corners[1][1]).dst2(otherCorners[1][0], otherCorners[0][1]);
        } else if (dif.x >= 0 && dif.y <= 0) {
            return (int) new Vector2(corners[1][0], corners[0][1]).dst2(otherCorners[0][0], otherCorners[1][1]);
        } else {
            return (int) new Vector2(corners[1][0], corners[1][1]).dst2(otherCorners[0][0], otherCorners[0][1]);
        }
    }

    @Override
    public String toString() {
        return card.toString();
    }
}
