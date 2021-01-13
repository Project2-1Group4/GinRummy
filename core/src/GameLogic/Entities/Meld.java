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
        if (type == Meld.MeldType.Set) {
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
    @Override
    public boolean equals(Object o) {
        return o instanceof Meld && MyCard.listEqualsIgnoreOrder(meld, ((Meld) o).meld);
    }
    public Meld copy() {
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

    // SETTERS

    public void add(MyCard card) {
        value += card.ginValue();
        meld.add(card);
    }
    public boolean remove(MyCard card) {
        if(meld.remove(card)){
            value-= card.ginValue();
            return true;
        }
        return false;
    }
    public void setType() {
        // Very simple. Only accurate if meld is actually valid
        type = meld.get(0).suit == meld.get(1).suit ? MeldType.Set : MeldType.Run;
    }
    public void sortByRank() {
        // Insertion sort
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

    // EXTRA

    public enum MeldType {
        Set,
        Run
    }
    public static List<Meld> copy(List<Meld> setOfMelds) {
        List<Meld> melds = new ArrayList<>();
        for (Meld setOfMeld : setOfMelds) {
            melds.add(setOfMeld.copy());
        }
        return melds;
    }
}
