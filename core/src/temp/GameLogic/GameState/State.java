package temp.GameLogic.GameState;

import temp.GameActors.GameActor;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.MyCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Saves all game information. Needs to be simplify to make saving/loading easy
 */
public class State {
    protected List<MyCard> deck;
    protected Stack<MyCard> discardPile;
    protected int numberOfPlayers;
    protected List<GameActor> actors;
    protected List<ActorState> actorStates;
    protected int playerTurn;
    protected StepInTurn stepInTurn;
    protected int[] scores;
    protected float[] secondsPerStep;
    protected float curTime;
    protected Integer knocker;
    protected int round;
    protected int turnInRound;
    protected Stack<Action> movesDone;

    protected State(List<MyCard> deck, Stack<MyCard> discardPile, List<GameActor> actors, List<ActorState> actorStates,
                    int numberOfPlayers, int playerTurn, StepInTurn stepInTurn, int[] scores, float[] secondsPerStep, Integer knocker, int round, int turnInRound, Stack<Action> movesDone) {
        this.deck = deck;
        this.discardPile = discardPile;
        this.actors = actors;
        this.actorStates = actorStates;
        this.numberOfPlayers = numberOfPlayers;
        this.playerTurn = playerTurn;
        this.stepInTurn = stepInTurn;
        this.scores = scores;
        this.secondsPerStep = secondsPerStep;
        this.knocker = knocker;
        this.round = round;
        this.turnInRound = turnInRound;
        this.movesDone = movesDone;
        curTime = secondsPerStep[stepInTurn.index];

    }

    /*
    GETTERS
    */
    public int getActorNumber() {
        return playerTurn;
    }

    public GameActor getActor() {
        return actors.get(playerTurn);
    }

    public ActorState getActorState(){
        return actorStates.get(playerTurn);
    }

    public int getKnockerNumber(){
        return knocker;
    }

    public GameActor getKnocker() {
        assert knocker != null;
        return actors.get(knocker);
    }

    public ActorState getKnockerState(){
        assert knocker != null;
        return actorStates.get(knocker);
    }

    public StepInTurn getStep() {
        return stepInTurn;
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    public boolean isDiscardEmpty() {
        return discardPile.isEmpty();
    }

    public MyCard peekDiscardTop() {
        if (discardPile.size() == 0) {
            return null;
        }
        return discardPile.peek().clone();
    }

    public float getCurTime() {
        return curTime;
    }

    public int[] getScores() {
        return scores;
    }

    public int getRound(){
        return round;
    }

    public int getTurn(){
        return turnInRound;
    }

    public Stack<Action> viewActions(){
        return (Stack<Action>) movesDone.clone();
    }

    public Action viewLastAction(){
        return movesDone.peek();
    }

    /*
    SETTERS. ONLY ACCESSIBLE BY PACKAGE (Executor mainly)
     */

    protected MyCard pickDeckTop() {
        return deck.remove(deck.size() - 1);
    }

    protected MyCard pickDiscardTop() {
        return discardPile.pop();
    }

    protected void addToDiscard(MyCard card) {
        discardPile.add(card);
    }

    /*
    HELPER
     */

    public enum StepInTurn {
        KnockOrContinue("Knock or not?", 0),
        Pick("Deck or Discard pile?", 1),
        Discard("Pick a card to discard", 2),
        MeldConfirmation("Confirm your melds", 3),
        LayOff("Layoff your deadwood cards in knocker melds", 4),
        EndOfRound("Round End", 5);
        public String question;
        public int index;

        StepInTurn(String n, int index) {
            this.question = n;
            this.index = index;
        }

        public StepInTurn getNext() {
            if (this == KnockOrContinue) {
                return Pick;
            } else if (this == Pick) {
                return Discard;
            } else if (this == Discard) {
                return KnockOrContinue;
            } else if (this == MeldConfirmation) {
                return LayOff;
            } else if (this == LayOff) {
                return EndOfRound;
            }
            System.out.println("STEP ERROR??");
            return null;
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Deck size: ").append(deck.size())
                .append("\nDiscard size: ").append(discardPile.size())
                .append("\nPlayers: ").append(numberOfPlayers).append(" ").append(actors.size());
        return sb.toString();

    }
}