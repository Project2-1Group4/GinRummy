package temp.GameLogic.MELDINGOMEGALUL;

import com.badlogic.gdx.Gdx;
import temp.GameLogic.MyCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Mainly (Only) handles the knocking/gin part
 */
public class Calculator {

    /**
     * Finds meldSet with lowest deadwood. If another player has the same deadwood as the knocker, knocker overpowers.
     *
     * @param handLayouts best hands found
     * @return index of the winning hand
     */
    public static int getLowestDeadwoodIndex(List<HandLayout> handLayouts, int knockerDeadwood) {
        int lowestDeadwood = knockerDeadwood;
        int lowestDeadwoodIndex = 0;
        for (int i = 0; i < handLayouts.size(); i++) {
            if (lowestDeadwood > handLayouts.get(i).getDeadwood()) {
                lowestDeadwood = handLayouts.get(i).getDeadwood();
                lowestDeadwoodIndex = i;
            }
        }
        return lowestDeadwoodIndex;
    }

    /**
     * Calculates the amount of points won only based on deadwood differences. Nothing else.
     *
     * @param handLayouts       1 meldSet for every actor (even winner but that gives 0)
     * @param winnerDeadwood value of the winners deadwood
     * @return sum(losers deadwood - winner deadwood)
     */
    public static int getPointsToAdd(List<HandLayout> handLayouts, int winnerDeadwood) {
        int points = 0;
        for (int i = 0; i < handLayouts.size(); i++) {
            points += handLayouts.get(i).getDeadwood() - winnerDeadwood;
        }
        return points;
    }

    /**
     * Finds set of melds that has the highest value and lowest deadwood.
     *
     * @param cards list of cards to be considered
     * @return list of melds that maximize actors hand value
     */
    public static HandLayout getBestMelds(List<MyCard> cards) {
        int[][] hand = new int[MyCard.Suit.values().length][MyCard.Rank.values().length];
        for (MyCard card : cards) {
            hand[card.suit.index][card.rank.index] = 1;
        }
        List<Stack<Meld>> meldCombinations = MeldCreator.recursiveMeld(new Stack<Meld>(), hand, new ArrayList<Stack<Meld>>());

        List<HandLayout> handLayouts = new ArrayList<>();
        for (int i = 0; i < meldCombinations.size(); i++) {
            handLayouts.add(new HandLayout(copy(hand), meldCombinations.get(i)));
        }

        // To stop auto sorting of unused cards
        HandLayout bestMeld = getBestHandLayout(handLayouts);
        List<MyCard> unusedCards = bestMeld.viewUnusedCards();
        for (MyCard card : cards) {
            for (int i = 0; i < unusedCards.size(); i++) {
                if(card.same(unusedCards.get(i))){
                    if(bestMeld.removeUnusedCard(unusedCards.get(i))) {
                        bestMeld.addUnusedCard(card);
                        unusedCards.remove(i);
                        break;
                    }
                    System.out.println("Calculator.getBestMelds() ERROR ERROR ERROR");
                    Gdx.app.exit();
                }
            }
        }
        return getBestHandLayout(handLayouts);
    }

    /**
     * Returns the first found index that can be added to meld
     *
     * @param cards list of cards you want to pick from
     * @param meld you want to fit card in
     * @return null if none, otherwise index
     */
    public static Integer getFirstIndexThatFitsInMeld(List<MyCard> cards, Meld meld){
        if(meld.getType()== Meld.MeldType.Run){
            return getCardIndexRun(cards,meld);
        }
        else{
            return getCardIndexSet(cards,meld);
        }
    }

    /**
     * Only used for runs
     *
     * @param cards list of cards you want to pick from
     * @param meld you want to fit card in
     * @return null if none, otherwise index
     */
    private static Integer getCardIndexRun(List<MyCard> cards, Meld meld){
        assert meld.getType()== Meld.MeldType.Run;

        MyCard.Suit runSuit = meld.viewMeld().get(0).suit;
        // For all cards in the meld
        for (int j = 0; j < meld.viewMeld().size(); j++) {
            // For all unused cards
            for (int i = 0; i < cards.size(); i++) {
                // If the card is of the same suit
                if(cards.get(i).suit==runSuit){
                    // If card is in the neighbourhood of the card
                    if(Math.abs(cards.get(i).rank.value-meld.viewMeld().get(j).rank.value)<=1){
                        return i;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Only used for sets
     *
     * @param cards list of cards you want to pick from
     * @param meld you want to fit card in
     * @return null if none, otherwise index
     */
    private static Integer getCardIndexSet(List<MyCard> cards, Meld meld){
        assert meld.getType()== Meld.MeldType.Set;

        MyCard.Rank setRank = meld.viewMeld().get(0).rank;
        // For all unused cards
        for (int i = 0; i < cards.size(); i++) {
            // If the card is of the same rank
            if(cards.get(i).rank==setRank){
                return i;
            }
        }
        return null;
    }

    /**
     * Finds meld that matches the toFind meld in the list given
     *
     * @param toFind meld to find
     * @param melds list to find meld in
     * @return null if not found otherwise meld
     */
    public static Integer getMeld(Meld toFind, List<Meld> melds){

        for (int i=0;i<melds.size(); i++) {
            int cardsFound = 0;
            for (MyCard meldC : melds.get(i).viewMeld()) {
                for (MyCard toFindC : toFind.viewMeld()) {
                    if(meldC.same(toFindC)){
                        cardsFound++;
                        break;
                    }
                }
            }
            if(cardsFound==melds.get(i).viewMeld().size()){
                return i;
            }
        }
        return null;
    }

    /**
     * Takes lowest deadwood then  highest value if there's a tie.
     *
     * @param handLayoutValues all values of set of melds considered
     * @return best hand found
     */
    private static HandLayout getBestHandLayout(List<HandLayout> handLayoutValues) {
        int index = 0;
        int maxVal = 0;
        int lowestDead = Integer.MAX_VALUE;
        for (int i = 0; i < handLayoutValues.size(); i++) {
            if (handLayoutValues.get(i).getDeadwood() <= lowestDead) {
                if (handLayoutValues.get(i).getValue() >= maxVal) {
                    index = i;
                    maxVal = handLayoutValues.get(i).getValue();
                    lowestDead = handLayoutValues.get(i).getDeadwood();
                }
            }
        }
        return handLayoutValues.get(index);
    }

    /**
     * Quality of life method because array is mutable.
     *
     * @param matrix to copy
     * @return copy of matrix
     */
    public static int[][] copy(int[][] matrix) {
        int[][] myInt = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            int[] aMatrix = matrix[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

}
