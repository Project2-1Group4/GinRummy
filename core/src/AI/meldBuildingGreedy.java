package AI;

import cardlogic.Card;
import cardlogic.SetOfCards;
import gameHandling.Player;
import gameHandling.PlayerGameInteractions;

import java.util.List;


public class meldBuildingGreedy extends basicGreedy {

    /*
    SO I just realized that with the general structure I want to implement a lot of this current code is garbage
    The idea is still alright, but a lot of it can be improved as the cards in melds will be separated elsewhere
    For now I'll leave in the garbage methods, but yeah this is how it's currently made
    TODO: Fix up the inefficient methods to actually be fast
     */

    public meldBuildingGreedy(SetOfCards cards){
        super(cards);

        this.updateMemoryMatrix();

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
    Card topDiscard;

    boolean evaluate(Card discard){
        this.setTopDiscard(discard);

    }

    @Override
    public boolean ChooseDeckOrPile(Card aCard){
        this.updateMemoryMatrix();

        List<Card> cards = this.hand.toList();
        cards.add(aCard);

        for(Card oneCard : cards){




        }



    }

    int[][] createMemoryMatrix(List<Card> cards){
        Player temp = new Player(new SetOfCards(cards));

        List<List<Card>> melds = temp.getMelds();
        List<Card> deadwood = temp.getDeadwood(melds);

        int[][] newMemMatrix = new int[4][13];

        for(List<Card> list : melds){
            for(Card aCard : list){
                newMemMatrix[aCard.getSuitVal()][aCard.getValue()] = -1;
            }
        }

        for(Card aCard: deadwood){
            newMemMatrix[aCard.getSuitVal()][aCard.getValue()] = 1;
        }

        return newMemMatrix;

    }

    void updateMemoryMatrix(){
        List<List<Card>> melds = this.getMelds();
        List<Card> deadwood = this.getDeadwood(melds);

        for(List<Card> list : melds){
            for(Card aCard : list){
                memoryMatrix[aCard.getSuitVal()][aCard.getValue()] = -1;
            }
        }

        for(Card aCard: deadwood){
            memoryMatrix[aCard.getSuitVal()][aCard.getValue()] = 1;
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
    static int evaluateSet(int val, int[][] memoryMatrix){

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
            } else if((memoryMatrix[i][val] == 4) || (memoryMatrix[i][val] == 3)){
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

     */
    static int evaluateRun(int suit, int val, int[][] memoryMatrix){
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

    public static int findIfNearbyIsValidWithNegativeOpposite(int val, int nearby, int nxt) {
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
    This method will probably be the one that gets changed the most going forward
    This will return a value for when the given card (in this case we only care about its value) is close to creating a meld
    The exact heuristic is probably what's going to be most tested, but for now I'll just create the method elsewhere and make it simple
     */
    static int heuristicForApprox(int value){
        return valInGinRummy(value);
    }
    /*
    Another method that'll be changed around going forward
    If a card will for sure never be in a meld
    Then we should "punish" that card somehow
    Current punishment is meh, but it'll do
     */

    static int heuristicForNever(int value){
        return valInGinRummy(value)*4;
    }

    /*
    value if a card is free
    AKA it's in hand, but none of the cards in hand make it close to a meld
     */

    static int heuristicForFree(int value){
        return valInGinRummy(value)*2;
    }


    void setTopDiscard(Card aCard){
        this.topDiscard = aCard;
        memoryMatrix[aCard.getSuitVal()][aCard.getValue()] = 4;

    }

}
