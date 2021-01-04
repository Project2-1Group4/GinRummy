package temp.GameLogic;

import cardlogic.Card;

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

    // TODO: Make sure that these deep copies are properly done
    public MyCard(MyCard aCard){
        this.suit = Suit.getSuit(aCard.suit.index);
        this.rank = Rank.getRank(aCard.rank.value);
    }

    public MyCard(Card aCard){
        this.suit = Suit.getSuit(aCard.getSuitVal());
        this.rank = Rank.getRank(aCard.getValue()-1) ;
    }

    // GETTERS
    public boolean same(MyCard card) {
        return card.suit == this.suit && card.rank == this.rank;
    }

    public int getIndex() {
        return suit.index * (Rank.values().length) + (rank.index);
    }

    public int ginValue() {
        return rank.ginValue;
    }

    public String toString() {
        return rank.name() + " Of " + suit.name();
    }

    // EXTRA
    public enum Suit {
        Clubs("clubs", 1),
        Spades("spades", 0),
        Hearts("hearts", 2),
        Diamonds("diamonds", 3);
        public int index;
        public String value;

        Suit(String value, int index) {
            this.value = value;
            this.index = index;
        }

        public static Suit getSuit(int i) {
            for (Suit suit : values()) {
                if(suit.index==i){
                    return suit;
                }
            }
            System.out.println("MyCard l65 Wrong index");
            return null;
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
            for (Rank rank : values()) {
                if(rank.index ==i){
                    return rank;
                }
            }
            System.out.println("MyCard l99 Wrong index");
            return null;
        }
    }

    public static String toString(List<MyCard> cards) {
        StringBuilder sb = new StringBuilder();
        if (cards.size() != 0) {
            sb.append(cards.get(0));
            for (int i = 1; i < cards.size(); i++) {
                sb.append(" ").append(cards.get(i));
            }
        }
        return sb.toString();
    }

    public static int[] getIndices(int index) {
        int rank = index % Rank.values().length;
        return new int[]{
                (index - rank) / Rank.values().length,
                rank
        };
    }

    public static int getIndex(Suit s, Rank r) {
        return getIndex(s.index, r.index);
    }

    public static int getIndex(int i, int j) {
        return new MyCard(i, j).getIndex();
    }

    public static MyCard getCard(int index) {
        int rank = index % Rank.values().length;
        int suit = (index - rank) / Rank.values().length;
        return new MyCard(suit, rank);
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

    @Override
    public boolean equals(Object o) {
        MyCard card = (MyCard) o;
        return (this.rank.index == card.rank.index && this.suit.index == card.suit.index);
    }

}
