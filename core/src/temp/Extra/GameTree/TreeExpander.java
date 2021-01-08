package temp.Extra.GameTree;

import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.States.RoundState;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * Expands the tree based on current game state.
 * Expands = returns all possible actions currently.
 */
public class TreeExpander {

    public static List<? extends Action> getPossibleActions(RoundState curState) {
        int index = curState.turn().playerIndex;
        switch (curState.turn().step) {
            case KnockOrContinue:
                return getPossibleKnockActions(index, curState.getCards(curState.turn().playerIndex));
            case Pick:
                return getPossiblePickActions(index, curState.deckSize(), curState.peekDiscard());
            case Discard:
                return getPossibleDiscardActions(index, curState.getCards(curState.turn().playerIndex));
            case LayoutConfirmation:
                return getPossibleLayoutConfirmationActions(index, curState.getCards(curState.turn().playerIndex));
            case Layoff:
                return getPossibleLayoffActions(index, Finder.findBestHandLayout(curState.getCards(curState.turn().playerIndex)), Finder.findBestHandLayout(curState.getCards(curState.knocker())).melds());
            default:
                return new ArrayList<>();
        }
    }

    public static List<KnockAction> getPossibleKnockActions(int index, List<MyCard> cards) {
        List<HandLayout> layouts = Finder.findAllLayouts(cards);
        List<KnockAction> possibleActions = new ArrayList<>();
        for (HandLayout possible : layouts) {
            if (possible.deadwoodValue() <= GameRules.minDeadwoodToKnock) {
                possibleActions.add(new KnockAction(index, true, possible));
            }
        }
        possibleActions.add(new KnockAction(index, false, null));
        return possibleActions;
    }

    public static List<PickAction> getPossiblePickActions(int index, int deckSize, MyCard topOfDiscard) {
        List<PickAction> possibleActions = new ArrayList<>();
        if (topOfDiscard != null) {
            possibleActions.add(new PickAction(index, false, topOfDiscard));
        }
        if (deckSize != 0) {
            possibleActions.add(new PickAction(index, true, null));
        }
        return possibleActions;
    }

    public static List<DiscardAction> getPossibleDiscardActions(int index, List<MyCard> cards) {
        List<DiscardAction> possibleActions = new ArrayList<>();
        for (MyCard card : cards) {
            possibleActions.add(new DiscardAction(index, card));
        }
        return possibleActions;
    }

    public static List<LayoutConfirmationAction> getPossibleLayoutConfirmationActions(int index, List<MyCard> cards) {
        List<LayoutConfirmationAction> possibleActions = new ArrayList<>();
        List<HandLayout> layouts = Finder.findAllLayouts(cards);
        for (HandLayout possible : layouts) {
            possibleActions.add(new LayoutConfirmationAction(index, possible));
        }
        return possibleActions;
    }

    public static List<LayoffAction> getPossibleLayoffActions(int index, HandLayout layout, List<Meld> knockerMelds) {
        List<LayoffAction> possibleActions = new ArrayList<>();
        List<MyCard> unusedCards = layout.unused();
        //TODO? make it return all possible ways you can layoff
        possibleActions.add(new LayoffAction(index, null));
        return possibleActions;
    }
}
