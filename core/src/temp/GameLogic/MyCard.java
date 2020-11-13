package temp.GameLogic;

import java.util.ArrayList;
import java.util.List;

// Simple card class. IMMUTABLE
public class MyCard {
    public final Suit suit;
    public final Rank rank;

    public MyCard(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public MyCard(int i, int j) {
        this.suit = Suit.getSuit(i);
        this.rank = Rank.getRank(j);
    }

    // GETTERS
    public boolean same(MyCard card) {
        return card.suit == this.suit && card.rank == this.rank;
    }

    public int ginValue() {
        return rank.ginValue;
    }

    public String toString() {
        return rank.name() + " Of " + suit.name();
    }

    // EXTRA
    public enum Suit {
        Clubs("clubs", 0),
        Spades("spades", 1),
        Hearts("hearts", 2),
        Diamonds("diamonds", 3);
        public int index;
        public String value;

        Suit(String value, int index) {
            this.value = value;
            this.index = index;
        }

        public static Suit getSuit(int i) {
            return values()[i];
        }
    }

    public enum Rank {
        Ace(0),
        Two(1),
        Three(2),
        Four(3),
        Five(4),
        Six(5),
        Seven(6),
        Eight(7),
        Nine(8),
        Ten(9),
        Jack(10),
        Queen(11),
        King(12);
        public int index;
        public int value;
        public int ginValue;

        Rank(int index) {
            this.index = index;
            this.value = index + 1;
            this.ginValue = Math.min(value, 10);
        }

        public static Rank getRank(int i) {
            return values()[i];
        }
    }

    public static String toString(List<MyCard> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append(cards.get(0));
        for (int i = 1; i < cards.size(); i++) {
            sb.append(" ").append(cards.get(i));
        }
        return sb.toString();
    }

    /**
     * Creates the basic game deck
     *
     * @return prototype deck: 1 of each card.
     */
    public static List<MyCard> getBasicDeck() {
        List<MyCard> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new MyCard(suit, rank));
            }
        }
        return deck;
    }
}
