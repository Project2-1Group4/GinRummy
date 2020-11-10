package temp.GameLogic.MELDINGOMEGALUL;

import temp.GameLogic.MyCard;

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
        init(Calculator.copy(hand));
    }

    private void init(int[][] hand){
        for (Meld meld : setOfMelds) {
            for (MyCard myCard : meld.viewMeld()) {
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

    public int[][] getHand(){
        return Calculator.copy(hand);
    }

    public void addUnusedCard(MyCard card){
        unusedCards.add(card);
        deadwood+= card.ginValue();
    }

    public void addMeld(Meld meld){
        setOfMelds.add(meld);
        value+= meld.getValue();
    }

    public void addToMeld(int i, MyCard card){
        setOfMelds.get(i).addCard(card);
        value+= card.ginValue();
    }

    public boolean removeUnusedCard(MyCard card){
        if(unusedCards.remove(card)) {
            deadwood -= card.ginValue();
            return true;
        }
        return false;
    }

    public boolean removeCard(MyCard card){
        if(!removeUnusedCard(card)){
            for (Meld meld : viewMelds()) {
                if(meld.removeCard(card)){
                    return true;
                }
            }
        }
        return true;
    }

    public int getDeadwood(){
        return deadwood;
    }

    public int getValue(){
        return value;
    }

    public List<MyCard> viewUnusedCards(){
        return new ArrayList<>(unusedCards);
    }

    public List<MyCard> viewAllCards(){
        List<MyCard> cards = new ArrayList<>(unusedCards);
        for (Meld setOfMeld : setOfMelds) {
            cards.addAll(new ArrayList<>(setOfMeld.viewMeld()));
        }
        return cards;
    }

    public List<Meld> viewMelds(){
        return Meld.deepCopy(setOfMelds);
    }

    public boolean isValid(){
        for (Meld setOfMeld : setOfMelds) {
            if(!setOfMeld.isValid()){
                return false;
            }
        }
        return true;
    }

    public boolean same(HandLayout other){
        int found =0;
        for (MyCard unusedCard : unusedCards) {
            for (MyCard card : other.unusedCards) {
                if(unusedCard.same(card)){
                    found++;
                }
            }
        }
        if(found!=unusedCards.size()){
            return false;
        }
        for (Meld meld1 : setOfMelds) {
            for (Meld meld2 : other.setOfMelds) {
                if(!meld1.same(meld2)){
                    return false;
                }
            }
        }
        return true;
    }

    public HandLayout deepCopy(){
        HandLayout m = new HandLayout();
        m.setOfMelds = Meld.deepCopy(setOfMelds);
        m.unusedCards = new ArrayList<>(unusedCards);
        m.deadwood = this.deadwood;
        m.value = this.value;
        m.hand = this.hand;
        return m;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Value ").append(value).append(" with melds:\n");
        if (setOfMelds.size() != 0) {
            sb.append(setOfMelds.get(0));
            for (int i = 1; i < setOfMelds.size(); i++) {
                sb.append(" ").append(setOfMelds.get(i));
            }
            sb.append("\n");
        }
        sb.append("Deadwood ").append(deadwood).append(" with cards:\n");
        sb.append(MyCard.toString(unusedCards));
        return sb.toString();
    }
}
