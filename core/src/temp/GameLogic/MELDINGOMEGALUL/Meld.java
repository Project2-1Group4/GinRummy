package temp.GameLogic.MELDINGOMEGALUL;

import temp.GameLogic.MyCard;
import temp.GameLogic.Validator;

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

    /* SETTERS */
    public void addCard(MyCard card) {
        value+= card.ginValue();
        meld.add(card);
    }

    public boolean removeCard(MyCard card){
        for (int i = 0; i < meld.size(); i++) {
            if(card.same(meld.get(i))){
                value-= meld.get(i).ginValue();
                meld.remove(i);
                return true;
            }
        }
        return false;
    }

    public void setType() {
        type = meld.get(0).suit == meld.get(1).suit ? MeldType.Run : MeldType.Set;
    }

    /* GETTERS */
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

    public boolean isValid(){
        // If set
        if (type == Meld.MeldType.Run) {
            for (int i = 1; i < meld.size() - 1; i++) {
                if (meld.get(i).suit != meld.get(i + 1).suit) {
                    return false;
                }
            }
        }
        // Else run
        else {
            sortByRank();
            for (int i = 1; i < meld.size() - 1; i++) {
                if (meld.get(i).rank != meld.get(i + 1).rank &&
                        meld.get(i).rank.value != meld.get(i + 1).rank.value + 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidWith(MyCard card){
        addCard(card);
        boolean valid = isValid();
        removeCard(card);
        return valid;
    }

    public boolean same(Meld other){
        int found = 0;
        for (MyCard card : meld) {
            for (MyCard myCard : other.meld) {
                if(card.same(myCard)){
                    found++;
                }
            }
        }
        return found == meld.size();
    }

    /* INSERTION SORT */
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

    /* EXTRA */
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

    public Meld deepCopy(){
        Meld m = new Meld();
        m.type = this.type;
        m.meld = new ArrayList<>(meld);
        return m;
    }

    public static List<Meld> deepCopy(List<Meld> setOfMelds) {
        List<Meld> melds = new ArrayList<>();
        for (Meld setOfMeld : setOfMelds) {
            melds.add(setOfMeld.deepCopy());
        }
        return melds;
    }
}
