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

public class GreedyV3 extends GamePlayer {

    // Copying over the internal values from meldBuildingGreedy, nothing's changed

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

    // Two heuristics for internal vs external runs
    double intRun = 1.0;
    double extRun = 2.0;

    /*
    If this is true, then the AI will avoid picking from the discard pile unless it completes a meld somehow
    If false the AI will pick from the discard pile if it improves its hand in some way

    Done because that way we can give less information to the other player, which is important in gin rummy
     */

    boolean avoidsDiscard = true;

    int knockValue = 10;

    public GreedyV3(double cardInMeld, double cardCloseToMeld, double cardFree, double cardNever){
        this();
        this.cardCloseToMeld = cardCloseToMeld;
        this.cardInMeld = cardInMeld;
        this.cardFree = cardFree;
        this.cardNever = cardNever;

    }

    @Override
    public Boolean knockOrContinue() {
        if (this.handLayout.getDeadwood() <= this.knockValue){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        List<MyCard> temp = this.handLayout.viewAllCards();
        temp.add(topOfDiscard);



        if(this.avoidsDiscard){
            HandLayout layout = Finder.findBestHandLayout(temp);
            List<MyCard> cardsInMelds = layout.getCardsInMelds();

            for(MyCard card : cardsInMelds){
                if (card.same(topOfDiscard)){
                    return true;
                }
            }

            if(findIfCardsMakeKnockingPossible(temp, this.knockValue)){
                return true;
            }

            return false;

        }

        // This only happens if the AI doesn't care about giving away hand information

        MyCard worst = this.findLeastValuableCard(temp);
        this.topDiscard = worst;

        if(worst != topOfDiscard){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public MyCard discardCard() {

        // So if there's a card that makes knocking possible, then the AI will find the hand with the lowest value with which to knock
        // Because a lower value is obviously better in the end
        if(findIfCardsMakeKnockingPossible(this.allCards, this.knockValue)){

            // I just use the method that already exists in basicGreedy, as that's good enough
            // Gotta make my life easier I guess
            return basicGreedyTest.chooseCardToDiscard(this.allCards);
        }


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
    public void update(List<MyCard> realLayout) {
        allCards = realLayout;
        handLayout = Finder.findBestHandLayout(allCards);
        this.updateMemoryMatrix(handLayout);
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

    public GreedyV3(){
        super();
    }

    void updateMemoryMatrix(HandLayout newHand){
        this.memoryMatrix = cloneResetMemMatrix(this.memoryMatrix, newHand);
    }

    void resetMemoryMatrix() {
        this.memoryMatrix = new int[4][13];
    }

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

        List<Meld> melds = hand.viewMelds();
        List<MyCard> deadwood = hand.viewUnusedCards();

        for (Meld list : melds) {
            for (MyCard aCard : list.viewMeld()) {
                newMemMatrix[aCard.suit.index][aCard.rank.index] = -1;
            }
        }

        for (MyCard aCard : deadwood) {
            newMemMatrix[aCard.suit.index][aCard.rank.index] = 1;
        }


        return newMemMatrix;
    }

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

    /*
    Currently I assume that if there's an internal run then that's good enough to return
    If there's an external run then that's also good enough to return
    Only after that there's no run mentioned

    Method doesn't care if there's an internal and an external somewhere somehow, just that the run exists
     */
    double evaluateRun(int suit, int val, int[][] memoryMatrix) {
        boolean internal = checkInternalRun(suit, val,memoryMatrix);

        if(internal){
            return this.heuristicInternalRun(val);
        }

        boolean external = checkExternalRun(suit,val,memoryMatrix);

        if(external){
            return this.heuristicExternalRun(val);
        }

        /*
        I'll only return free if both the front and back are unknown
        If one of them is guaranteed to be out of our hands, I'll return the never heuristic

        Arbitrary but screw it it works
         */

        int front = checkCardNearby(suit,val, 1, memoryMatrix);
        int back = checkCardNearby(suit,val, -1, memoryMatrix);

        if ((front >=2) || (back>=2)) {
            return heuristicForNever(val);
        } else {
            return heuristicForFree(val);
        }

    }

    /*
    Returns true if there's a chance of an internal run (meaning one of the adjacent cards is in the hand)
    Return false if there's no chance of an internal run (meaning none of the adjacent cards are in the hand)

    I should do an extra check for cards that are more in the middle of the pack
    As those usually have an easier time making runs
    But for now I'll just ignore it completely
    TODO: Add this little fix maybe eventually
     */
    boolean checkInternalRun(int suit, int val, int[][] memoryMatrix){
        int front = checkCardNearby(suit, val, 1, memoryMatrix);
        int back = checkCardNearby(suit,val,-1, memoryMatrix);


        return ((front == 1) || (back == 1));
    }

    boolean checkExternalRun(int suit, int val, int[][] memoryMatrix){
        int front = checkCardNearby(suit, val, 2, memoryMatrix);
        int back = checkCardNearby(suit,val,-2, memoryMatrix);


        return ((front == 1) || (back == 1));
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

    /**
     *
     * @param suit suit of the card to be checked
     * @param val card currently being checked
     * @param loc location to be checked (relative to the card, so it's in front or in the back)
     *            Meaning it checks [suit][val+loc] in the memory matrix
     * @param memoryMatrix the current memory matrix
     * @return whether the related position exists or not, with the following way of explaining the results:
     *  For the card that's being checked:
     *         If not known return 0
     *         If it's in the player's hand: return 1
     *         If it's known to be in the other player's hand: return 2
     *         If it's known to be discarded: return 3
     *         If it's out of bounds, return 3
     *         If it's already in another meld (which is more valuable) then return 3
     */

    // This feels like a major speed obstacle for the AI
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
    A 4 and a 5 are more valuable than a 4 and a 6, as the 4 and 5 have two options for completing the run
    So I'm calling the 4 and 5 and "internal" run, and the 4 and 6 and "external" run
    And giving them some heuristics

    These are used instead of approx for the runs, and only for the runs
     */
    double heuristicInternalRun(int value){
        return valInGinRummy(value)*this.intRun;
    }

    double heuristicExternalRun(int value){
        return valInGinRummy(value)*this.extRun;
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

            int[][] cloneOfMatrix = cloneResetMemMatrix(this.memoryMatrix, layout);

            // Here I'm adding the value of all the melds to valOfHand
            for(Meld melds: layout.viewMelds()){
                for(MyCard card: melds.viewMeld()){
                    valOfHand += this.heuristicForMeld(card.rank.index);
                }

            }

            // Here the method takes care of evaluating all of the cards that are free
            for(MyCard deadWoodCard: layout.viewUnusedCards()){
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

    /**
     * Method goes through the 11 different card combinations and checks if there's one of them that assures a knock
     * @param cardList set of 11 cards to be checked
     * @param knockValue knockValue to check for, usually 10
     * @return True if a combination allows for the player to knock
     *  False if there's no way to knock based off this hand
     */

    public static boolean findIfCardsMakeKnockingPossible(List<MyCard> cardList, int knockValue){
        for(MyCard aCard: cardList){

            List<MyCard> tempList = new ArrayList<>(cardList);
            tempList.remove(aCard);

            HandLayout layout = Finder.findBestHandLayout(tempList);

            if(layout.getDeadwood() <= knockValue){
                return true;
            }

        }

        return false;


    }

    @Override
    public String toString() {
        return "GreedyV3: " +
                " CardInMeld: " + this.cardInMeld +
                " CardCloseToMeld: " + this.cardCloseToMeld +
                " CardFree: " + this.cardFree +
                " CardNever: " + this.cardNever;
    }

}
