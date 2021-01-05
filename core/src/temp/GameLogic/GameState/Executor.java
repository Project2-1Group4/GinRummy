package temp.GameLogic.GameState;

import temp.GameLogic.GameActions.*;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MELDINGOMEGALUL.Meld;
import temp.GameLogic.MyCard;
import temp.GameLogic.TreeExpander;
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

    /* GAME/ROUND INITIALISATION */

    public static State startGame(int shuffles, State curState){
        curState.round = -1;
        return startNewRound(shuffles, curState);
    }
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
            if (GameRules.printEndOfRound) {
                Integer winner = getWinner(curState);
                if (winner != null) {
                    System.out.println("Player " + winner + " won round " + curState.round + " with\n" + curState.playerStates.get(winner));
                } else {
                    System.out.println("No one won round " + curState.round);
                }
                if (winner == null) {
                    winner = -1;
                }
                System.out.println("Other Players:");
                for (int i = 0; i < curState.playerStates.size(); i++) {
                    if (i != winner) {
                        System.out.println("Player " + i + ":\n" + curState.playerStates.get(i) + "\n");
                    }
                }
                System.out.println("Current scores:");
                for (int i = 0; i < curState.scores.length; i++) {
                    System.out.println("Player " + i + ": " + curState.scores[i]);
                }
                System.out.println();
            }
            int max = 0;
            for (int i = 0; i < curState.scores.length; i++) {
                if(curState.scores[i]>=GameRules.pointsToWin && curState.scores[i]> max){
                    curState.gameWinnerIndex =i;
                    max = curState.scores[i];
                }
            }
            if(curState.endOfGame()){
                return curState;
            }
            newState = new StateBuilder()
                    .setRandomizer(curState.seed)
                    .useCustomDeck(curState.initDeck)
                    .addPlayers(curState.players)
                    .setScores(curState.scores)
                    .setRound(curState.round + 1)
                    .setSecondsPerStep(curState.secondsPerStep)
                    .setStartingPlayer((curState.startingPlayer + 1) % curState.numberOfPlayers)
                    .build();
        }
        shuffleList(newState.seed, shuffles, newState.deck);
        startDiscardPile(newState);
        distributeCards(GameRules.baseCardsPerHand, newState);
        newState.playerTurn = newState.startingPlayer;
        return newState;
    }

    /**
     * @param rd randomizer
     * @param shuffles number of shuffles
     * @param cards list of cards to shuffle
     * @return shuffled list
     */
    public static List<MyCard> shuffleList(Random rd, int shuffles, List<MyCard> cards) {
        for (int i = 0; i < shuffles; i++) {
            MyCard card = cards.remove(rd.nextInt(cards.size()));
            cards.add(rd.nextInt(cards.size()), card);
        }
        if (GameRules.print || GameRules.minPrint)  System.out.println("Deck shuffled");
        return cards;
    }

    /**
     * Distributes the cards to all players
     *
     * @param cardsPerHand amount of cards in every player's hand
     * @param curState     current game state
     */
    public static void distributeCards(int cardsPerHand, State curState) {
        for (int i = 0; i < curState.playerStates.size(); i++) {
            for (int j = 0; j < cardsPerHand; j++) {
                curState.playerStates.get(i).handLayout.addUnusedCard(curState.pickDeckTop());
            }
            curState.players.get(i).update(curState.playerStates.get(i).viewHandLayout());
            curState.players.get(i).newRound(curState.peekDiscardTop());
            if (GameRules.print || GameRules.minPrint) System.out.println("Player "+i+" cards distributed");
        }
    }

    /**
     * Adds the top of the deck to the discard pile
     *
     * @param curState current game state
     */
    public static void startDiscardPile(State curState) {
        if (curState.discardPile.isEmpty()) {
            curState.addToDiscard(curState.pickDeckTop());
        }
        if (GameRules.print || GameRules.minPrint)  System.out.println("Discard pile created");
    }

    /* STATE UPDATING */

    /**
     * @param curState current game state
     * @param deltaT   time elapsed between now and previous render call
     * @return true if there is no more time for the current step
     */
    public static boolean update(State curState, float deltaT) {
        curState.curTime -= deltaT * GameRules.gameSpeed;
        return curState.curTime <= 0;
    }

    /**
     * Goes to the next step of the game (knock, pick, discard, meld creation, laying off, point assignment, next player).
     *
     * @param curState current game state
     */
    public static void nextStep(State curState) {

        curState.getPlayer().update(curState.getPlayerState().viewHandLayout());
        if (GameRules.print) System.out.println(curState.viewLastAction());

        Action a = curState.viewLastAction();
        for (int i = 0; i < curState.players.size(); i++) {
            if (i != curState.playerTurn) {
                if (a instanceof PickAction && ((PickAction) a).deck) {
                    curState.players.get(i).playerActed(new PickAction(a.playerIndex, ((PickAction) a).deck, null));
                } else {
                    curState.players.get(i).playerActed(a);
                }
            }
        }

        if (curState.stepInTurn == State.StepInTurn.LayoutConfirmation || curState.stepInTurn == State.StepInTurn.LayOff) {
            getNextPlayer(curState);
            if (curState.getKnockerNumber().equals(curState.getPlayerNumber())) {
                curState.stepInTurn = curState.stepInTurn.getNext();
            }
        } else {
            if (curState.stepInTurn == State.StepInTurn.KnockOrContinue) {
                getNextPlayer(curState);
            }
            curState.stepInTurn = curState.stepInTurn.getNext();
        }
        curState.curTime = curState.secondsPerStep[curState.getStep().index];

        if (GameRules.print || GameRules.minPrint)
            System.out.println("New step: Player " + curState.getPlayerNumber() + " " + curState.stepInTurn);

    }

    /**
     * Updates current player turn and counts which turn it is in the current round
     *
     * @param curState current game state
     */
    private static void getNextPlayer(State curState) {
        curState.playerTurn = (curState.playerTurn + 1) % curState.numberOfPlayers;
        if (curState.playerTurn == curState.startingPlayer) {
            curState.turnInRound++;
        }
    }

    /**
     * Starts the knocking process (meld creation then laying off (if no gin) and then point counting).
     *
     * @param knocker  index of the player that knocked
     * @param curState current game state
     */
    private static void knocked(int knocker, State curState) {
        if (GameRules.print)
            System.out.println("Player " + curState.getPlayerNumber() + " knocked with:\n" + curState.getPlayer().viewHandLayout() + "\n");

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
            if (GameRules.print) System.out.println("End round loop. Current step: " + curState.stepInTurn);
            nextStep(curState);
        }
    }

    /**
     * Assigns points won to winner.
     *
     * @param curState current game state
     */
    public static void assignPoints(State curState) {
        curState.setWinnerByIndex(getWinner(curState));
        if (curState.roundWinnerIndex == null) {
            return;
        }
        List<HandLayout> handLayouts = new ArrayList<>();
        for (PlayerState player : curState.playerStates) {
            handLayouts.add(player.viewHandLayout());
        }
        int pointsWon = Finder.getPointsToAdd(handLayouts, curState.players.get(curState.roundWinnerIndex).viewHandLayout().getDeadwood());

        if (curState.roundWinnerID.equals(curState.knocker)) {
            if (curState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                if (GameRules.print) System.out.println("Gin");
                pointsWon += GameRules.ginBonus;
            } else {
                if (GameRules.print) System.out.println("Knock");
            }
        } else {
            if (GameRules.print) System.out.println("Undercut");
            pointsWon += GameRules.undercutBonus;
        }
        curState.scores[curState.roundWinnerIndex] += pointsWon;
    }

    /**
     * Gets index of winner of the round.
     *
     * @param curState current game state
     * @return index of winner
     */
    private static Integer getWinner(State curState) {
        List<HandLayout> handLayouts = new ArrayList<>();
        for (PlayerState player : curState.playerStates) {
            handLayouts.add(player.viewHandLayout());
        }
        if (curState.knocker == null) {
            return null;
        }
        return Finder.findLowestDeadwoodIndex(handLayouts, handLayouts.get(curState.getKnockerIndex()).getDeadwood(), curState.getKnockerIndex());
    }

    // TURN HANDLING
    public static boolean execute(Action action, State curState) {
        if (action == null || curState.endGame) {
            return false;
        }
        List<? extends Action> possibleActions = TreeExpander.getPossibleActions(curState);
        for (Action possibleAction : possibleActions) {
            if (action.same(possibleAction)) {
                Action executed = null;
                if (action instanceof KnockAction) {
                    executed = knock((KnockAction) action, curState);
                } else if (action instanceof PickAction) {
                    executed = pick((PickAction) action, curState);
                } else if (action instanceof DiscardAction) {
                    executed = discard((DiscardAction) action, curState);
                } else if (action instanceof LayoutConfirmationAction) {
                    executed = layoutConfirmation((LayoutConfirmationAction) action, curState);
                } else if (action instanceof LayoffAction) {
                    executed = layoff((LayoffAction) action, curState);
                } else {
                    System.out.println("Executor.execute() ERROR ERROR ERROR");
                }
                if (executed != null) {
                    if (GameRules.print) System.out.println("Action saved");
                    curState.movesDone.add(action);
                    curState.getPlayer().executed(executed);
                    if(action instanceof KnockAction && ((KnockAction) action).knock){
                        knocked(action.playerIndex, curState);
                    }else {
                        Executor.nextStep(curState);
                    }
                }
                return executed != null;
            }
        }
        if (GameRules.print || GameRules.minPrint) System.out.println("ACTION NOT AVAILABLE " + action);

        return false;
    }

    private static KnockAction knock(KnockAction action, State curState) {
        if (action.knock && action.viewLayout().getDeadwood() <= GameRules.minDeadwoodToKnock) {
            return action;
        } else if (!action.knock) {
            return action;
        }
        System.out.println("Executor.knock() ERROR ERROR ERROR");
        return null;
    }

    private static PickAction pick(PickAction action, State curState) {
        if (action.deck && curState.getDeckSize() != 0) {
            curState.getPlayerState().handLayout.addUnusedCard(curState.pickDeckTop());
            return new PickAction(action.playerIndex, true, curState.peekDeckTop());
        } else if (!action.deck && !curState.isDiscardEmpty() && action.card.same(curState.peekDiscardTop())) {
            curState.getPlayerState().handLayout.addUnusedCard(curState.pickDiscardTop());
            return action;
        }
        System.out.println("Executor.pick() ERROR ERROR ERROR");
        return null;
    }

    private static DiscardAction discard(DiscardAction action, State curState) {
        for (MyCard card : curState.getPlayerState().viewHand()) {
            if (action.card.same(card) && curState.getPlayerState().removeCard(action.card)) {
                curState.addToDiscard(action.card);
                return action;
            }
        }
        System.out.println("Executor.discard() ERROR ERROR ERROR");
        return null;
    }

    private static LayoutConfirmationAction layoutConfirmation(LayoutConfirmationAction action, State curState) {
        if (action.layout.isValid()) {
            int found = 0;
            for (MyCard card : action.layout.viewAllCards()) {
                for (MyCard myCard : curState.getPlayerState().viewHand()) {
                    if (card.same(myCard)) {
                        found++;
                        break;
                    }
                }
            }
            if (found == curState.getPlayerState().viewHand().size()) {
                curState.getPlayerState().handLayout = action.layout;
                return action;
            }
        }else{
            System.out.println("NOT VALID");
        }
        System.out.println("Executor.layoutConfirmation() ERROR ERROR ERROR");
        return null;
    }

    private static Action layoff(LayoffAction action, State curState) {
        if (action.meld == null) {
            return action;
        }
        boolean foundInUnused = false;
        for (MyCard card : curState.getPlayerState().viewUnusedCards()) {
            if (card.same(action.card)) {
                foundInUnused = true;
                break;
            }
        }
        if (foundInUnused) {
            for (Meld meld : curState.getKnockerState().viewMelds()) {
                if (meld.same(action.meld) && meld.isValidWith(action.card)) {
                    meld.addCard(action.card);
                    return action;
                }
            }
        }
        System.out.println("Executor.layoff() ERROR ERROR ERROR");
        return null;

    }
}