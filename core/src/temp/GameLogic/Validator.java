package temp.GameLogic;

import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameRules;

import java.util.List;

/**
 * Checks if move is valid/can be executed
 */
public class Validator {

    /**
     * Checks if you can knock with the given info.
     *
     * @param knock rue if player wants to knock, false if player wants to continue
     * @param layout cards currently in player hand
     * @return true move can be executed, false if not
     */
    public static boolean knockOrContinueMove(Boolean knock, HandLayout layout) {
        if (!knock) {
            return true;
        } else {
            if(validHandLayout(layout)) {
                return layout.getDeadwood() <= GameRules.minDeadwoodToKnock;
            }
            return false;
        }
    }

    /**
     * Checks if you can pick from the deck/discard pile with the given info.
     *
     * @param deck true if pick from deck, false if pick from discard
     * @return true move can be executed, false if not
     */
    public static boolean pickDeckOrDiscard(Boolean deck, boolean deckEmpty, boolean discardEmpty) {
        // Deck but deck empty
        if (deck && deckEmpty) {
            return false;
        }
        // Discard but discard empty (shouldn't happen)
        return deck || !discardEmpty;
        // Valid move
    }

    /**
     * Checks if you can discard with the given info.
     *
     * @param cardToDiscard card player wants to discard
     * @param playerHand    list of cards in player hand
     * @return true move can be executed, false if not
     */
    public static boolean discardCard(MyCard cardToDiscard, List<MyCard> playerHand) {
        for (MyCard card : playerHand) {
            // If card in hand, valid move
            if (cardToDiscard.same(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all melds in wantedLayout are valid and then that the realLayout and the wantedLayout share the same cards.
     *
     * @param wantedLayout layout you want to save
     * @param realLayout current layout
     * @return true if valid, false if not
     */
    public static boolean confirmLayout(HandLayout wantedLayout, HandLayout realLayout) {
        for (Meld meld : wantedLayout.viewMelds()) {
            if(!validMeld(meld)){
                return false;
            }
        }
        List<MyCard> realCards = realLayout.viewAllCards();
        int removed = 0;
        for (MyCard card : realCards) {
            if(!wantedLayout.removeCard(card)){
                return false;
            }
            removed++;
        }
        return realCards.size()==removed;
    }

    /**
     * Checks if all melds in layout are valid.
     *
     * @param handLayout to be checked
     * @return true if valid, no if not
     */
    private static boolean validHandLayout(HandLayout handLayout){
        for (Meld meld : handLayout.viewMelds()) {
            if(!validMeld(meld)){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the meld is valid.
     *
     * @param meld to be checked
     * @return true if valid meld, false if non-valid meld
     */
    private static boolean validMeld(Meld meld) {
        Meld.MeldType type = meld.getType();
        // If set
        if (type == Meld.MeldType.Run) {
            for (int i = 1; i < meld.viewMeld().size() - 1; i++) {
                if (meld.viewMeld().get(i).suit != meld.viewMeld().get(i + 1).suit) {
                    return false;
                }
            }
        }
        // Else run
        else {
            meld.sortByRank();
            for (int i = 1; i < meld.viewMeld().size() - 1; i++) {
                if (meld.viewMeld().get(i).rank != meld.viewMeld().get(i + 1).rank &&
                        meld.viewMeld().get(i).rank.value != meld.viewMeld().get(i + 1).rank.value + 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if you can layoff with the given info.
     *
     * @param layoff wanting to be executed
     * @param knockerMelds melds of knocker
     * @param playerCards cards in player hand
     * @return true if valid, false if not
     */
    public static boolean layOff(Layoff layoff, List<Meld> knockerMelds, List<MyCard> playerCards) {
        //Check if card to layoff is in player hand
        boolean cardFound = false;
        for (MyCard playerCard : playerCards) {
            if(playerCard.same(layoff.card)){
                cardFound=true;
                break;
            }
        }
        if(!cardFound){
            return false;
        }
        //Check if the meld given in the layoff is one of the knockers melds
        Integer index = Finder.findMeldIndexIn(layoff.meld,knockerMelds);

        //If meld found in knocker meld, validate it
        if(index!=null){
            knockerMelds.get(index).addCard(layoff.card);
            return validMeld(knockerMelds.get(index));
        }
        //Meld not found
        return false;
    }
}
