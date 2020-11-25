package gameHandling;

import cardlogic.Card;
import cardlogic.Card.SUITS;
import cardlogic.CardBatch;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    public SetOfCards hand;
    private int score;

    private int bestValueCombination; //get the sum value of the set of card 
    public List<List<Card>> bestCombination;
    private List<List<Card>> permutations;
    public List<Card> deadWood;

    public static int constantScore = 100; //just constant score for alphabeta pruning

    public Player(String name, CardBatch hand) {
        this.name = name;
        this.hand = hand;
        this.score = 0;
    }

    public Player(SetOfCards cards) {
        this("player",cards);
    }

    public Player(String name, SetOfCards cards) {
        this.name = name;
        this.hand = cards;
        this.score = 0;

        // TODO: Add a method so that the default constructor already scores the hand
        // TODO: Add this method as well whenever a card is drawn
        this.scoreHand();
        this.deadWood = this.findDeadwood();
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

                /*
                If newSequenceSet.size() == 0 {
                    Ignore everything and return the new removed, which should be empty
                }
                 */
                int newValue;
                if (newSequenceSet.getCardSetSize() == 0) {
                    newValue = valueOfLists(newRemoved);
                }
                else
                    newValue = valueOfLists(findRuns(newSequenceSet)) + valueOfLists(newRemoved);

                if(newValue > this.bestValueCombination){
                    List<List<Card>> newCombination = new ArrayList<>();
                    newCombination.addAll(copyListOfList(findRuns(newSequenceSet)));
                    newCombination.addAll(copyListOfList(newRemoved));
                    this.bestCombination = newCombination;

                    /*
                    Not sure how important best Value combination is, because for gin rummy it's the deadwood that matters in scoring
                     */

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

        //System.out.println("runs and sets chosen: "+this.bestCombination);
    }

    public int valueOfLists(List<List<Card>> cards){
        int sumValue = 0;
        for(List<Card> list:cards){
            sumValue += valueInList(list);
        }
        return sumValue;
    }

    public List<List<Card>> getPermutation(List<List<Card>> listSets) {
        if (listSets.size() > 0) {
            for (int i = 0; i < listSets.size(); i++) {
                if (listSets.get(i).size() > 3) {

                    for (int j = 0; j < 4; j++) {
                        List<Card> set = copyList(listSets.get(i));

                        this.remove(set, j);
                        if (!listSets.contains(set)) {
                            listSets.add(i + 1, set);
                            //System.out.println(permutation.get(index))
                        }
                    }
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

    public static int valueInList(List<Card> listCard) {
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

                if(count >= 2 && i == handOfCards.getCardSetSize()-1) {

                    List<Card> tempList = getSetOrRun(i+1, count, handOfCards);

                    listList.add(tempList);
                }
            } else {

                // TODO: Bugtest this thingy, cause the logic is rather wonky for me

                // Okay, so the idea is that if there's a count>=2 then there's at least 3 cards in common
                // We then find the index of the first card there, and add the n cards to the temp list
                // Then save the templist into the listlist
                // So what we're going to do is go ahead and store all of the previous cards in the listList

                if(count >= 2) {

                    List<Card> tempList = getSetOrRun(i, count, handOfCards);

                    listList.add(tempList);
                }

                prev = aCard.getValue();
                count = 0;
            }


        }

        //System.out.println("listSet of "+this.name+" is: " + listList);

        return listList;


    }

    public List<List<Card>> findRuns(SetOfCards handOfCards){
        List<List<Card>> runs = new ArrayList<>();

        if (handOfCards.getCardSetSize() < 3) {
            return runs;
        }

        else {
            handOfCards.sortBySuits();


            List<Card> currentRun = new ArrayList<>();
            currentRun.add(handOfCards.getCard(0));
            for (int i = 1; i < handOfCards.getCardSetSize(); i++) {
                if (handOfCards.getCard(i).getSuit().equals(currentRun.get(currentRun.size() - 1).getSuit())) {
                    if (currentRun.isEmpty()) {
                        currentRun.add(handOfCards.getCard(i));
                    } else {
                        if (currentRun.get(currentRun.size() - 1).getValue() + 1 == handOfCards.getCard(i).getValue()) {
                            currentRun.add(handOfCards.getCard(i));
                        } else {
                            if (currentRun.size() >= 3) {
                                runs.add(currentRun);
                            }
                            currentRun = new ArrayList<>();
                            currentRun.add(handOfCards.getCard(i));
                        }
                    }

                } else {
                    if (currentRun.size() >= 3) {
                        runs.add(currentRun);
                    }
                    currentRun = new ArrayList<>();
                    currentRun.add(handOfCards.getCard(i));
                }
                if (i == handOfCards.getCardSetSize() - 1 && currentRun.size() >= 3) {
                    runs.add(currentRun);
                }


            }
            return runs;
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

                if(count >= 2 && i == this.hand.getCardSetSize()-1) {

                    List<Card> tempList = getSetOrRun(i+1, count);

                    listList.add(tempList);
                }
            } else  {

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

        //System.out.println("listSet of "+this.name+" is: " + listList);



        return listList;


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

    public List<Card> getSetOrRun(int index, int count, SetOfCards cards) {
        int startPoint = index - count - 1;
        List<Card> tempList = new ArrayList<>();

        for (int j=0; j<count+1; j++) {
            tempList.add(cards.getCard(j + startPoint));
        }
        return tempList;
    }


    /*
     * I'm not sure if I'm understanding this method correctly, but it seems to find the deadwood somehow
     * The part that makes me unsure is the copyList() method, as I don't know how it reaches the hand
     * That and the fact that best combination is stored inside the class, there's something in there that makes me unsure
     */

    public List<Card> findDeadwood() {

        List<Card> handCard = this.hand.toList();
        this.deadWood = copyList(handCard);
        //System.out.println("Raw deadwoood: "+deadWood);
        this.bestCombination();

        if (this.bestCombination.size() == 0) {
            return this.deadWood;
        } else {
            for (List<Card> card : this.bestCombination) {
                for (Card aCard : card) {
                    if (handCard.contains(aCard)) {
                        this.deadWood.remove(aCard);
                    }
                }
            }
            return this.deadWood;

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

    public void addPoints(int points) {
        this.score += points;
    }


    /*
     *  Finds the score of a given hand
     *  Needs to be modified to receive which cards are already in a run or a set
     *  I'm currently doing it internally, so there should be no problems with that
     *  TODO: Modify method so that the bonuses can be added at the end of a turn
     */


    public int scoreHand() {


        List<List<Card>> resultingCards = this.getMelds();

        List<Card> deadwood = this.getDeadwood(resultingCards);

        // I also need to find a way to take the deadwood out in some capacity
        // As it's convenient for the player that didn't know, so that I can add the deadwood to any other sets
        // TODO: Modify this so that the player that didn't knock can lose some of the deadwood

        int score = SetOfCards.scoreGinRummy(deadwood);

        return score;

    }

    public static int scoreHand(List<Card> aHand) {
        SetOfCards hand = new SetOfCards(aHand);
        Player player = new Player(hand);
        int scoreHand = player.scoreHand();
        return scoreHand;
    }

    //by this method, the hand with less deadwood will give the more value on score (an apparent way to use for evaluation)
    public static int getHandValue(List<Card> aHand) {
        int scoreHand = scoreHand(aHand);
        return constantScore - scoreHand;
    }

    public int getHandValue() {
        return constantScore - scoreHand();
    }


    public List<Card> findDeadwood(List<Card> cardsInMelds){

        List<Card> deadwood = new ArrayList<Card>();

    	/*
    	if(cardsInMelds.size() == 0){
    	    deadwood.addAll(this.hand.toList());
    	    return deadwood;
        }*/

        deadwood.addAll(this.hand.toList());

        for(Card aCard: cardsInMelds) {
            if(deadwood.contains(aCard)) {
                deadwood.remove(aCard);
            }
        }

        return deadwood;

    }

    /*
     * This method is more than all used internally
     * Idea was to overload this guy with findDeadwood, but due to garbage java treatment of generics this happened
     */

    public List<Card> getDeadwood(List<List<Card>> cardsInMelds){

        List<Card> usedCards = new ArrayList<Card>();

        for(List<Card> melds:cardsInMelds) {
            usedCards.addAll(melds);
        }

        return this.findDeadwood(usedCards);

    }


    /*
     * Idea is that this method will find the melds in the given player's hand
     * It'll return the melds in a list of lists
     *
     *
     * NEW INFO:
     * I'm pretty sure the method's useless now due to the findBestCombinations method done by Truc
     * I added some changes to make sure the older code works, but there's definitely some updating needed inside
     * TODO: Check code for inconsistencys and speed
     *
     */
    public List<List<Card>> getMelds(){
        this.bestCombination();
        return this.bestCombination;
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
    Gives the player the option to knock or no
    If the player doesn't want to knock:
        return false
    If he wants to knock:
        return true
    */
    public boolean chooseToKnock(){
        return false;
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

    public boolean chooseDeckOrPile(Card pileTop) {
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


    public void addCard(Card aCard){
        this.hand.addCard(aCard);
    }

    public String toString(){
        return this.name + " has " + this.score;
    }


    public static void main(String[] args) {


        //SetOfCards deck = new SetOfCards(true, false);
        //deck.shuffleCards();
        SetOfCards hand = new SetOfCards(false, false);

        for (int i = 0; i < 3; i++) {
            hand.addCard(new Card(i,1));
        }

        for (int i = 3; i < 7; i++) {
            hand.addCard(new Card(0,i));
        }

        Player p = new Player(hand);
        p.bestCombination();
        System.out.println(p.bestCombination);

        /*
        for (int i = 1; i < 10; i++) {
            hand.addCard(new Card(0,i));
        }

        for (int i = 0; i <= 3; i++) {
            hand.addCard(new Card(i,12));
        }

        Player aPlayer = new Player("player", hand);
        System.out.println(hand);

        SetOfCards handy = new SetOfCards(false, false);

        for (int i = 0; i <= 3; i++) {
            handy.addCard(new Card(i,4));
        }

        for (int i = 0; i < 3; i++) {
            handy.addCard(new Card(i,3));
        }

        //Player ap = new Player("player1", handy);

        System.out.println("handy: "+handy+"cc"+hand.getCard(0));

        //  System.out.println("permutations: "+ap.getPermutation(ap.findSets(handy)));

        System.out.println("permutation: " +aPlayer.getPermutation(aPlayer.findSets(handy)));


        List<List<Card>> runs = aPlayer.findRuns();
        System.out.println("runs: \n " + runs);

        //List<List<Card>> sets = aPlayer.findSets();
        //System.out.println("sets: \n" + sets);

        List<List<Card>> sets = aPlayer.findSets(hand);
        System.out.println("setss: \n" + sets);

        System.out.println("hand: "+ aPlayer.hand + "\n");

        aPlayer.bestCombination();

        System.out.println("deadwood: "+aPlayer.findDeadwood());

        int deadWoodValue = Player.valueInList(aPlayer.deadWood);

        System.out.println("Value of deadwood set: "+deadWoodValue);

        Player p2 = new Player("player 2", SetOfCards.handOutCard(20, deck));

        System.out.println("runs: \n" + p2.findRuns(p2.hand));

        System.out.println("sets: \n" + p2.findSets(p2.hand));

        System.out.println("deadwood" + p2.findDeadwood());

         */

    }

}
