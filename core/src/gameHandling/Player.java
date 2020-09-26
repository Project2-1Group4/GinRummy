package gameHandling;
import cardlogic.Card;
import cardlogic.Card.SUITS;
import cardlogic.SetOfCards;

import java.util.*;

public class Player {
    private String name;
    private SetOfCards hand;
    private int score;

    private int bestValueCombination;
    private List<List<Card>> bestCombination;
    private List<List<Card>> permutations;


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

    public void recursiveSearch(List<Card> sequence, List<List<Card>> removed){
        // have a loop over the set permutations

        // inside the loop remove the current permutation (set) from a deep copy of the sequence (not a reference)

        //make sure that you can remove the set (if you cant remove the set it's not a valid final sequence, since you would remove too little cards)
        //so you skip those.
        //add the permutation to a deep copy of the removed (newRemoved)


        //calculate the value; value = sum_of_values(removed) + sum_of_values(this.findruns(sequence))
        //update the best value and best value combination if you get a higher value then that.
        //call the recursiveSearch again with the new sequence and new removed.
        //Note: (make also sure that there is in the loop at least one empty sequence, that doesnt remove anything)

        SetOfCards SequenceSet = new SetOfCards();
        SequenceSet.fromList(copyList(sequence));


        int Value = valueOfLists(findRuns(SequenceSet)) + valueOfLists(copyListOfList(removed));

        if(Value > this.bestValueCombination){
            List<List<Card>> newCombination = new ArrayList<>();
            newCombination.addAll(copyListOfList(findRuns(SequenceSet)));
            newCombination.addAll(copyListOfList(removed));
            this.bestCombination = newCombination;
            this.bestValueCombination = Value;

        }


        for (int i = 0; i < permutations.size(); i++) {
            if(sequence.containsAll(permutations.get(i))){

                List<Card> newSequence = copyList(sequence);
                newSequence.removeAll(copyList(permutations.get(i)));

                List<List<Card>> newRemoved = copyListOfList(removed);
                newRemoved.add(permutations.get(i));

                SetOfCards newSequenceSet = new SetOfCards();
                newSequenceSet.fromList(newSequence);

                int newValue = valueOfLists(findRuns(newSequenceSet)) + valueOfLists(newRemoved);

                if(newValue > this.bestValueCombination){
                    List<List<Card>> newCombination = new ArrayList<>();
                    newCombination.addAll(copyListOfList(findRuns(newSequenceSet)));
                    newCombination.addAll(copyListOfList(newRemoved));
                    this.bestCombination = newCombination;
                    this.bestValueCombination = newValue;

                }
                this.recursiveSearch(newSequence,newRemoved);

            }
        }

    }

    /*
    This method is used to find the optimal combination between runs and sets having mutual cards
    The optimal solution will be saved in the bestCombination
     */
    public void bestCombination() {

        //setup
        this.bestValueCombination = 0;
        this.bestCombination = new ArrayList<>();
        this.permutations = this.getPermutation(this.findSets(this.hand));
        List<List<Card>> removed = new ArrayList<>();
        //run recursion
        this.recursiveSearch(this.hand.toList(), removed);

        System.out.println(this.bestCombination);
        System.out.println(this.bestValueCombination);
    }

    public int valueOfLists(List<List<Card>> cards){
        int sumValue = 0;
        for(List<Card> list:cards){
            sumValue += valueInRunOrSet(list);
        }
        return sumValue;
    }

    public List<List<Card>> getPermutation(List<List<Card>> listSets) {
        for (int i = 0; i < listSets.size(); i++) {
            if (listSets.get(i).size()>3) {

                for (int j = 0; j < 4; j++) {
                    List<Card> set = copyList(listSets.get(i));

                    this.remove(set,j);
                    listSets.add(i+1,set);
                    //System.out.println(permutation.get(index))
                }
            }
        }
        return listSets;
    }

    public void remove(List<Card> card, int i) {
        card.remove(i);
    }

    public static List<List<Card>> copyListOfList(List<List<Card>> card) {
        List<List<Card>> newCard = new ArrayList<>();
        for (int i = 0; i < card.size(); i++) {
            newCard.add(copyList(card.get(i)));
        }
        return newCard;
    }

    public static List<Card> copyList(List<Card> card) {
        List<Card> newCard = new ArrayList<>();
        for (int i = 0; i < card.size(); i++) {
            newCard.add(card.get(i));
        }
        return newCard;
    }

    public static int valueInRunOrSet(List<Card> listCard) {
        int score = 0;
        for (int i = 0; i < listCard.size(); i++) {
        	score = score + listCard.get(i).getGinRummyValue();
        }
        return score;
    }

