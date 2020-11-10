package temp.GameLogic.MELDINGOMEGALUL;

import temp.GameLogic.MyCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Easy way to store melds I guess.
 */
public class Meld {
    private int value;
    private List<MyCard> meld;
    private MeldType type;

    public Meld() {
        meld = new ArrayList<>();
    }

    public void addCard(MyCard card) {
        value+= card.ginValue();
        meld.add(card);
    }

    public boolean removeCard(MyCard card){
        if(meld.remove(card)){
            value-= card.ginValue();
            return true;
        }
        return false;
    }

    public void setType() {
        type = meld.get(0).suit == meld.get(1).suit ? MeldType.Run : MeldType.Set;
    }

    public int getValue(){
        return value;
    }

    public List<MyCard> viewMeld(){
        return new ArrayList<>(meld);
    }

    public MeldType getType() {
        if (type == null) {
            setType();
        }
        return type;
    }

    public Meld clone(){
        Meld m = new Meld();
        m.type = this.type;
        m.meld = new ArrayList<>(meld);
        return m;
    }


    /**
     * Uses insertion sort
     **/
    public void sort() {
        sortByRank();
        sortBySuit();
    }

    public void sortByRank() {
        for (int i = 0; i < meld.size(); i++) {
            for (int j = i; j > 0; j--) {
                MyCard cardJ = meld.get(j);
                MyCard cardJneg1 = meld.get(j - 1);
                if (cardJ.rank.index < cardJneg1.rank.index) {
                    meld.remove(cardJneg1);
                    meld.add(j, cardJneg1);
                }
            }
        }
    }

    public void sortBySuit() {
        for (int i = 0; i < meld.size(); i++) {
            for (int j = i; j > 0; j--) {
                MyCard cardJ = meld.get(j);
                MyCard cardJneg1 = meld.get(j - 1);
                if (cardJ.suit.index < cardJneg1.suit.index) {
                    meld.remove(cardJneg1);
                    meld.add(j, cardJneg1);
                }
            }
        }
    }

    public enum MeldType {
        Set,
        Run
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(meld.get(0));
        for (int i = 1; i < meld.size(); i++) {
            sb.append(", ").append(meld.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public static List<Meld> clone(List<Meld> setOfMelds) {
        List<Meld> melds = new ArrayList<>();
        for (Meld setOfMeld : setOfMelds) {
            melds.add(setOfMeld.clone());
        }
        return melds;
    }
}
