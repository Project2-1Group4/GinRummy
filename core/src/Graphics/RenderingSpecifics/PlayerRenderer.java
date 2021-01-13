package Graphics.RenderingSpecifics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import GamePlayers.GamePlayer;
import temp.GameRules;
import Graphics.GameCard;
import Graphics.GameMeld;
import Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class PlayerRenderer {

    public final GamePlayer player;

    private List<GameCard> unmoved = new ArrayList<>();
    private List<GameMeld> melds = new ArrayList<>();
    private List<GameCard> moved = new ArrayList<>();

    public PlayerRenderer(GamePlayer player) {
        this.player = player;
    }

    public void render(SpriteBatch batch, Style style) {
        HandLayout handLayout = player.viewHandLayout();
        init(style, handLayout);
        player.render(batch, style, this);

        renderUnmoved(batch, style);
        renderMoved(batch, style);
        renderMelds(batch, style);
        renderValues(batch, style, new int[]{handLayout.meldValue(), handLayout.deadwoodValue()});
    }

    public void init(Style style, HandLayout handLayout) {
        List<MyCard> cards = handLayout.unused();
        List<Meld> cardMelds = handLayout.melds();

        updateUnusedCards(cards);
        updateMeldCards(cardMelds);

        float[] dimensions = getDimensions(style.getWidthToHeightCard());
        initUnmoved(dimensions);
        initMelds(dimensions);

    }

    private void updateUnusedCards(List<MyCard> cards) {
        List<GameCard> newUnmoved = new ArrayList<>();
        List<GameCard> newMoved = new ArrayList<>();
        // Gets rid of all unmoved
        for (MyCard card : cards) {
            boolean found = false;
            for (GameCard gameCard : unmoved) {
                if (gameCard.isSame(card)) {
                    newUnmoved.add(gameCard);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (GameCard gameCard : moved) {
                    if (gameCard.isSame(card)) {
                        newMoved.add(gameCard);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                newUnmoved.add(new GameCard(card));
            }
        }
        unmoved = newUnmoved;
        moved = newMoved;
    }

    private void updateMeldCards(List<Meld> meldCards) {
        List<GameMeld> newMelds = new ArrayList<>();
        for (Meld meld : meldCards) {
            boolean exists = false;
            for (int i = 0; i < melds.size(); i++) {
                for (MyCard myCard : meld.cards()) {
                    if (melds.get(i).has(myCard)) {
                        exists = true;
                    }
                }
                if (exists) {
                    List<GameCard> newMeld = new ArrayList<>();
                    for (MyCard myCard : meld.cards()) {
                        GameCard c = melds.get(i).get(myCard);
                        newMeld.add(c);
                    }
                    newMelds.add(new GameMeld(newMeld));
                    break;
                }
            }
            if (!exists) {
                List<GameCard> newMeld = new ArrayList<>();
                for (MyCard myCard : meld.cards()) {
                    newMeld.add(new GameCard(myCard));
                }
                newMelds.add(new GameMeld(newMeld));
            }
        }
        this.melds = newMelds;
    }

    private void initUnmoved(float[] dimensions) {
        float[] p = new float[]{
                Gdx.graphics.getWidth() * 0.5f - ((dimensions[0] * unmoved.size()) / 2) + 0.5f * dimensions[0],
                Gdx.graphics.getHeight() * GameRules.percentageAwayFromBottom
        };
        for (GameCard gameCard : unmoved) {
            gameCard.size = new float[]{
                    dimensions[0],
                    dimensions[1]
            };
            gameCard.centerPosition.set(p[0], p[1]);
            p[0] += dimensions[0];
        }
    }

    private void initMelds(float[] dimensions) {
        float[] p = new float[]{
                Gdx.graphics.getWidth() - (1.5f * (dimensions[0] * melds.size())),
                Gdx.graphics.getHeight() * 0.75f
        };

        for (GameMeld meld : melds) {
            meld.setSize(new float[]{dimensions[0], dimensions[1]});
            meld.setCenterPosition(p[0], p[1]);
            p[0] += dimensions[0] * 1.5f;
        }
    }

    private void renderUnmoved(SpriteBatch batch, Style style) {
        for (GameCard card : unmoved) {
            card.render(batch, style);
        }
    }

    private void renderMelds(SpriteBatch batch, Style style) {
        for (GameMeld meld : melds) {
            meld.render(batch, style);
        }
    }

    private void renderMoved(SpriteBatch batch, Style style) {
        for (GameCard card : moved) {
            card.render(batch, style);
        }
    }

    private void renderValues(SpriteBatch batch, Style style, int[] values) {
        style.getFont().draw(batch, "Hand value: " + values[0] + "\nDeadwood value: " + values[1], 10, 40);
    }

    private float[] getDimensions(float widthToHeight) {
        float maxW = (Gdx.graphics.getWidth() / (float) 11) * GameRules.cardMaxWidthPercentage;
        float maxH = Gdx.graphics.getHeight() * GameRules.cardMaxHeightPercentage;
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

    public GameCard getHovered(float x, float y) {
        for (GameCard card : moved) {
            if (card.isHovered(x, y)) {
                return card;
            }
        }
        for (GameCard gameCard : unmoved) {
            if (gameCard.isHovered(x, y)) {
                return gameCard;
            }
        }
        for (GameMeld meld : melds) {
            GameCard hovered = meld.getHovered(x, y);
            if (hovered != null) {
                return hovered;
            }
        }
        return null;
    }

    public void move(GameCard card, float x, float y) {
        for (int i = 0; i < unmoved.size(); i++) {
            if (card.card != null && card.isSame(unmoved.get(i))) {
                unmoved.remove(card);
                moved.add(card);
                break;
            }
        }
        // To make the last moved card render on top but doesn't work for some reason
        moved.remove(card);
        moved.add(card);
        card.centerPosition.set(x, y);
    }

    public void reset() {
        unmoved.addAll(moved);
        moved = new ArrayList<>();
    }
}