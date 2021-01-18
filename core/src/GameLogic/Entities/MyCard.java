package GameLogic.Entities;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

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

    public MyCard(MyCard aCard){
        this.suit = Suit.getSuit(aCard.suit.index);
        this.rank = Rank.getRank(aCard.rank.index);
    }

    // GETTERS

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MyCard)){
            return false;
        }
        MyCard card = (MyCard) o;
        return (this.rank.index == card.rank.index && this.suit.index == card.suit.index);
    }
    @Override
    public int hashCode() {
        return Suit.values().length*rank.index+suit.index;
    }
    public int ginValue() {
        return rank.ginValue;
    }
    public String toString() {
        return rank.name() + " Of " + suit.name();
    }

    // EXTRA
    public enum Suit {
        Spades("spades", 0),
        Clubs("clubs", 1),
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
            System.out.println("MyCard getSuit() Wrong index "+i);
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
            this.value = index +1;
            this.ginValue = Math.min(value, 10);
        }

        public static Rank getRank(int i) {
            for (Rank rank : values()) {
                if(rank.index ==i){
                    return rank;
                }
            }
            System.out.println("MyCard getRank() Wrong index "+i);
            return null;
        }
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
    /**
     * Creates the basic game deck
     *
     * @return prototype deck: 1 of each card.
     */
    public static Stack<MyCard> getBasicDeck() {
        Stack<MyCard> deck = new Stack<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new MyCard(suit, rank));
            }
        }
        return deck;
    }
}
