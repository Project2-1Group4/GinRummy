package temp.GameLogic;

import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;

/**
 * Expands the tree based on current game state.
 * Expands = returns all possible actions currently.
 */
public class TreeExpander {

    public static List<? extends Action> getPossibleActions(State curState) {
        int index = curState.getPlayerNumber();
        switch (curState.getStep()) {
            case KnockOrContinue:
                return getPossibleKnockActions(index, curState.getPlayerState().viewHandLayout());
            case Pick:
                return getPossiblePickActions(index, curState.getDeckSize(), curState.peekDiscardTop());
            case Discard:
                return getPossibleDiscardActions(index, curState.getPlayerState().viewHandLayout());
            case LayoutConfirmation:
                return getPossibleLayoutConfirmationActions(index, curState.getPlayerState().viewHandLayout());
            case LayOff:
                return getPossibleLayoffActions(index, curState.getPlayerState().viewHandLayout(), curState.getKnockerState().viewMelds());
            default:
                return new ArrayList<>();
        }
    }

    public static List<KnockAction> getPossibleKnockActions(int index, HandLayout layout) {
        List<HandLayout> layouts = Finder.findAllLayouts(layout.viewAllCards());
        List<KnockAction> possibleActions = new ArrayList<>();
        for (HandLayout possible : layouts) {
            if (possible.getDeadwood() <= GameRules.minDeadwoodToKnock) {
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

    public static List<DiscardAction> getPossibleDiscardActions(int index, HandLayout layout) {
        List<DiscardAction> possibleActions = new ArrayList<>();
        for (MyCard card : layout.viewAllCards()) {
            possibleActions.add(new DiscardAction(index, card));
        }
        return possibleActions;
    }

    public static List<LayoutConfirmationAction> getPossibleLayoutConfirmationActions(int index, HandLayout layout) {
        List<LayoutConfirmationAction> possibleActions = new ArrayList<>();
        List<HandLayout> layouts = Finder.findAllLayouts(layout.viewAllCards());
        for (HandLayout possible : layouts) {
            possibleActions.add(new LayoutConfirmationAction(index, possible));
        }
        return possibleActions;
    }

    public static List<LayoffAction> getPossibleLayoffActions(int index, HandLayout layout, List<Meld> knockerMelds) {
        List<LayoffAction> possibleActions = new ArrayList<>();
        List<MyCard> unusedCards = layout.viewUnusedCards();
        for (Meld meld : knockerMelds) {
            for (MyCard unusedCard : unusedCards) {
                if (meld.isValidWith(unusedCard)) {
                    possibleActions.add(new LayoffAction(index, unusedCard, meld));
                }
            }
        }
        possibleActions.add(new LayoffAction(index, null, null));
        return possibleActions;
    }
}
