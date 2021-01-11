package temp.GameLogic.Entities;

import cardlogic.Card;

import java.util.ArrayList;
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

    // TODO: Make sure that these deep copies are properly done
    public MyCard(MyCard aCard){
        this.suit = Suit.getSuit(aCard.suit.index);
        this.rank = Rank.getRank(aCard.rank.index);
    }

    // GETTERS
    public boolean equals(Object o){
        return o instanceof MyCard && ((MyCard) o).suit == this.suit && ((MyCard) o).rank == this.rank;
    }

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
            this.value = index +1;
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

    public static boolean remove(List<MyCard> list, MyCard card){
        for (int i = 0; i < list.size(); i++) {
            if(card.same(list.get(i))){
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    public static boolean has(List<MyCard> list, MyCard card){
        for (MyCard myCard : list) {
            if (card.same(myCard)) {
                return true;
            }
        }
        return false;
    }

    public static List<MyCard> intraListDifference(List<MyCard> c1, List<MyCard> c2){
        List<MyCard> cardDiff = new ArrayList<>();
        for (MyCard card : c1) {
            boolean found = false;
            for (MyCard myCard : c2) {
                if (card.same(myCard)) {
                    found = true;
                    break;
                }
            }
            if (!found) cardDiff.add(card);
        }
        return cardDiff;
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

    @Override
    public boolean equals(Object o) {
        MyCard card = (MyCard) o;
        return (this.rank.index == card.rank.index && this.suit.index == card.suit.index);
    }

}
