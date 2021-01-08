package temp.GameLogic.Logic;

import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Recursion is a nightmare.
 */
class MeldCreator {

    /**
     * Finds all possible meld combinations of current hand. No overlapping melds.
     *
     * @param usedMeld   current stack of melds
     * @param hand       current hand to be evaluated
     * @param savedMelds stack of melds saved (Only saved once no more melds left)
     * @return all stack of melds found
     */
    protected static List<Stack<Meld>> recursiveMeld(Stack<Meld> usedMeld, int[][] hand, List<Stack<Meld>> savedMelds) {
        if (usedMeld.size() != 0) {
            setMeldTo(hand, usedMeld.peek(), 0);
        }
        List<Meld> melds = getAllPossibleMelds(hand);

        if (melds.size() == 0) {
            savedMelds.add((Stack<Meld>) usedMeld.clone());
            return savedMelds;
        }
        for (Meld meld : melds) {
            usedMeld.add(meld);
            recursiveMeld(usedMeld, hand, savedMelds);
            setMeldTo(hand, meld, 1);
            usedMeld.pop();
        }
        return savedMelds;
    }

    /**
     * Gets all possible melds. Includes overlapping.
     *
     * @param hand wanted to be evaluated
     * @return list of all possible melds of given hand. Uses duplicates
     */
    private static List<Meld> getAllPossibleMelds(int[][] hand) {
        List<Meld> melds = new ArrayList<>();
        for (int i = 0; i < hand.length; i++) {
            for (int j = 0; j < hand[i].length; j++) {
                if (hand[i][j] == 1) {
                    int offset = 0;
                    // Does a run start from this [i,j]?
                    List<Integer> consecutive = new ArrayList<>();
                    while (j + offset < hand[i].length && hand[i][j + offset] == 1) {
                        consecutive.add(j+offset);
                        offset++;
                    }
                    if (consecutive.size() >= 3) {
                        melds.add(createMeld(i, j, consecutive, null));
                    }
                    offset = 0;
                    consecutive.clear();
                    // Does a set start from this [i,j]?
                    while (i + offset < hand.length) {
                        if(hand[i + offset][j] == 1) {
                            consecutive.add(i+offset);
                        }
                        offset++;
                    }
                    if (consecutive.size() >= 3) {
                        melds.add(createMeld(i, j, null, consecutive));
                    }
                }
            }
        }
        return melds;
    }

    /**
     * Creates meld using the given info.
     *
     * @param i      suit of start of meld
     * @param j      rank of start of meld
     * @param kRight direction of meld. = 0 if kDown != 0
     * @param kDown  direction of meld.== 0 if kRight != 0
     * @return created meld
     */
    private static Meld createMeld(int i, int j, List<Integer> kRight, List<Integer> kDown) {
        assert kRight !=null || kDown != null;

        Meld meld = new Meld();
        if(kRight!=null) {
            for (Integer integer : kRight) {
                meld.addCard(new MyCard(i, integer));
            }
        }else {
            for (Integer integer : kDown) {
                meld.addCard(new MyCard(integer, j));
            }
        }
        return meld;
    }

    /**
     * Quality of life method because array is mutable.
     *
     * @param array to update
     * @param meld  used to update
     * @param val   to change array values to
     * @return modified array
     */
    private static int[][] setMeldTo(int[][] array, Meld meld, int val) {
        for (MyCard myCard : meld.viewMeld()) {
            array[myCard.suit.index][myCard.rank.index] = val;
        }
        return array;
    }
}
