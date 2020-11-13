package temp.Graphics.RenderingSpecifics.PlayerRenderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.GameRules;
import temp.Graphics.Style;

import java.util.ArrayList;
import java.util.List;

public class PlayerRenderer{

    public CardVisualInfo hovered = null;
    public List<CardVisualInfo> unmoved = new ArrayList<>();
    public List<CardVisualInfo> moved = new ArrayList<>();
    public List<MeldVisualInfo> melds = new ArrayList<>();

    public void render(SpriteBatch batch, Style style, HandLayout handLayout){
        update();

        renderUnmoved(batch,style);
        renderMelds(batch,style);
        renderMoved(batch,style);
        renderValues(batch,style,new int[]{handLayout.getValue(),handLayout.getDeadwood()});
    }

    public void init(Style style, HandLayout handLayout){
        List<MyCard> cards = handLayout.viewUnusedCards();
        List<Meld> cardMelds = handLayout.viewMelds();
        for (int j = 0; j < moved.size(); j++) {
            boolean inHand = false;
            for (int i = 0; i < cards.size(); i++) {
                if (moved.get(j).card.same(cards.get(i))) {
                    cards.remove(i);
                    inHand = true;
                    break;
                }
            }
            if(!inHand){
                moved.remove(j);
                j--;
            }
        }

        float[] dimensions = getDimensions(style.getWidthToHeightCard());

        initMelds(cardMelds,dimensions);
        initUnmoved(cards,dimensions);

    }

    private void initMelds(List<Meld> cardMelds, float[] dimensions){
        float[] p = new float[]{
                Gdx.graphics.getWidth() - (1.5f*(dimensions[0] * melds.size())),
                Gdx.graphics.getHeight() * 0.8f
        };
        melds = new ArrayList<>();
        for (Meld meld : cardMelds) {
            MeldVisualInfo visualInfo = new MeldVisualInfo(meld);
            visualInfo.width = dimensions[0];
            visualInfo.height = dimensions[1];
            visualInfo.pos.set(p[0],p[1]);
            visualInfo.init();
            melds.add(visualInfo);
            p[0]+= dimensions[0] * 1.5f;
        }
    }

    private void initUnmoved(List<MyCard> cards, float[] dimensions){
        float[] p = new float[]{
                Gdx.graphics.getWidth()*0.5f-((dimensions[0]*unmoved.size())/2)+0.5f*dimensions[0],
                Gdx.graphics.getHeight() * GameRules.percentageAwayFromBottom
        };
        unmoved = new ArrayList<>();
        for (MyCard card : cards) {
            CardVisualInfo visualInfo = new CardVisualInfo(card);
            visualInfo.width = dimensions[0];
            visualInfo.height = dimensions[1];
            visualInfo.pos.set(p[0],p[1]);
            p[0]+= visualInfo.width;
            unmoved.add(visualInfo);
        }
    }

    private void update(){
        //TODO check if a moved card can snap somewhere
    }

    private void renderUnmoved(SpriteBatch batch, Style style){
        for (CardVisualInfo card : unmoved) {
            card.render(batch,style);
        }
    }

    private void renderMelds(SpriteBatch batch, Style style){
        for (MeldVisualInfo meld : melds) {
            meld.render(batch,style);
        }
    }

    private void renderMoved(SpriteBatch batch, Style style){
        for (CardVisualInfo card : moved) {
            card.render(batch,style);
        }
    }

    private void renderValues(SpriteBatch batch, Style style, int[] values){
        style.getFont().draw(batch,"Hand value: "+values[0]+"\nDeadwood value: "+values[1],10,40);
    }

    private float[] getDimensions(float widthToHeight) {
        float maxW = (Gdx.graphics.getWidth()/(float)11) * GameRules.cardMaxWidthPercentage;
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

    public CardVisualInfo getCard(int x, int y){
        for (CardVisualInfo card : moved) {
            if(card.isOn(x,y)){
                return card;
            }
        }
        for (CardVisualInfo card : unmoved) {
            if(card.isOn(x,y)){
                return card;
            }
        }
        for (MeldVisualInfo meld : melds) {
            CardVisualInfo card = meld.getCardAt(x,y);
            if(card!=null){
                return card;
            }
        }
        return null;
    }
}