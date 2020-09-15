package gameHandling;
import cardlogic.Card;
import cardlogic.Card.SUITS;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private SetOfCards hand;
    private int score;

    public Player(String name) {
        this(name, new SetOfCards());
    }
    
    public Player(SetOfCards cards) {
    	this("player",cards);
    }
    
    public Player(String name, SetOfCards cards) {
    	this.name = name;
    	this.hand = cards;
    	this.score = 0;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<List<Card>> findSets(){
        List<List<Card>> listList = new ArrayList<List<Card>>();

        this.hand.sortByValue();

        int prev = 0;

        int count = 0;

        for(int i=0; i<this.hand.getCardSetSize();i++) {
            Card aCard = this.hand.getCard(i);

            if(aCard.getValue() == prev) {
                count++;
            } else {

                // TODO: Bugtest this thingy, cause the logic is rather wonky for me

                // Okay, so the idea is that if there's a count>=2 then there's at least 3 cards in common
                // We then find the index of the first card there, and add the n cards to the temp list
                // Then save the templist into the listlist
                // So what we're going to do is go ahead and store all of the previous cards in the listList

                if(count >= 2) {
                    int startPoint = i-count-1;
                    List<Card> tempList = new ArrayList<>();

                    for(int j=0; j<count+1;j++) {
                        tempList.add(this.hand.getCard(j+startPoint));
                    }

                    listList.add(tempList);


                }

                prev = aCard.getValue();
                count = 0;
            }


        }

        if(listList.size() == 0) {
            return null;
        } else {
            return listList;
        }

    }

    public int getHandSize() {
        return hand.size();
    }

    /* getcard
    setcard
     */
    public int getScore() {
        return this.score;
    }
    public void evaluateScore(int value) { //subtract or add score in case of loss or win
        score += value;
    }
    
    public void setHand(SetOfCards cards) {
    	this.hand = cards;
    }



    public List<List<Card>> findRuns(){
        this.hand.sortBySuits();

        List<List<Card>> listList = new ArrayList<List<Card>>();

        SUITS prevSuit = null;
        int prevVal = 0;
        int count = 0;

        for(int i=0; i<this.hand.getCardSetSize(); i++) {
            Card aCard = this.hand.getCard(i);

            if(aCard.getSuit() != prevSuit) {

                // I use this if statement like 3 times, I should probably make it a method
                // TODO: Clean up code and make this a separate method
                if(count >= 2) {
                    int startPoint = i-count-1;
                    List<Card> tempList = new ArrayList<>();

                    for(int j=0; j<count+1;j++) {
                        tempList.add(this.hand.getCard(j+startPoint));
                    }

                    listList.add(tempList);
                }


                prevSuit = aCard.getSuit();
                prevVal = aCard.getValue();
                count = 0;
                continue;
            }

            if( (aCard.getValue()-prevVal) == 1 ) {
                count++;
            } else {

                if(count >= 2) {
                    int startPoint = i-count-1;
                    List<Card> tempList = new ArrayList<>();

                    for(int j=0; j<count+1;j++) {
                        tempList.add(this.hand.getCard(j+startPoint));
                    }

                    listList.add(tempList);


                }


                count = 0;
            }

            prevVal = aCard.getValue();

        }

        if(listList.size() == 0) {
            return null;
        } else {
            return listList;
        }

    }
    
    public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true);
		
		Player aPlayer = new Player("player",SetOfCards.handOutCard(10));
		
		List<List<Card>> runs = aPlayer.findRuns();
		
		List<List<Card>> sets = aPlayer.findSets();
		
		deck.shuffleCards();
		
		System.out.println(aPlayer.hand);
		
	}
    
}
