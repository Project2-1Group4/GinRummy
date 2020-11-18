package temp.GamePlayers.AIs;

import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.GeneticAlgo.TestPlayer;

import java.util.ArrayList;
import java.util.List;


public class meldBuildingGreedy extends TestPlayer {

    /*
    SO I just realized that with the general structure I want to implement a lot of this current code is garbage
    The idea is still alright, but a lot of it can be improved as the cards in melds will be separated elsewhere
    For now I'll leave in the garbage methods, but yeah this is how it's currently made
    TODO: Fix up the inefficient methods to actually be fast
     */

    int deadwoodCutOff;

    // These methods are for the GA, as I want to be able to modify them going on
    // For the start the value of the heuristics will be set in stone.
    double cardInMeldMult;
    double cardApproxMeld;
    double cardFreeMult;
    double cardNeverMult;

    List<MyCard> discardedCards;
    List<MyCard> otherPlayerDiscards;

    List<MyCard> knownInOtherHand;

    public meldBuildingGreedy(){
        super();
        this.cardInMeldMult = 0.0;
        this.cardApproxMeld = 1.0;
        this.cardFreeMult = 2.0;
        this.cardNeverMult = 4.0;

        this.discardedCards = new ArrayList<>();
        this.otherPlayerDiscards = new ArrayList<>();
        this.knownInOtherHand = new ArrayList<>();
        this.deadwoodCutOff = 10;
    }



    /*
    Memory matrix takes into account all of the cards played so far and stores them so that it can decide how to act
    It's a 2d matrix, with the first row representing the suit and the second row representing the value (going from 0-12)
    For the memory matrix, these are the respective values:
    -1 = card in hand and in a meld (somewhere)
    0 = card unknown
    1 = card in hand and leftover
    2 = card known in other player's hand
    3 = card discarded by other player <-- useless for now, but will probably be used later
    4 = card in discard pile

    The card at the top of the discard pile is stored as a separate value

     */

    int[][] memoryMatrix = new int[4][13];

    /*
    Card at the top of the discard pile, used for the evaluate function
     */
    MyCard topDiscard;


    int[][] createMemoryMatrix(HandLayout layout){

        List<Meld> melds = layout.viewMelds();
        List<MyCard> deadwood = layout.viewUnusedCards();

        int[][] newMemMatrix = new int[4][13];

        for(Meld list : melds){
            for(MyCard aCard : list.viewMeld()){
                newMemMatrix[aCard.suit.index][aCard.suit.index] = -1;
            }
        }

        for(MyCard aCard: deadwood){
            newMemMatrix[aCard.suit.index][aCard.suit.index] = 1;
        }

        return newMemMatrix;

    }

    /*
    For a given memory matrix it'll delete all values that are in a meld or in hand
    And then update it with the new values that are in the given hand

    This method assumes that the reset cards will be added to the discardPile
     */
    static int[][] cloneResetMemMatrix(int[][] memoryMatrix, HandLayout hand, MyCard removedCard){
        int[][] newMem = memoryMatrix.clone();

        newMem[removedCard.suit.index][removedCard.rank.index] = 4;

        List<Meld> melds = hand.viewMelds();
        List<MyCard> deadwood = hand.viewUnusedCards();

        for(Meld aMeld: melds){
            for(MyCard myCard: aMeld.viewMeld()){
                newMem[myCard.suit.index][myCard.rank.index] = -1;
            }
        }

        for(MyCard myCard:deadwood){
            newMem[myCard.suit.index][myCard.rank.index] = 1;
        }

        return newMem;

    }

    /*
    Updates all the values in the memory matrix according to the found values
    This could be a bottleneck considering how often the update is done
    As well as a potential source of bugs.
    But for now it seems good enough.

    TODO: Make sure no issues arise out of this method
     */

