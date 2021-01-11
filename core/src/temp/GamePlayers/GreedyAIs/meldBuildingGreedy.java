package temp.GamePlayers.GreedyAIs;

import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;
import temp.GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.List;


public class meldBuildingGreedy extends GamePlayer {

    /*
    SO I just realized that with the general structure I want to implement a lot of this current code is garbage
    The idea is still alright, but a lot of it can be improved as the cards in melds will be separated elsewhere
    For now I'll leave in the garbage methods, but yeah this is how it's currently made
    TODO: Fix up the inefficient methods to actually be fast
     */

    public meldBuildingGreedy() {
        super();

    }

    public meldBuildingGreedy(double cardInMeld, double cardCloseToMeld, double cardFree, double cardNever){
        this();
        this.cardCloseToMeld = cardCloseToMeld;
        this.cardInMeld = cardInMeld;
        this.cardFree = cardFree;
        this.cardNever = cardNever;

    }

    public meldBuildingGreedy(double[] values){
        this(values[0],values[1],values[2],values[3]);
    }

    public double[] heuristicValues(){
        return new double[]{this.cardInMeld,this.cardCloseToMeld, this.cardFree, this.cardNever};
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

    /*
    A card is divided into 4 different types:
    -It's in a meld
    -It's close to being in a meld
    -It can never belong to a meld (as the relevant cards are discarded)
    -It's free

    These multipliers are for that, to use them for a GA
    */

    // These were tha values obtained after running the GA for a while
    double cardInMeld = 0.5342849458886937;
    double cardCloseToMeld = 3.43501263871715;
    double cardFree = 4.592593036604112;
    double cardNever = 5.002065914031019;

    int knockValue = 10;

    static int[][] createMemoryMatrix(HandLayout layout) {

        List<Meld> melds = layout.melds();
        List<MyCard> deadwood = layout.unused();

        int[][] newMemMatrix = new int[4][13];

        for (Meld list : melds) {
            for (MyCard aCard : list.cards()) {
                newMemMatrix[aCard.suit.index][aCard.suit.index] = -1;
            }
        }

        for (MyCard aCard : deadwood) {
            newMemMatrix[aCard.suit.index][aCard.suit.index] = 1;
        }

        return newMemMatrix;

    }

    // The idea behind when I use it seems solid
    // I update the internal matrix by using the cloneResetMemMatrix
    // Which takes care of removing the previous cards in hand, and adding the new cards
    // Still feel kinda unsure about everything
    // TODO: Check this part of the code out
    void updateMemoryMatrix(HandLayout newHand){
        this.memoryMatrix = cloneResetMemMatrix(this.memoryMatrix, newHand);
    }

    /*
    For a given memory matrix it'll delete all values that are in a meld or in hand
    And then update it with the new values that are in the given hand

    This method assumes that the reset cards will be added to the discardPile
     */
    static int[][] cloneResetMemMatrix(int[][] memoryMatrix, HandLayout hand) {
        

        int[][] newMemMatrix = new int[memoryMatrix.length][memoryMatrix[0].length];
        for(int i = 0; i<memoryMatrix.length; i++){
            for(int j = 0; j<memoryMatrix[0].length;j++){
                int temp = memoryMatrix[i][j];

                if((temp!=-1) && (temp!= 1)){
                    newMemMatrix[i][j] = temp;
                } else {
                    // For now I'll just assume that all of the previous cards in hand are now discarded
                    newMemMatrix[i][j] = 4;
                }

            }

        }

        List<Meld> melds = hand.melds();
        List<MyCard> deadwood = hand.unused();

        for (Meld list : melds) {
            for (MyCard aCard : list.cards()) {
                newMemMatrix[aCard.suit.index][aCard.rank.index] = -1;
            }
        }

        for (MyCard aCard : deadwood) {
            newMemMatrix[aCard.suit.index][aCard.rank.index] = 1;
        }


        return newMemMatrix;
    }

    /*
    This method should only be used at round start
    It's dangerous because it doesn't take into account the memory of previous turns
    So using it makes the program lose information
    */

    void resetMemoryMatrix() {
        this.memoryMatrix = new int[4][13];

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
    double evaluateSet(int val, int[][] memoryMatrix) {

        int setCount = 0;
        int discardCount = 0;
        int otherPlayerCnt = 0;

        /*
        I'm not sure if I should put cards in the other player's hand as discard cards
        I'll store them in memory for now, that way if I want to do something with them later I don't have to do shit

        For now I'm just taking them as being equal to a discard card
         */
        for (int i = 0; i < 4; i++) {
            if (memoryMatrix[i][val] == 1) {
                setCount++;
            } else if (((memoryMatrix[i][val] == 4) || (memoryMatrix[i][val] == 3)) || (memoryMatrix[i][val] == -1)) {
                // Just increasing the value of cards that are discarded for now
                discardCount++;
            } else if (memoryMatrix[i][val] == 2) {
                otherPlayerCnt++;
            }

        }

        if (setCount >= 3) {
            return 0;
        } else if ((setCount == 2) && (discardCount + otherPlayerCnt < 2)) {
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

     */
    double evaluateRun(int suit, int val, int[][] memoryMatrix) {
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

        int prev = checkCardNearby(suit, val, -1, memoryMatrix);
        int front = checkCardNearby(suit, val, 1, memoryMatrix);

        // I hate this garbage spaghetti code, can't think of any other way of doing it
        // TODO: Bug test my spaghetti code to make it doesn't break down on me
        if ((prev >= 2) && (front >= 2)) {
            return heuristicForNever(val);
        } else if (prev >= 2) {
            int nxt = checkCardNearby(suit, val, 2, memoryMatrix);

            return findIfNearbyIsValidWithNegativeOpposite(val, front, nxt);

        } else if (front >= 2) {
            int nxt = checkCardNearby(suit, val, -2, memoryMatrix);

            return findIfNearbyIsValidWithNegativeOpposite(val, prev, nxt);
        } else if ((front == 0) && (prev == 0)) {
            int frontNxt = checkCardNearby(suit, val, 2, memoryMatrix);
            int backNxt = checkCardNearby(suit, val, -2, memoryMatrix);

            if ((frontNxt == 1) || (backNxt == 1)) {
                return heuristicForApprox(val);
            } else {
                return heuristicForFree(val);
            }

        } else if ((prev == 1) && (front == 1)) {
            return 0;
        } else if (prev == 1) {
            int nxt = checkCardNearby(suit, val, -2, memoryMatrix);

            if (nxt == 1) {
                return 0;
            } else {
                return heuristicForApprox(val);
            }

        } else {
            // This state should only be reached when only front == 1 and prev is unknown (so = 1)
            // As a sanity check, I'll print out an error message inside here, just in case

            if ((front != 1) && (prev != 0)) {
                System.out.println("There's been an error in the spaghetti code");
            }

            int nxt = checkCardNearby(suit, val, 2, memoryMatrix);

            if (nxt == 1) {
                return 0;
            } else {
                return heuristicForApprox(val);
            }

        }


    }

    double findIfNearbyIsValidWithNegativeOpposite(int val, int nearby, int nxt) {
        if (nxt >= 2) {
            return heuristicForNever(val);
        } else if ((nxt == 1) && (nearby == 1)) {
            return 0;
        } else if (((nxt == 1) && (nearby == 0)) || ((nxt == 0) && (nearby == 1))) {
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
    static int valInGinRummy(int value) {
        if (value >= 10) {
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

    // This seems to be a major speed obstacle for the AI
    static int checkCardNearby(int suit, int val, int loc, int[][] memoryMatrix) {
        try {
            int res = memoryMatrix[suit][val + loc];
            if ((res >= 3) || (res == -1)) {
                return 3;
            } else {
                return res;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 3;
        }
    }

    /*
    This method will probably be the one that gets changed the most going forward
    This will return a value for when the given card (in this case we only care about its value) is close to creating a meld
    The exact heuristic is probably what's going to be most tested, but for now I'll just create the method elsewhere and make it simple
     */
    double heuristicForApprox(int value) {
        return valInGinRummy(value)* this.cardCloseToMeld;
    }
    /*
    Another method that'll be changed around going forward
    If a card will for sure never be in a meld
    Then we should "punish" that card somehow
    Current punishment is meh, but it'll do
     */

    double heuristicForNever(int value) {
        return valInGinRummy(value) * this.cardNever;
    }

    /*
    value if a card is free
    AKA it's in hand, but none of the cards in hand make it close to a meld
     */

    double heuristicForFree(int value) {
        return valInGinRummy(value) * this.cardFree;
    }

    double heuristicForMeld(int value){
        return valInGinRummy(value)*this.cardInMeld;
    }


    void setTopDiscard(MyCard aCard) {
        this.topDiscard = aCard;

        // TODO: Add a quick check to make sure I'm not deleting info about who discarded the card
        memoryMatrix[aCard.suit.index][aCard.rank.index] = 4;

    }

    public MyCard findLeastValuableCard(HandLayout aLayout){
        return findLeastValuableCard(aLayout.cards());
    }

    // I choose the card with the highest value, as high value = bad in Gin Rummy
    public MyCard findLeastValuableCard(List<MyCard> aList){

        // Value is just an arbitrary high number
        // Due to the heuristics it's higher than 101
        double handWithLowestScore = 101000.0;
        MyCard worstCard = null;
        for(MyCard aCard: aList){

            List<MyCard> tempList = new ArrayList<>(aList);
            tempList.remove(aCard);

            HandLayout layout = Finder.findBestHandLayout(tempList);

            double valOfHand = 0.0;

            // I'm doing an extra check in case the hand allows for a knock
            // I don't think it will change much, but it definitely feels important as a change
            // Done just to reward more any hand that can already knock

            if(layout.deadwoodValue()<= this.knockValue){
                valOfHand=- 1000;
            }

            int[][] cloneOfMatrix = cloneResetMemMatrix(this.memoryMatrix, layout);
            
            // Here I'm adding the value of all the melds to valOfHand
            for(Meld melds: layout.melds()){
                for(MyCard card: melds.cards()){
                    valOfHand += this.heuristicForMeld(card.rank.index);
                }
                
            }

            // Here the method takes care of evaluating all of the cards that are free
            for(MyCard deadWoodCard: layout.unused()){
                double runVal = this.evaluateRun(deadWoodCard.suit.index, deadWoodCard.rank.index, cloneOfMatrix);
                double setVal = this.evaluateSet(deadWoodCard.suit.index, cloneOfMatrix);

                valOfHand+= runVal + setVal;
            }

            // It's <= because that way the last card can be returned in case it's as valuable as another card
            // Because in that case it's better to not pick from the deck because you'd give the opponent information
            // This method also assumes that the last card in the list is the card at the top of the discard pile
            if(valOfHand<=handWithLowestScore){
                handWithLowestScore = valOfHand;
                worstCard = aCard;
            }

            


        }

        return worstCard;
    }

    @Override
    public Boolean knockOrContinue() {
        if (this.handLayout.deadwoodValue() <= this.knockValue){
            return true;
        } else {
            return false;
        }
    }


    // TODO: Implement this method
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        List<MyCard> temp = this.handLayout.cards();
        temp.add(topOfDiscard);
        MyCard worst = this.findLeastValuableCard(temp);
        this.topDiscard = worst;

        if(worst != topOfDiscard){
            return false;
        } else {
            return true;
        }

    }

    // TODO: Implement this method
    @Override
    public MyCard discardCard() {
        return this.findLeastValuableCard(this.allCards);
    }

    // I don't think anything else is required for a new round
    // As anything else that's stored internally gets eliminated elsewhere
    @Override
    public void newRound(MyCard topOfDiscard) {
        this.topDiscard = topOfDiscard;
        this.resetMemoryMatrix();
        this.updateMemoryMatrix(this.handLayout);
    }

    // TODO: Update this method to update the internal matrix correctly
    // Always assume that it's when a new set of cards is given
    // As the update method is handled separately, and has already been implemented
    @Override
    public void update(List<MyCard> cards) {
        allCards = cards;
        handLayout = Finder.findBestHandLayout(allCards);
        this.updateMemoryMatrix(handLayout);
    }

    

    static public int findValOfHand(List<MyCard> cardList) {
        HandLayout layout = Finder.findBestHandLayout(cardList);

        return layout.deadwoodValue();
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        MyCard DisCard = discardAction.card;
        this.memoryMatrix[DisCard.suit.index][DisCard.rank.index] = 3;
    }

    // TODO: Go over this method to update what happens when the other player picks up a card
    @Override
    public void playerPicked(PickAction pickAction) {
        if(!pickAction.deck){
            MyCard aCard = pickAction.card();
            this.memoryMatrix[aCard.suit.index][aCard.rank.index] = 2;
        }

    }

    @Override
    public String toString() {
        return "meldBuildingGreedy: " +
                " CardInMeld: " + this.cardInMeld +
                " CardCloseToMeld: " + this.cardCloseToMeld +
                " CardFree: " + this.cardFree +
                " CardNever: " + this.cardNever;
    }
}
