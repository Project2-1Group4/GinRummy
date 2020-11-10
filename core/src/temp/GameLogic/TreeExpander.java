package temp.GameLogic;

import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.Calculator;
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

    public static List<? extends Action> getPossibleActions(State curState){
        switch (curState.getStep()) {
            case KnockOrContinue:
                return getPossibleKnockActions(curState);
            case Pick:
                return getPossiblePickActions(curState);
            case Discard:
                return getPossibleDiscardActions(curState);
            case LayoutConfirmation:
                return getPossibleLayoutConfirmationActions(curState);
            case LayOff:
                return getPossibleLayoffActions(curState);
            default:
                return new ArrayList<>();
        }
    }

    private static List<KnockAction> getPossibleKnockActions(State curState){
        assert curState.getStep() == State.StepInTurn.KnockOrContinue;

        List<HandLayout> layouts = Calculator.getAllLayouts(curState.getActorState().viewHand());
        List<KnockAction> possibleActions = new ArrayList<>();
        for (HandLayout layout : layouts) {
            if(layout.getDeadwood()<= GameRules.minDeadwoodToKnock){
                possibleActions.add(new KnockAction(curState.getActorNumber(),true,layout));
            }
        }
        possibleActions.add(new KnockAction(curState.getActorNumber(),false,null));
        return possibleActions;
    }

    private static List<PickAction> getPossiblePickActions(State curState){
        assert curState.getStep() == State.StepInTurn.Pick;

        List<PickAction> possibleActions = new ArrayList<>();
        if(!curState.isDiscardEmpty()) {
            possibleActions.add(new PickAction(curState.getActorNumber(), false, curState.peekDiscardTop()));
        }
        if(!curState.isDeckEmpty()) {
            possibleActions.add(new PickAction(curState.getActorNumber(), true, null));
        }
        return possibleActions;
    }

    private static List<DiscardAction> getPossibleDiscardActions(State curState){
        assert curState.getStep()== State.StepInTurn.Discard;

        List<DiscardAction> possibleActions = new ArrayList<>();
        for (MyCard card : curState.getActorState().viewHand()) {
            possibleActions.add(new DiscardAction(curState.getActorNumber(),card));
        }
        return possibleActions;
    }

    private static List<LayoutConfirmationAction> getPossibleLayoutConfirmationActions(State curState){
        assert curState.getStep() == State.StepInTurn.LayoutConfirmation;

        List<LayoutConfirmationAction> possibleActions = new ArrayList<>();
        List<HandLayout> layouts = Calculator.getAllLayouts(curState.getActorState().viewHand());
        for (HandLayout layout : layouts) {
            possibleActions.add(new LayoutConfirmationAction(curState.getActorNumber(),layout));
        }
        return possibleActions;
    }

    private static List<LayoffAction> getPossibleLayoffActions(State curState){
        assert curState.getStep() == State.StepInTurn.LayOff;

        List<LayoffAction> possibleActions = new ArrayList<>();
        List<MyCard> unusedCards = curState.getActorState().viewUnusedCards();
        for (Meld meld : curState.getKnockerState().viewMelds()) {
            for (MyCard unusedCard : unusedCards) {
                if(meld.isValidWith(unusedCard)){
                    possibleActions.add(new LayoffAction(curState.getActorNumber(),unusedCard,meld));
                }
            }
        }
        possibleActions.add(new LayoffAction(curState.getActorNumber(),null,null));
        return possibleActions;
    }
}
