package gameHandling;
import cardlogic.Card;
import cardlogic.Card.SUITS;
import cardlogic.SetOfCards;

import java.util.*;

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

    public void bestCombination() {
        List<List<Card>> listSets = this.findSets();
        List<List<Card>> listRuns = this.findRuns();

        HashMap<Integer[], Card> mutualCards = this.getMutualCards(listSets, listRuns);


        HashMap<Integer, List<Integer>> dependOnSets = new HashMap<>(); //index set: {setIndex, setIndex,setIndex}



        for (Map.Entry<Integer[], Card> entry : mutualCards.entrySet()) {
            int runIndex = entry.getKey()[0];
            int setIndex = entry.getKey()[1];

            if(dependOnSets.containsKey(runIndex)){
                dependOnSets.get(runIndex).add(setIndex);

            }else {
                List<Integer> setIndices = new ArrayList<>();
                setIndices.add(setIndex);
                dependOnSets.put(runIndex, setIndices);
            }
        }

        //sort for good messure
        for(Map.Entry<Integer,List<Integer>> entry: dependOnSets.entrySet()){
            Collections.sort(entry.getValue());
        }


        //extend dependance
        List<Integer> mergedKeys = new ArrayList<>();
        HashMap<Integer, List<Integer>> dependOnRuns = new HashMap<>(); //index set: {runIndex, runIndex, runIndex}



        for(Map.Entry<Integer, List<Integer>> dependence: dependOnSets.entrySet()){
            for(Map.Entry<Integer, List<Integer>> dependenceInnerLoop: dependOnSets.entrySet()){
                List<Integer> setTarget =dependence.getValue();

                int targetKey = dependenceInnerLoop.getKey();

                if(setTarget.equals(dependenceInnerLoop.getValue()) && dependence.getKey() != dependenceInnerLoop.getKey()){

                    if(dependOnRuns.containsKey(dependence.getKey())){
                        dependOnRuns.get(dependence.getKey()).add(targetKey);

                    }else{
                        if(!mergedKeys.contains(dependence.getKey())){
                            mergedKeys.add(targetKey);
                        }

                        ArrayList<Integer> runs = new ArrayList<>();
                        runs.add(targetKey);
                        dependOnRuns.put(dependence.getKey(),runs);
                    }

                }
            }

        }


        //loop over everything and remove merged ones
        for(int i = 0; i < mergedKeys.size(); i++){
            dependOnRuns.remove(mergedKeys.get(i));
            dependOnSets.remove(mergedKeys.get(i));
        }

        //debug
        //sets
        System.out.println("\n");

        System.out.println("Chosen after sets!!!");
        for(Map.Entry<Integer, List<Integer>> sets: dependOnSets.entrySet()){
            System.out.println("run: " + sets.getKey() + " influence on sets : " + sets.getValue());

        }
        //runs
        System.out.println("Chosen after runs!!");
        for(Map.Entry<Integer, List<Integer>> runs: dependOnRuns.entrySet()){
            System.out.println("run: " + runs.getKey() + " influence on runs : " + runs.getValue());

        }

        //deep copy runs and sets

        List<List<Card>> listSetsNew = this.findSets();
        List<List<Card>> listRunsNew = this.findRuns();


        //finalizing
        if(!dependOnSets.isEmpty()){

            for(Integer key:dependOnSets.keySet()) {
                int runsSum = 0;
                int setSum = 0;
                //calc length of (run) key
                runsSum += valueInRunOrSet(listRuns.get(key));

                //calc length of dependent runs
                if (!dependOnRuns.isEmpty()) {
                    for (Integer index : dependOnRuns.get(key)) {
                        runsSum += valueInRunOrSet(listRuns.get(index));
                    }
                }

                //calc length of related sets
                if (!dependOnSets.isEmpty()) {
                    for (Integer index : dependOnSets.get(key)) {
                        setSum += valueInRunOrSet(listSets.get(index));
                    }
                }
                //compare
                System.out.println("runSum: " +  runsSum);
                System.out.println("setSum: "+ setSum);

                if(runsSum < setSum){
                    if(listRuns.get(key).size() <= 3){
                        listRunsNew.remove(listRuns.get(key));
                    }else{
                        //remove one card have to compensate for being in the middle (and if at the edges have to just remove 1 card)

                    }

                    if(!dependOnRuns.isEmpty()){
                        for(Integer index : dependOnRuns.get(key)) {
                            if(listRuns.get(index).size() <= 3){
                                listRunsNew.remove(listRuns.get(index));
                            }else{
                                //remove one card have to compensate for being in the middle (and if at the edges have to just remove 1 card)



                            }

                        }
                    }

                }else{
                    for (Integer index : dependOnSets.get(key)) {
                        if (listSets.get(index).size() <= 3) {
                            listSetsNew.remove(listSets.get(index));
                        }else{


                            Card c = mutualCards.get(new Integer[] {index,key});
                            System.out.println("card: " + c);
                            listSets.get(index).remove(c);

                        }
                    }
                }
            }
            System.out.println("Chosen card: ");
            System.out.println(listRunsNew);
            System.out.println(listSetsNew);
        }


    }



    public static int valueInRunOrSet(List<Card> listCard) {
        int score = 0;
        for (int i = 0; i < listCard.size(); i++) {
        	score = score + listCard.get(i).getGinRummyValue();
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
    this method is used for check if the set of runs and the sets of suits have the mutual card
    Return the hashmap of mutual card, keep track of the index of which run and set has the same card.
    EX:
     */
    public HashMap<Integer[], Card> getMutualCards(List<List<Card>> setSets, List<List<Card>> setRuns) {
        HashMap<Integer[], Card> mutualCard = new HashMap<>(); // first element in array is index of runs, second is index of sets

        for (int i = 0; i < setRuns.size(); i++) {
            List<Card> listRun = setRuns.get(i);

            for (int j = 0; j < listRun.size(); j++) {

                for (int ii = 0; ii < setSets.size(); ii++) {
                    List<Card> listSet = setSets.get(ii);

                    for (int jj = 0; jj < listSet.size(); jj++) {
                        if (listRun.get(j).equals(listSet.get(jj))) {
                            mutualCard.put(new Integer[] {i,ii},listRun.get(j));
                        }
                    }
                }
            }
        }



        return mutualCard;
    }



    //This method is to decide whether a card should be in a run or set if any run and set has one mutual card
    /*Need to be improved
      For example: a run contains two cards that are in 2 separated sets or vice versa
     */
    public static void compareScore(List<List<Card>> listRuns, List<List<Card>> listSets) {

        for (int i = 0; i < listRuns.size(); i++) {
            List<Card> listRun = listRuns.get(i);

            int scoreRun = valueInRunOrSet(listRun);

            for (int j = 0; j < listRun.size(); j++) {

                for (int ii = 0; ii < listSets.size(); ii++) {
                    List<Card> listSet = listSets.get(ii);
                    int scoreSet = valueInRunOrSet(listSet);

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
    
    /*
     * Somehow another method will take care of finding the optimal combination of runs and melds
     * This method will just remove those cards, and then return a list with the remaining deadwood
     */
    
    public List<Card> findDeadwood(List<Card> cardsInMelds){
    	
    	List<Card> deadwood = new ArrayList<Card>();
    	
    	for(Card aCard:cardsInMelds) {
    		if(!this.hand.contains(aCard)) {
    			deadwood.add(aCard);
    		}
    	}
    	
    	return deadwood;
    	
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

        //compareScore(runs, sets);

        //System.out.println(runs);
        //System.out.println(sets);
        System.out.println("hand: "+ aPlayer.hand + "\n");
        //System.out.println(aPlayer.hand.getCard(1).getSuit());

        aPlayer.bestCombination();


		
	}
    
}
