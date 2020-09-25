package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.*;

public class PlayerOne {
    private String name;
    private SetOfCards hand;
    private int score;

    private int bestValueCombination;
    private List<List<Card>> bestCombination;




    public PlayerOne(String name) {
        this(name, new SetOfCards());
    }

    public PlayerOne(SetOfCards cards) {
        this("player",cards);
    }

    public PlayerOne(String name, SetOfCards cards) {
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

            // inside the loop remove the current permutation (set) form a deep copy of the sequence (not a reference)

            //make sure that you can remove the set (if you cant remove the set it's not a valid final sequence, since you would remove too little cards)
            //so you skip those.
            //add the permutation to a deep copy of the removed (newRemoved)


            //calculate the value; value = sum_of_values(removed) + sum_of_values(this.findruns(sequence))
            //update the best value and best value combination if you get a higher value then that.
            //call the recursiveSearch again with the new sequence and new removed.
            //Note: (make also sure that there is in the loop at least one empty sequence, that doesnt remove anything)
        List<List<Card>> setPermutation = this.getPermutation(this.findSets(this.hand));


    }

    public void bestCombination() {
        this.bestValueCombination = 0;
        this.bestCombination = new ArrayList<>();
        System.out.println("Sets: "+ getPermutation(this.findSets(this.hand))); //make permutations and make 4,4,4,4 => into 4,4,4 and 4,4,4,4 ; ToDo list
        System.out.println("Runs: " + this.findRuns(this.hand));

        List<List<Card>> removed = new ArrayList<>();
        this.recursiveSearch(this.hand.toList(), removed);

    }

    public List<List<Card>> getPermutation(List<List<Card>> listSets) {
        List<List<Card>> permutation = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < listSets.size(); i++) {
            if (listSets.get(i).size()>3) {

                for (int j = 0; j < 4; j++) {
                    List<Card> set = new ArrayList<>();
                    for (int ii = 0; ii < 4; ii++) {

                        set.add(listSets.get(i).get(ii));
                    }

                    permutation.add(set);
                    this.remove(permutation.get(index), j);
                    //System.out.println(permutation.get(index));
                    index++;
                }
            }
        }
        listSets.addAll(permutation);
        return listSets;
    }

    public void remove(List<Card> card, int i) {
        card.remove(i);
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

        if(listList.size() == 0) {
            return null;
        } else {
            return listList;
        }

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
    public List<List<Card>> findRuns(SetOfCards handOfCards){
        handOfCards.sortBySuits();

        List<List<Card>> listList = new ArrayList<List<Card>>();

        Card.SUITS prevSuit = null;
        int prevVal = 0;
        int count = 0;

        for(int i=0; i<handOfCards.getCardSetSize(); i++) {
            Card aCard = handOfCards.getCard(i);

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

    public List<List<Card>> findRuns(){
        this.hand.sortBySuits();

        List<List<Card>> listList = new ArrayList<List<Card>>();

        Card.SUITS prevSuit = null;
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

        PlayerOne aPlayer = new PlayerOne("player",SetOfCards.handOutCard(20, deck));
        //aPlayer.hand.sortBySuitAndValue();
        //System.out.println(aPlayer.hand);

        List<List<Card>> runs = aPlayer.findRuns();
        System.out.println("runs: \n " + runs);

        List<List<Card>> sets = aPlayer.findSets();
        System.out.println("sets: \n" + sets);

        /*
        aPlayer.getPermutation(sets);
        System.out.println("sets: \n" + sets);
         */

        //compareScore(runs, sets);

        //System.out.println(runs);
        //System.out.println(sets);
        System.out.println("hand: "+ aPlayer.hand + "\n");
        //System.out.println(aPlayer.hand.getCard(1).getSuit());

        aPlayer.bestCombination();



    }


}
