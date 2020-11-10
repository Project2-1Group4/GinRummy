package temp.GameLogic.GameState;

import com.badlogic.gdx.Gdx;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Calculator;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.GameLogic.TreeExpander;
import temp.GameRules;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Executes validates moves. Only class that should(/can) modify the game state.
 * <p>
 * Saving of game tree should be done when any method here is called I guess.
 * <p>
 * Can be done by saving initial game state (will make starting a new round extremely easy)
 * and then having a list of all actions executed to get the current state:
 * <p>
 * Player x |CONTINUED OR KNOCKED|
 * Player x Picked |CARD| from |DECK OR DISCARD|
 * Player x Discard Card |CARD|
 * ^ One turn
 * <p>
 * All methods return type can be made void,
 * except for update(), getWinner() and startNewRound()
 */
public class Executor {
    private static Integer seed =10;

    /* GAME/ROUND INITIALISATION */

    /**
     * Either creates a new game or starts a new round using the previous game (round)
     *
     * @param shuffles number of times to shuffle the deck (move 1 card to another place in the deck)
     * @param curState current game state
     * @return new game state
     */
    public static State startNewRound(int shuffles, State curState) {
        State newState;
        if (curState == null) {
            newState = new StateBuilder().build();
        } else {
            for (int i = 0; i < curState.scores.length; i++) {
                if (curState.scores[i] >= GameRules.pointsToWin) {
                    //TODO properly make end of game screen
                    System.out.println("Player " + i + " won with "+curState.scores[i]+" points");
                    System.out.println("FInal scores: ");
                    for (int j = 0; j < curState.scores.length; j++) {
                        System.out.println("Player "+j+" "+curState.scores[j]);
                    }
                    Gdx.app.exit();
                }
            }
            //TODO save initial creation such as custom deck, custom actors
            newState = new StateBuilder()
                    .setScores(curState.scores)
                    .setRound(curState.round+1)
                    .setSecondsPerStep(curState.secondsPerStep)
                    .build();
        }
        shuffleDeck(shuffles, newState);
        distributeCards(GameRules.baseCardsPerHand, newState);
        startDiscardPile(newState);

        return newState;
    }

    /**
     * Shuffles the deck of the current game state
     *
     * @param shuffles number of times to shuffle the deck (move 1 card to another place in the deck)
     * @param curState current game state
     */
    public static void shuffleDeck(int shuffles, State curState) {
        if (GameRules.print) System.out.println("Deck shuffled");

        Random rd;
        if(seed!=null){
            rd = new Random(seed);
        }else{
            rd = new Random();
        }
        for (int i = 0; i < shuffles; i++) {
            MyCard card = curState.deck.remove(rd.nextInt(curState.deck.size()));
            curState.deck.add(rd.nextInt(curState.deck.size()), card);
        }

    }

    /**
     * Distributes the cards to all players
     *
     * @param cardsPerHand amount of cards in every player's hand
     * @param curState     current game state
     */
    public static void distributeCards(int cardsPerHand, State curState) {
        if (GameRules.print) System.out.println("Cards distributed");

        for (int i =0;i<curState.actorStates.size();i++) {

            for (int j = 0; j < cardsPerHand; j++) {
                curState.actorStates.get(i).handLayout.addUnusedCard(curState.pickDeckTop());
            }
            curState.actors.get(i).update(curState.actorStates.get(i).viewHandLayout(), i);
        }

    }

    /**
     * Adds the top of the deck to the discard pile
     *
     * @param curState current game state
     */
    public static void startDiscardPile(State curState) {
        if (GameRules.print) System.out.println("Discard pile started");

        if (curState.discardPile.isEmpty()) {
            curState.addToDiscard(curState.pickDeckTop());
        }

    }

    /* STATE UPDATE */

    /**
     * @param curState current game state
     * @param deltaT   time elapsed between now and previous render call
     * @return true if there is no more time for the current step
     */
    public static boolean update(State curState, float deltaT) {
        curState.curTime -= deltaT;
        return curState.curTime <= 0;
    }

    /**
     * Goes to the next step of the game (knock, pick, discard, meld creation, laying off, point assignment, next player).
     *
     * @param curState current game state
     */
    public static void nextStep(State curState) {
        if (GameRules.print) System.out.println(curState.viewLastAction());

        curState.getActor().update(curState.getActorState().viewHandLayout(),curState.getActorNumber());

        if (curState.stepInTurn == State.StepInTurn.LayoutConfirmation || curState.stepInTurn == State.StepInTurn.LayOff) {
            getNextPlayer(curState);
            if (curState.knocker == curState.playerTurn) {
                curState.stepInTurn = curState.stepInTurn.getNext();
            }
        } else {
            if (curState.stepInTurn == State.StepInTurn.Discard) {
                getNextPlayer(curState);
            }
            curState.stepInTurn = curState.stepInTurn.getNext();
        }
        curState.curTime = curState.secondsPerStep[curState.getStep().index];

        if (GameRules.print) System.out.println("\nNew step: Player "+curState.getActorNumber()+" "+curState.stepInTurn);

    }

    /**
     * Updates current player turn and counts which turn it is in the current round
     *
     * @param curState current game state
     */
    private static void getNextPlayer(State curState) {
        if(curState.playerTurn==0){
            curState.turnInRound++;
        }
        curState.playerTurn = (curState.playerTurn + 1) % curState.numberOfPlayers;
    }

