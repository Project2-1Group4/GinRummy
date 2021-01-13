package GameLogic.Entities;

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

    // SETTERS

    public void add(MyCard card) {
        value += card.ginValue();
        meld.add(card);
    }
    public boolean remove(MyCard card) {
        for (int i = 0; i < meld.size(); i++) {
            if (card.same(meld.get(i))) {
                value -= meld.get(i).ginValue();
                meld.remove(i);
                return true;
            }
        }
        return false;
    }
    public void setType() {
        type = meld.get(0).suit == meld.get(1).suit ? MeldType.Run : MeldType.Set;
    }

    // USES INSERTION SORT
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

    // GETTERS. Returns copies to keep state from being modified outside

    public int value() {
        return value;
    }
    public int size() {
        return meld.size();
    }
    public List<MyCard> cards() {
        return new ArrayList<>(meld);
    }
    public MeldType type() {
        if (type == null) {
            setType();
        }
        return type;
    }
    public boolean isValid() {
        setType();
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
    public boolean isValidWith(MyCard card) {
        add(card);
        boolean valid = isValid();
        remove(card);
        return valid;
    }

    public List<MyCard> viewMeld() {
        return new ArrayList<>(meld);
    }
    public boolean same(Meld other) {
        int found = 0;
        for (MyCard card : meld) {
            for (MyCard myCard : other.meld) {
                if (card.same(myCard)) {
                    found++;
                }
            }
        }
        return found == meld.size();
    }
    public Meld deepCopy() {
        Meld m = new Meld();
        m.type = this.type;
        m.meld = new ArrayList<>(meld);
        return m;
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

    // EXTRA

    public enum MeldType {
        Set,
        Run
    }
    public static List<Meld> deepCopy(List<Meld> setOfMelds) {
        List<Meld> melds = new ArrayList<>();
        for (Meld setOfMeld : setOfMelds) {
            melds.add(setOfMeld.deepCopy());
        }
        return melds;
    }
}
