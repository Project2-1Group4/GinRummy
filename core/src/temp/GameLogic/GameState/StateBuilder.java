package temp.GameLogic.GameState;

import temp.GameActors.GameActor;
import temp.GameActors.KeyboardPlayer;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.MyCard;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Builder class
 */
public class StateBuilder {
    /**
     * Statebuilder.build() returns normal 2 player game. No AI. From starting space
     */
    private List<MyCard> deck;
    private Stack<MyCard> discardPile;
    private int numberOfPlayers;
    private List<GameActor> actors;
    private List<ActorState> actorStates;
    private int playerTurn;
    private State.StepInTurn stepInTurn;
    private int[] scores;
    private float[] secondsPerStep;
    private Integer knocker;
    private int round;
    private int turnInRound;
    private Stack<Action> actions;

    public StateBuilder() {
        deck = MyCard.getBasicDeck();
        numberOfPlayers = 0;
        playerTurn = 0;
        round = 0;
        turnInRound = 0;
        stepInTurn = State.StepInTurn.KnockOrContinue;
        knocker = null;
        actions = new Stack<>();
        actors = new ArrayList<>();
        actorStates = new ArrayList<>();
        discardPile = new Stack<>();
        secondsPerStep = new float[State.StepInTurn.values().length];
        secondsPerStep[0] = GameRules.knockOrContinueTime;
        secondsPerStep[1] = GameRules.DeckOrDiscardPileTime;
        secondsPerStep[2] = GameRules.DiscardTime;
        secondsPerStep[3] = GameRules.MeldConfirmationTime;
        secondsPerStep[4] = GameRules.LayOffTime;
    }

    public StateBuilder useCustomDeck(List<MyCard> deck) {
        this.deck = deck;
        return this;
    }

    public StateBuilder setNumberOfPlayers(int nb) {
        this.numberOfPlayers = nb;
        return this;
    }

    public StateBuilder setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
        return this;
    }

    public StateBuilder setStepInTurn(State.StepInTurn step) {
        this.stepInTurn = step;
        return this;
    }

    public StateBuilder addActor(GameActor actor, ActorState state) {
        actors.add(actor);
        actorStates.add(state);
        numberOfPlayers++;
        return this;
    }

    public StateBuilder addActor(GameActor actor){
        return addActor(actor, new ActorState());
    }

    public StateBuilder addActors(List<GameActor> actors, List<ActorState> states){
        for (int i = 0; i < actors.size(); i++) {
            addActor(actors.get(i),states.get(i));
        }
        return this;
    }

    public StateBuilder addActors(List<GameActor> actors){
        for (GameActor actor : actors) {
            addActor(actor);
        }
        return this;
    }

    public StateBuilder setScores(int[] scores) {
        this.scores = scores;
        return this;
    }

    public StateBuilder setSecondsPerStep(float[] seconds) {
        this.secondsPerStep = seconds;
        return this;
    }

    public StateBuilder setRound(int round){
        this.round = round;
        return this;
    }

    public StateBuilder setTurnInRound(int turnInRound){
        this.turnInRound = turnInRound;
        return this;
    }

    public StateBuilder setKnocker(int knocker){
        this.knocker = knocker;
        return this;
    }

    public StateBuilder setActions(Stack<Action> actions){
        this.actions = actions;
        return this;
    }
    public State build() {
        if(numberOfPlayers==0){
            numberOfPlayers = 2;
        }

        assert deck != null;
        assert discardPile != null;
        assert actors.size() < numberOfPlayers;
        assert actors.size() == actorStates.size();
        assert playerTurn < numberOfPlayers;
        assert stepInTurn != null;
        assert scores.length == numberOfPlayers;
        assert secondsPerStep.length == 3;
        assert knocker > numberOfPlayers;

        int actorsToAdd = numberOfPlayers - actors.size();
        for (int i = 0; i < actorsToAdd; i++) {
            actors.add(new KeyboardPlayer());
            actorStates.add(new ActorState());
        }
        if(scores==null){
            scores = new int[numberOfPlayers];
        }
        return new State(deck, discardPile, actors, actorStates,numberOfPlayers, playerTurn, stepInTurn, scores, secondsPerStep, knocker,round,turnInRound,actions);
    }
}