    void updateMemoryMatrix(){

        this.memoryMatrix = new int[4][13];
        List<Meld> melds = this.handLayout.viewMelds();
        List<MyCard> deadwood = this.handLayout.viewUnusedCards();

        for(Meld list : melds){
            for(MyCard aCard : list.viewMeld()){
                memoryMatrix[aCard.suit.index][aCard.suit.index] = -1;
            }
        }

        for(MyCard aCard: deadwood){
            memoryMatrix[aCard.suit.index][aCard.suit.index] = 1;
        }

        for(MyCard myCard: this.knownInOtherHand){
            memoryMatrix[myCard.suit.index][myCard.suit.index] = 2;
        }

        for(MyCard myCard: this.otherPlayerDiscards){
            memoryMatrix[myCard.suit.index][myCard.suit.index] = 3;
        }

        for(MyCard myCard: this.discardedCards){
            memoryMatrix[myCard.suit.index][myCard.suit.index] = 4;
        }


    }

    /*
    Event is just a short hand way of defining what kind of event updated the memory matrix
    It follows the same general guidelines of the memory matrix, where 1= in hand
    3 = discarded by other player, etc.

    The method also does additional stuff depending on what the event was

    If it's in hand, there's no extra event as for the most part any extra stuff for that is handled elsewhere
    This is really only important for when the other player picks a card from the discard pile or something like that
     */

    void updateMemoryMatrix(MyCard aCard, int event){
        this.memoryMatrix[aCard.suit.index][aCard.rank.index] = event;

        if (event == 2) {
            this.knownInOtherHand.add(aCard);
        } else if (event == 3){
            this.knownInOtherHand.remove(aCard);
            this.otherPlayerDiscards.add(aCard);
        } else if (event == 4){
            this.discardedCards.add(aCard);
        }
    }

    void resetMemoryMatrix(){
        this.memoryMatrix = new int[4][13];
        this.updateMemoryMatrix();
    }

    // Just gives an evaluation of the card
    // It's simple and effective
    // If it's in a meld it just returns the heuristicForMeld
    // If it's not then it calculates its run value + its set value
    // And returns that
    // Under the idea that a card that could be in a run or in a set is more valuable, as there's more possible options
    double evaluateCard(MyCard aCard, int[][] memMatrix){
        int val = aCard.rank.index;
        int suit = aCard.suit.index;
        if(memMatrix[suit][val] == -1){
            return heuristicForMeld(val);
        } else {
            return evaluateRun(suit, val, memoryMatrix) + evaluateSet(val, memMatrix);
        }
    }

    /*
    Method works as follows:
    -If the card makes a set, then return a 0 (which is very good!)
    -If the card almost makes a set, return half the value of the card <-- this is almost definitely the part that should be most changed
    -If the card is alone, return the value of the card

    The reason why the value of the card when it almost makes a set is the most tentative thing is because it's pretty much the only heuristic
    So it should be tested around to see what threshold works, and how should the value of a card be "improved" when a card almost makes a set

     */

    // Method looks finished
    // TODO: Bugtest
    double evaluateSet(int val, int[][] memoryMatrix){

        int setCount = 0;
        int discardCount = 0;
        int otherPlayerCnt = 0;

        /*
        I'm not sure if I should put cards in the other player's hand as discard cards
        I'll store them in memory for now, that way if I want to do something with them later I don't have to do shit

        For now I'm just taking them as being equal to a discard card
         */
        for(int i = 0; i<4; i++){
            if(memoryMatrix[i][val] == 1){
                setCount++;
            } else if(((memoryMatrix[i][val] == 4) || (memoryMatrix[i][val] == 3)) || (memoryMatrix[i][val] == -1)){
                // Just increasing the value of cards that are discarded for now
                discardCount++;
            } else if (memoryMatrix[i][val]==2){
                otherPlayerCnt++;
            }

        }

        if(setCount >= 3){
            return 0;
        } else if ((setCount == 2) && (discardCount + otherPlayerCnt < 2)){
            return heuristicForApprox(val);
        } else if (discardCount + otherPlayerCnt >= 2) {
            return heuristicForNever(val);
        } else {
            return heuristicForFree(val);
        }

    }