    /**
     * Starts the knocking process (meld creation then laying off (if no gin) and then point counting).
     *
     * @param knocker  index of the actor that knocked
     * @param curState current game state
     */
    private static void knocked(int knocker, State curState) {
        if (GameRules.print) System.out.println("Player "+curState.getActorNumber()+" knocked with:\n"+curState.getActor().viewHandLayout()+"\n");

        curState.stepInTurn = State.StepInTurn.LayoutConfirmation;
        curState.knocker = knocker;
    }

    /**
     * Fast forwards the game to avoid any "logical" issues.
     *
     * @param curState current game state
     */
    public static void endRound(State curState) {
        while (curState.stepInTurn != State.StepInTurn.EndOfRound) {
            if(GameRules.print) System.out.println("End round loop. Current step: "+curState.stepInTurn);
            nextStep(curState);
        }
    }

    /**
     * Assigns points won to winner.
     *
     * @param curState current game state
     */
    public static void assignPoints(State curState) {
        int winner = getWinner(curState);
        List<HandLayout> handLayouts = new ArrayList<>();
        for (ActorState actor : curState.actorStates) {
            handLayouts.add(actor.viewHandLayout());
        }
        int pointsWon = Calculator.getPointsToAdd(handLayouts, curState.actors.get(winner).viewHandLayout().getDeadwood());

        if (winner == curState.knocker) {
            if(curState.getKnocker().viewHandLayout().getDeadwood() == 0){
                if(GameRules.print) System.out.println("Gin");
                pointsWon += GameRules.ginBonus;
            }
            else{
                if(GameRules.print) System.out.println("Knock");
            }
        } else {
            if(GameRules.print) System.out.println("Undercut");
            pointsWon += GameRules.undercutBonus;
        }
        curState.scores[winner] += pointsWon;
    }

    /**
     * Gets index of winner of the round.
     *
     * @param curState current game state
     * @return index of winner
     */
    private static int getWinner(State curState) {
        List<HandLayout> handLayouts = new ArrayList<>();
        for (ActorState actor : curState.actorStates) {
            handLayouts.add(actor.viewHandLayout());
        }
        return Calculator.getLowestDeadwoodIndex(handLayouts, handLayouts.get(curState.knocker).getDeadwood());
    }

    /*ACTOR UPDATE*/

    public static boolean execute(Action action, State curState){
        if(action==null){
            return false;
        }
        List<? extends Action> possibleActions = TreeExpander.getPossibleActions(curState);
        for (Action possibleAction : possibleActions) {
            if(action.same(possibleAction)){
                boolean executed = false;
                if(action instanceof KnockAction){
                    executed = knock((KnockAction)action,curState);
                }
                else if(action instanceof PickAction){
                    executed = pick((PickAction)action,curState);
                }
                else if(action instanceof DiscardAction){
                    executed = discard((DiscardAction)action,curState);
                }
                else if(action instanceof LayoutConfirmationAction){
                    executed = layoutConfirmation((LayoutConfirmationAction)action,curState);
                }
                else if(action instanceof LayoffAction){
                    executed = layoff((LayoffAction)action,curState);
                }
                else{
                    System.out.println("Executor.execute() ERROR ERROR ERROR");
                }
                if(executed){
                    curState.movesDone.add(action);
                }
                return executed;
            }
        }
        if(GameRules.print) System.out.println("ACTION NOT AVAILABLE "+action);

        return false;
    }

    private static boolean knock(KnockAction action, State curState){
        if(action.knock && action.viewLayout().getDeadwood()<=GameRules.minDeadwoodToKnock){
            knocked(curState.getKnockerNumber(),curState);
            return true;
        }else if(!action.knock){
            return true;
        }
        System.out.println("Executor.knock() ERROR ERROR ERROR");
        return false;
    }

    private static boolean pick(PickAction action, State curState){
        if(action.deck && !curState.isDeckEmpty()){
            curState.getActorState().handLayout.addUnusedCard(curState.pickDeckTop());
            return true;
        }else if(!action.deck && !curState.isDiscardEmpty() && action.card.same(curState.peekDiscardTop())){
            curState.getActorState().handLayout.addUnusedCard(curState.pickDiscardTop());
            return true;
        }
        System.out.println("Executor.pick() ERROR ERROR ERROR");
        return false;
    }

    private static boolean discard(DiscardAction action, State curState){
        for (MyCard card : curState.getActorState().viewHand()) {
            if(action.card.same(card) && curState.getActorState().removeCard(action.card)){
                curState.addToDiscard(action.card);
                return true;
            }
        }
        System.out.println("Executor.discard() ERROR ERROR ERROR");
        return false;
    }

    private static boolean layoutConfirmation(LayoutConfirmationAction action, State curState){
        if(action.layout.isValid()){
            int found = 0;
            for (MyCard card : action.layout.viewAllCards()) {
                for (MyCard myCard : curState.getActorState().viewHand()) {
                    if(card.same(myCard)){
                        found++;
                        break;
                    }
                }
            }
            if(found==curState.getActorState().viewHand().size()){
                curState.getActorState().handLayout = action.layout;
                return true;
            }
        }
        System.out.println("Executor.layoutConfirmation() ERROR ERROR ERROR");
        return false;
    }

    private static boolean layoff(LayoffAction action, State curState){
        if(action.meld==null){
            return true;
        }
        boolean foundInUnused = false;
        for (MyCard card : curState.getActorState().viewUnusedCards()) {
            if(card.same(action.card)){
                foundInUnused = true;
                break;
            }
        }
        if(foundInUnused) {
            for (Meld meld : curState.getKnockerState().viewMelds()) {
                if (meld.same(action.meld) && meld.isValidWith(action.card)) {
                    meld.addCard(action.card);
                    return true;
                }
            }
        }
        System.out.println("Executor.layoff() ERROR ERROR ERROR");
        return false;

    }
}