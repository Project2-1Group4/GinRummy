package temp.GameLogic.GameState;

import com.badlogic.gdx.Gdx;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.LayoffAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.Calculator;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GameLogic.Validator;
import temp.GameRules;

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

        if (curState.stepInTurn == State.StepInTurn.MeldConfirmation || curState.stepInTurn == State.StepInTurn.LayOff) {
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
        if (GameRules.print) System.out.println("Player "+curState.getActorNumber()+" knocked with "+curState.getActor().viewHandLayout());

        curState.stepInTurn = State.StepInTurn.MeldConfirmation;
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

    /**
     * Executes the knock or continue order given. Uses hand layout in actor. Not in game save.
     *
     * @param move     null if no move, true if knock, false if continue // Can be made into an enum
     * @param curState current game state
     * @return true if executed, false if not
     */
    public static boolean knockOrContinue(Boolean move, State curState) {
        if (Validator.knockOrContinueMove(move, curState.getActor().viewHandLayout())) {
            curState.getActorState().handLayout = curState.getActor().viewHandLayout();
            if (move) {
                knocked(curState.getActorNumber(), curState);
            }
            curState.movesDone.add(new KnockAction(curState.getActorNumber(),move));
            return true;
        }
        return false;
    }

    /**
     * Executes pick order given.
     *
     * @param move     null if no move (shouldn't happen), true if deck and false if discard pile // can be made into an enum
     * @param curState current game state
     * @return true if executed, false if not
     */
    public static boolean pickDeckOrDiscard(Boolean move, State curState) {
        if (Validator.pickDeckOrDiscard(move, curState.isDeckEmpty(),
                curState.isDiscardEmpty())) {

            if (move) {
                curState.getActorState().handLayout.addUnusedCard(curState.pickDeckTop());
            } else {
                curState.getActorState().handLayout.addUnusedCard(curState.pickDiscardTop());
            }
            curState.movesDone.add(new PickAction(curState.getActorNumber(),move));
            return true;
        }
        return false;
    }

    /**
     * Removes card from current actor's hand and adds it to the discard pile.
     *
     * @param card     to discard
     * @param curState current game state
     * @return true if executed, false if not
     */
    public static boolean discardCard(MyCard card, State curState) {
        if (Validator.discardCard(card, curState.getActor().viewHand())) {

            if(curState.getActorState().removeCard(card)){
                curState.addToDiscard(card);
            }else{
                System.out.println("Executor.discardCard() ERROR ERROR ERROR"); // in case some weird error occurs
                return false;
            }
            curState.movesDone.add(new DiscardAction(curState.getActorNumber(),card));
            return true;
        }
        return false;
    }

    /**
     * Saves hand layout in actor to game save if valid
     *
     * @param handLayout updated meldSet
     * @param curState current game state
     * @return true if executed, false if not
     */
    public static boolean updateHandLayout(HandLayout handLayout, State curState) {
        if (Validator.confirmLayout(handLayout.deepCopy(), curState.getActorState().handLayout.deepCopy())) {
            if(GameRules.print) System.out.println("Player "+curState.getActorNumber()+" confirmed handLayout");
            curState.getActorState().handLayout = handLayout.deepCopy();
            return true;
        }
        return false;
    }

    /**
     * Removes card from actor hand and adds to knock melds
     *
     * @param layOff to be executed
     * @param curState current game state
     */
    public static void layOff(Layoff layOff, State curState){

        if(Validator.layOff(layOff,curState.getKnockerState().viewMelds(),curState.getActorState().viewHand())){

            Integer index = Calculator.getMeld(layOff.meld,curState.getKnockerState().viewMelds());
            assert index != null;

            if(curState.getActorState().removeCard(layOff.card)){
                curState.getKnockerState().handLayout.addToMeld(index,layOff.card);
            }else {
                System.out.println("Executor.layOff() ERROR ERROR ERROR");
            }
            curState.movesDone.add(new LayoffAction(curState.getActorNumber(),layOff.card));
        }
    }
}