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

    public static int scoreInRunOrSet(List<Card> listCard) {
        int score = 0;
        for (int i = 0; i < listCard.size(); i++) {
            /*
             * It should be ginRummy value, as normal value makes
             * K=13, and stuff like that. Which is not desired
             */
        	score = score + listCard.get(i).getValue();
        }
        return score;
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
                    /*
                    int startPoint = i-count-1;
                    List<Card> tempList = new ArrayList<>();

                    for(int j=0; j<count+1;j++) {
                        tempList.add(this.hand.getCard(j+startPoint));
                    }
                     */

                    List<Card> tempList = getSetOrRun(i, count);

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
                // TODO: Clean up code and make this a separate method. DONE
                if(count >= 2) {
                   /*
                    int startPoint = i-count-1;
                    List<Card> tempList1 = new ArrayList<>();

                    for(int j=0; j<count+1;j++) {
                        tempList1.add(this.hand.getCard(j+startPoint));
                    }
                    */

                    List<Card> tempList = getSetOrRun(i, count);

                    listList.add(tempList);
                }


                prevSuit = aCard.getSuit();
                prevVal = aCard.getValue();
                count = 0;
                continue;
            }

            if((aCard.getValue()-prevVal) == 1 ) {
                count++;
            } else {

                if(count >= 2) {

                    List<Card> tempList = getSetOrRun(i, count);

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

    public List<Card> getSetOrRun(int index, int count) {
        int startPoint = index - count - 1;
        List<Card> tempList = new ArrayList<>();

        for (int j=0; j<count+1; j++) {
            tempList.add(this.hand.getCard(j + startPoint));
        }
        return tempList;
    }

    //This method is to decide whether a card should be in a run or set if any run and set has one mutual card
    /*Need to be improved
        For example: a run contains two cards that are in 2 separated sets or vice versa
     */
    public static void compareScore(List<List<Card>> listRuns, List<List<Card>> listSets) {

        for (int i = 0; i < listRuns.size(); i++) {
            List<Card> listRun = listRuns.get(i);

            int scoreRun = scoreInRunOrSet(listRun);

            for (int j = 0; j < listRun.size(); j++) {

                for (int ii = 0; ii < listSets.size(); ii++) {
                    List<Card> listSet = listSets.get(ii);
                    int scoreSet = scoreInRunOrSet(listSet);

                    for (int jj = 0; jj < listSet.size(); jj++) {
                        if (listRun.get(j).equals(listSet.get(jj))) {
                            if (scoreRun >= scoreSet) {
                                listSets.remove(ii);
                            }
                            else {
                                listRuns.remove(i);
                            }
                            break;
                        }
                        else
                            continue;
                    }
                }
            }
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
    
    
    /*
     *  Finds the score of a given hand
     *  Needs to be modified to receive which cards are already in a run or a set
     */
    void scoreHand() {
    	
    }




    
    public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true);
        deck.shuffleCards();
		
		Player aPlayer = new Player("player",SetOfCards.handOutCard(20, deck));
		aPlayer.hand.sortByValue();
		
		List<List<Card>> runs = aPlayer.findRuns();
        System.out.println(runs);

		List<List<Card>> sets = aPlayer.findSets();
        System.out.println(sets);

        compareScore(runs, sets);

        System.out.println(runs);
        System.out.println(sets);
        System.out.println(aPlayer.hand);
        //System.out.println(aPlayer.hand.getCard(1).getSuit());


		
	}
    
}