    /*
    Method works as follows:
    -If card makes a run, then return 0
    -If the card almost makes a run, return the heuristic for the value
    -If the card doesn't make a run, then return the value of the card

    Again, value of almost making a run is the heuristic that probably should be checked

    Method assumes that all runs are just as valuable
    When in practice a larger run is better (as more cards are discarded)

    Method also assume that the memoryMatrix has been build already with all of the possible values
    What it's finding is the run value of the specific card in the memory matrix

    The method already takes into accounts the fact that cards in other melds are not as valuable
    So I don't need to worry about updating that fact

     */
    double evaluateRun(int suit, int val, int[][] memoryMatrix){
        // I go twice, once forwards and once backwards
        // Because it felt like the easiest way to do it
        // Garbage efficiency though

        // Forward running
        // Starting count at 1, cause I'll just assume that the given val and suit are in hand
        // Need to bug test to make sure that an array index out of bounds won't end up nullifying the changes to count
        // TODO: Bugtest the index out of bounds exception


        /*
        Extra method to store how many discard cards there are, and to check if they are within the bound of screwing things
         */

        int count = 1;

        int prev = checkCardNearby(suit,val, -1, memoryMatrix);
        int front = checkCardNearby(suit,val, 1, memoryMatrix);

        // I hate this garbage spaghetti code, can't think of any other way of doing it
        // TODO: Bug test my spaghetti code to make it doesn't break down on me
        if((prev>=2) && (front>=2)){
            return heuristicForNever(val);
        } else if (prev>=2){
            int nxt = checkCardNearby(suit,val,2,memoryMatrix);

            return findIfNearbyIsValidWithNegativeOpposite(val, front, nxt);

        } else if (front>=2){
            int nxt = checkCardNearby(suit,val,-2,memoryMatrix);

            return findIfNearbyIsValidWithNegativeOpposite(val, prev, nxt);
        } else if ((front == 0) && (prev == 0)){
            int frontNxt = checkCardNearby(suit,val,2,memoryMatrix);
            int backNxt = checkCardNearby(suit,val,-2, memoryMatrix);

            if((frontNxt == 1) || (backNxt == 1)){
                return heuristicForApprox(val);
            } else {
                return heuristicForFree(val);
            }

        } else if((prev == 1)&&(front == 1)){
            return 0;
        } else if (prev==1) {
            int nxt = checkCardNearby(suit,val,-2,memoryMatrix);

            if(nxt == 1){
                return 0;
            } else {
                return heuristicForApprox(val);
            }

        } else {
            // This state should only be reached when only front == 1 and prev is unknown (so = 1)
            // As a sanity check, I'll print out an error message inside here, just in case

            if ((front!=1)&&(prev != 0)){
                System.out.println("There's been an error in the spaghetti code");
            }

            int nxt = checkCardNearby(suit,val,2,memoryMatrix);

            if(nxt ==1){
                return 0;
            } else {
                return heuristicForApprox(val);
            }

        }


    }

    /*
    Helper method for runs
     */
    public double findIfNearbyIsValidWithNegativeOpposite(int val, int nearby, int nxt) {
        if(nxt >=2){
            return heuristicForNever(val);
        } else if ((nxt == 1) && (nearby == 1)){
            return 0;
        } else if (((nxt == 1)&&(nearby ==0))||((nxt == 0)&&(nearby ==1))) {
            return heuristicForApprox(val);
        } else {
            return heuristicForFree(val);
        }
    }

    /*
    Silly helper method to make sure that returns the value of a card value in gin rummy
    So if value >= 10, return 10
    else return value
     */
    static int valInGinRummy(int value){
        if(value >=10){
            return 10;
        } else {
            return value;
        }
    }

