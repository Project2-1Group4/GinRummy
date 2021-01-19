package GameLogic.Logic;

import GameLogic.Entities.HandLayout;
import GameLogic.Entities.Layoff;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import com.badlogic.gdx.Gdx;
import Extra.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Mainly (Only) handles the knocking/gin part
 */
public class Finder {

    /**
     * Finds set of melds that has the lowest deadwood and highest value (if tie).
     * Could be moved to GamePlayer
     * @param cards list of playerCards to be considered
     * @return list of melds that maximize players hand value
     */
    public static HandLayout findBestHandLayout(List<MyCard> cards) {

        List<HandLayout> handLayouts = findAllLayouts(cards);
        // To stop auto sorting of unused cards
        HandLayout bestLayout = findLowestDeadwoodLayout(handLayouts);
        List<MyCard> unusedCards = bestLayout.unused();
        for (MyCard card : cards) {
            for (int i = 0; i < unusedCards.size(); i++) {
                if (card.equals(unusedCards.get(i))) {
                    if (bestLayout.removeUnusedCard(unusedCards.get(i))) {
                        bestLayout.addUnusedCard(card);
                        unusedCards.remove(i);
                        break;
                    }
                    System.out.println("Calculator.findBestHandLayout() ERROR ERROR ERROR");
                    Gdx.app.exit();
                }
            }
        }
        return bestLayout;
    }

    /**
     * Finds all possible layouts with a given list of playerCards
     *
     * @param cards list of playerCards to evaluate
     * @return list of all possible layouts
     */
    public static List<HandLayout> findAllLayouts(List<MyCard> cards) {
        int[][] hand = new int[MyCard.Suit.values().length][MyCard.Rank.values().length];
        for (MyCard card : cards) {
            hand[card.suit.index][card.rank.index] = 1;
        }
        List<Stack<Meld>> meldCombinations = MeldCreator.recursiveMeld(new Stack<Meld>(), hand, new ArrayList<Stack<Meld>>());

        List<HandLayout> handLayouts = new ArrayList<>();
        for (Stack<Meld> meldCombination : meldCombinations) {
            handLayouts.add(new HandLayout(copy(hand), meldCombination));
        }
        return handLayouts;
    }

    /**
     * Helper for findBestHandLayout(). Takes lowest deadwood then  highest value if there's a tie.
     *
     * @param handLayoutValues all values of set of melds considered
     * @return best hand found
     */
    private static HandLayout findLowestDeadwoodLayout(List<HandLayout> handLayoutValues) {
        int index = 0;
        int maxVal = 0;
        int lowestDead = Integer.MAX_VALUE;
        for (int i = 0; i < handLayoutValues.size(); i++) {
            if (handLayoutValues.get(i).deadwoodValue() < lowestDead) {
                index = i;
                maxVal = handLayoutValues.get(i).meldValue();
                lowestDead = handLayoutValues.get(i).deadwoodValue();
            }
            else if (handLayoutValues.get(i).deadwoodValue() == lowestDead && handLayoutValues.get(i).meldValue() >= maxVal) {
                index = i;
                maxVal = handLayoutValues.get(i).meldValue();
                lowestDead = handLayoutValues.get(i).deadwoodValue();
            }
        }
        return handLayoutValues.get(index);
    }

    /**
     * Finds hand layout with lowest deadwood in the given list. If another player has the same deadwood as the knocker, knocker overpowers.
     *
     * @param handLayouts best hands found
     * @return index of the winning hand
     */
    public static int findLowestDeadwoodIndex(List<HandLayout> handLayouts, int knockerDeadwood, int knockerIndex) {
        int lowestDeadwood = knockerDeadwood;
        int lowestDeadwoodIndex = knockerIndex;
        for (int i = 0; i < handLayouts.size(); i++) {
            if (lowestDeadwood > handLayouts.get(i).deadwoodValue()) {
                lowestDeadwood = handLayouts.get(i).deadwoodValue();
                lowestDeadwoodIndex = i;
            }
        }
        return lowestDeadwoodIndex;
    }

    /**
     * Calculates the amount of points won only based on deadwood differences. Nothing else.
     *
     * @param handLayouts list of layouts of player (including winner because that will give 0)
     * @param winnerDeadwood value of the winners deadwood
     * @return sum(losers deadwood - winner deadwood)
     */
    public static int getPointsToAdd(List<HandLayout> handLayouts, int winnerDeadwood) {
        int points = 0;
        for (HandLayout handLayout : handLayouts) {
            points += handLayout.deadwoodValue() - winnerDeadwood;
        }
        return points;
    }

    /**
     * returns bonus point the winning player gets based on Gin Rummy Bonus Point rules
     * @param knockerIndex index of knocker
     * @param winnerIndex index of winner
     * @param knockerDeadwood deadwood in knocker hand
     * @return bonus win
     */
    public static int getBonusPoints(int knockerIndex, int winnerIndex, int knockerDeadwood){
        int bonus = 0;
        if(knockerDeadwood==0){
            bonus+= GameRules.ginBonus;
        }
        if(knockerIndex!=winnerIndex){
            bonus+= GameRules.undercutBonus;
        }
        return bonus;
    }

    /**
     * Finds all possible layoffs that can be made, given a set of playerCards and a set of melds
     * Could be moved to GamePlayer
     * @param allCards playerCards to layoff
     * @param knockerMelds melds to lay into
     * @return list of possibilities
     */
    public static List<Layoff> findAllPossibleLayoffs(List<MyCard> allCards, List<Meld> knockerMelds) {
        List<Layoff> layoffs = new ArrayList<>();
        for (MyCard card : allCards) {
            for (Meld meld : knockerMelds) {
                if(meld.isValidWith(card)){
                    layoffs.add(new Layoff(card, meld));
                    break;
                }
            }
        }
        return layoffs;
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