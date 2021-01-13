package GameLogic.Entities;

import GameLogic.Logic.Finder;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores all information about a set of melds and the hand that it came from
 * List of melds
 * List of non-meld cards
 * Value of cards part of meld (useless I think)
 * Value of cards not part of a meld (deadwood)
 */
public class HandLayout {
    private int[][] hand;
    private List<Meld> setOfMelds;
    private List<MyCard> unusedCards;
    private int value;
    private int deadwood;

    public HandLayout() {
        setOfMelds = new ArrayList<>();
        unusedCards = new ArrayList<>();
    }
    public HandLayout(int[][] hand, List<Meld> melds) {
        this.hand = hand;
        this.setOfMelds = melds;
        unusedCards = new ArrayList<>();
        init(Finder.copy(hand));
    }
    public HandLayout(List<MyCard> cards) {
        this();
        for (MyCard card : cards) {
            addUnusedCard(card);
        }
    }

    // SETTERS. Ony way to change inner state

    private void init(int[][] hand) {
        for (Meld meld : setOfMelds) {
            for (MyCard myCard : meld.cards()) {
                this.value += myCard.ginValue();
                hand[myCard.suit.index][myCard.rank.index] = 0;
            }
        }
        for (int i = 0; i < hand.length; i++) {
            for (int j = 0; j < hand[i].length; j++) {
                if (hand[i][j] == 1) {
                    MyCard unusedCard = new MyCard(i, j);
                    this.deadwood += unusedCard.ginValue();
                    unusedCards.add(unusedCard);
                }
            }
        }
    }
    public void addUnusedCard(MyCard card) {
        unusedCards.add(card);
        deadwood += card.ginValue();
    }
    public void addMeld(Meld meld) {
        setOfMelds.add(meld);
        value += meld.value();
    }
    public void addToMeld(int i, MyCard card) {
        setOfMelds.get(i).add(card);
        value += card.ginValue();
    }
    public boolean removeUnusedCard(MyCard card) {
        if(unusedCards.remove(card)){
            deadwood -= card.ginValue();
            return true;
        }
        return false;
    }
    public boolean removeCard(MyCard card) {
        if (!removeUnusedCard(card)) {
            for (Meld meld : melds()) {
                if (meld.remove(card)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    // GETTERS. Returns copies to keep HandLayout from changing on the outside

    public int[][] getHand() {
        return Finder.copy(hand);
    }
    public int deadwoodValue() {
        return deadwood;
    }
    public int cardsInDeadwood(){
        return unusedCards.size();
    }
    public int meldValue() {
        return value;
    }
    public double evaluate(){
        List<MyCard> notInHand = MyCard.getBasicDeck();
        notInHand.removeAll(cards());
        int val = 0;
        for (int i = 0; i < notInHand.size(); i++) {
            unusedCards.add(notInHand.get(i));
            val+= Finder.findBestHandLayout(unusedCards).deadwoodValue();
            for (int j = i+1; j < notInHand.size(); j++) {
                unusedCards.add(notInHand.get(j));
                val+= Finder.findBestHandLayout(unusedCards).deadwoodValue();
                unusedCards.remove(notInHand.get(j));
            }
            unusedCards.remove(notInHand.get(i));
        }
        return val;
    }
    public boolean isValid() {
        for (Meld setOfMeld : setOfMelds) {
            if (!setOfMeld.isValid()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof HandLayout)){
            return false;
        }
        HandLayout o = (HandLayout) other;
        if (!MyCard.listEqualsIgnoreOrder(unusedCards, o.unusedCards)) {
            return false;
        }
        List<Meld> cpy = new ArrayList<>(setOfMelds);
        cpy.removeAll(o.setOfMelds);
        return cpy.size()==0;
    }
    public List<MyCard> unused() {
        return new ArrayList<>(unusedCards);
    }
    public List<MyCard> cards() {
        List<MyCard> cards = new ArrayList<>(unusedCards);
        cards.addAll(meldCards());
        return cards;
    }
    public List<MyCard> meldCards(){
        List<MyCard> cards = new ArrayList<>();
        for (Meld meld : setOfMelds) {
            cards.addAll(meld.cards());
        }
        return cards;
    }
    public List<Meld> melds() {
        return Meld.copy(setOfMelds);
    }
    public HandLayout copy() {
        HandLayout m = new HandLayout();
        m.setOfMelds = Meld.copy(setOfMelds);
        m.unusedCards = new ArrayList<>(unusedCards);
        m.deadwood = this.deadwood;
        m.value = this.value;
        m.hand = this.hand;
        return m;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Melds:");
        if (setOfMelds.size() != 0) {
            sb.append(setOfMelds.get(0));
            for (int i = 1; i < setOfMelds.size(); i++) {
                sb.append(" ").append(setOfMelds.get(i));
            }
        }
        sb.append("\n");
        sb.append("Deadwood ").append(deadwood).append(" with cards: ").append(unusedCards);

        return sb.toString();
    }
}