    /*
    Method is done to kinda hide the try catch statements
    So it'll check the [suit][val+loc} in the memory matrix
    Meaning it can check forward and backward

    For the card that's being checked:
        If not known return 0
        If it's in the player's hand: return 1
        If it's known to be in the other player's hand: return 2
        If it's known to be discarded: return 3
        If it's out of bounds, return 3
        If it's already in another meld (which is more valuable) then return 3
     */
    static int checkCardNearby(int suit, int val, int loc, int[][] memoryMatrix){
        try{
            int res = memoryMatrix[suit][val+loc];
            if((res >= 3) || (res == -1)){
                return 3;
            } else {
                return res;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            return 3;
        }
    }

    /*
    Method to multiply in case a card is in a meld
    For now, just keep it as very valuable
     */
    double heuristicForMeld(int value){
        return valInGinRummy(value)*this.cardInMeldMult;
    }

    /*
    This method will probably be the one that gets changed the most going forward
    This will return a value for when the given card (in this case we only care about its value) is close to creating a meld
    The exact heuristic is probably what's going to be most tested, but for now I'll just create the method elsewhere and make it simple
     */
    double heuristicForApprox(int value){
        return valInGinRummy(value)*this.cardApproxMeld;
    }
    /*
    Another method that'll be changed around going forward
    If a card will for sure never be in a meld
    Then we should "punish" that card somehow
    Current punishment is meh, but it'll do
     */

    double heuristicForNever(int value){
        return valInGinRummy(value)*this.cardNeverMult;
    }

    /*
    value if a card is free
    AKA it's in hand, but none of the cards in hand make it close to a meld
     */

    double heuristicForFree(int value){
        return valInGinRummy(value)*this.cardFreeMult;
    }


    void setTopDiscard(MyCard aCard){
        this.topDiscard = aCard;
        memoryMatrix[aCard.suit.index][aCard.rank.index] = 4;

    }

    public MyCard findLeastValuableCard(List<MyCard> cardList){

        // A high value is bad, so I'm just setting an arbitrarily large value
        double val = -100;
        MyCard worst = null;

        for(MyCard myCard:cardList){
            List<MyCard> temp = new ArrayList<>(cardList);
            temp.remove(myCard);
            HandLayout layout = Finder.findBestHandLayout(temp);

            int[][] clone = cloneResetMemMatrix(this.memoryMatrix, layout, myCard);
            double subVal = this.evaluate(memoryMatrix, temp);

            if(subVal >=val){
                val = subVal;
                worst = myCard;
            }


        }

        return worst;
    }

    public double evaluate(int[][] memoryMatrix, List<MyCard> cardList){
        double val = 0;

        for(MyCard myCard: cardList){
            val += this.evaluateCard(myCard, memoryMatrix);
        }
        return val;

    }

    // TODO: Ask if this method is used when resetting rounds or for every time a card is added.
    @Override
    public void update(HandLayout realLayout){
        super.update(realLayout);
        this.updateMemoryMatrix();
    }

    @Override
    public Boolean knockOrContinue() {
        int val = this.handLayout.getDeadwood();
        if (val <= this.deadwoodCutOff){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {

        List<MyCard> tempList = new ArrayList<>(this.allCards);

        tempList.add(topOfDiscard);

        MyCard least = this.findLeastValuableCard(tempList);

        if (least == topOfDiscard){
            return true;
        } else {
            return false;
        }

    }

    /*
    DiscardCard is for when there's 11 cards in the hand
    So what we need to do is just evaluate all combinations with 11 cards and see what happens
     */
    @Override
    public MyCard discardCard() {
        return this.findLeastValuableCard(this.allCards);
    }

    static public int findValOfHand(List<MyCard> cardList){
        HandLayout layout = Finder.findBestHandLayout(cardList);

        return layout.getDeadwood();
    }

    @Override
    public void otherPlayerDiscarded(DiscardAction discardAction) {
        MyCard DisCard = discardAction.card;
        this.updateMemoryMatrix(DisCard, 3);
    }

    // TODO: Go over this method to update what happens when the other player picks up a card
    @Override
    public void otherPlayerPicked(PickAction pickAction) {
        if(pickAction.deck){
            // There's really not much to do in case the other player picked a card from the deck
        } else {
            this.updateMemoryMatrix(pickAction.card, 2);

        }
    }
}