    public List<List<Card>> findSets(SetOfCards handOfCards){
        List<List<Card>> listList = new ArrayList<List<Card>>();

        handOfCards.sortByValue();

        int prev = 0;

        int count = 0;

        for(int i=0; i<handOfCards.getCardSetSize();i++) {
            Card aCard = handOfCards.getCard(i);

            if(aCard.getValue() == prev) {
                count++;
            } else {

                // TODO: Bugtest this thingy, cause the logic is rather wonky for me


                if(count >= 2) {

                    List<Card> tempList = getSetOrRun(i, count);

                    listList.add(tempList);
                }

                prev = aCard.getValue();
                count = 0;
            }


        }

        return listList;

    }

    public List<List<Card>> findRuns(SetOfCards handOfCards){
        handOfCards.sortBySuits();

        List<List<Card>> runs = new ArrayList<>();

        List<Card> currentRun = new ArrayList<>();
        currentRun.add(handOfCards.getCard(0));
        for(int i = 1; i < handOfCards.getCardSetSize();i++){
            if(handOfCards.getCard(i).getSuit().equals(currentRun.get(currentRun.size()-1).getSuit())){
                if(currentRun.isEmpty()){
                    currentRun.add(handOfCards.getCard(i));
                }else{
                    if(currentRun.get(currentRun.size()-1).getValue()+1 == handOfCards.getCard(i).getValue()){
                        currentRun.add(handOfCards.getCard(i));
                    }else{
                        if(currentRun.size() >= 3){
                            runs.add(currentRun);
                        }
                        currentRun = new ArrayList<>();
                        currentRun.add(handOfCards.getCard(i));
                    }
                }

            }else{
                if(currentRun.size() >= 3){
                    runs.add(currentRun);
                }
                currentRun = new ArrayList<>();
                currentRun.add(handOfCards.getCard(i));
            }


        }
        return runs;
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
                // TODO: Clean up code and make this a separate method. DONE
                if(count >= 2) {

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

    
    /*
     * Somehow another method will take care of finding the optimal combination of runs and melds
     * This method will just remove those cards, and then return a list with the remaining deadwood
     */
    
    public List<Card> findDeadwood(){
    	List<Card> deadWood = new ArrayList<>();
    	List<Card> handCard = this.hand.toList();
    	this.bestCombination();
    	for (List<Card> card : this.bestCombination) {
            deadWood.addAll(copyList(card));
        }
    	for (List<Card> card : this.bestCombination) {
    	    if (handCard.containsAll(card)) {
    	        deadWood.removeAll(card);
            }
        }
    	return deadWood;
    	
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
     *  I'm currently doing it internally, so there should be no problems with that
     *  TODO: Modify method so that the bonuses can be added at the end of a turn
     */
    /*
    public int scoreHand() {
    	List<List<Card>> runs = this.findRuns();
    	List<List<Card>> sets = this.findSets();
    	
    	this.compareScore(runs, sets);
    	
    	List<Card> resultingCards = new ArrayList<Card>();
    	
    	for(List<Card> aRun:runs) {
    		resultingCards.addAll(aRun);
    	}
    	
    	for(List<Card> aSet:sets) {
    		resultingCards.addAll(aSet);
    	}
    	
    	List<Card> deadwood = this.findDeadwood(resultingCards);
    	
    	// I also need to find a way to take the deadwood out in some capacity
    	// As it's convenient for the player that didn't know, so that I can add the deadwood to any other sets
    	// TODO: Modify this so that the player that didn't knock can lose some of the deadwood
    	
    	//int score = SetOfCards.scoreGinRummy(deadwood);
    	
    	return score;
    	
    }

     */
    
    /*
     * Some method that has listeners and stuff
     * It'll add the card to the player's hand, and then ask them to discard a card
     * If the returned card is the given card, then there's no changes
     * 
     * For now, I'm just returning the given card
     * So the game will always discard the given card
     * 
     * TODO: Add the listeners
     * 
     */
    public Card chooseCardToDiscard(Card aCard) {
    	
    	return aCard;
    }
    
    
    /*
     * Listener to get whether to get the deck or from the discard pile
     * 
     * If false:
     * 	then the player chose the deck
     * if true:
     * 	player chose the card from the discard pile
     * 
     * Currently the player only chooses from the deck
     * 
     * TODO: Add the listeners for the game
     * 
     */
    
    public boolean chooseDeckOrPile() {
    	return false;
    }
    
    /*
     * Listener to get whether the player chose to knock or not
     * 
     * If false:
     * 	Player didn't knock, so goes on
     * If true:
     * 	Player knocked, so cards are down and next phase of game happens
     */
    
    public boolean chooseToKnock() {
    	return false;
    }
    
    public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true);
        deck.shuffleCards();
		
		Player aPlayer = new Player("player",SetOfCards.handOutCard(20, deck));
		//aPlayer.hand.sortBySuitAndValue();
        //System.out.println(aPlayer.hand);

		List<List<Card>> runs = aPlayer.findRuns();
        System.out.println("runs: \n " + runs);

		List<List<Card>> sets = aPlayer.findSets();
        System.out.println("sets: \n" + sets);

        System.out.println("hand: "+ aPlayer.hand + "\n");

        aPlayer.bestCombination();


		
	}
    
}
