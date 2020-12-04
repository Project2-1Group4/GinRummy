package temp.GameLogic.GameState;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Saves all game information. Needs to be simplify to make saving/loading easy
 */
public class State {
    public boolean endGame;

    protected Random seed;
    protected List<MyCard> initDeck;
    protected List<MyCard> deck;
    protected Stack<MyCard> discardPile;
    protected int numberOfPlayers;
    protected List<GamePlayer> players;
    protected List<PlayerState> playerStates;
    protected int playerTurn;
    protected StepInTurn stepInTurn;
    protected int[] scores;
    protected float[] secondsPerStep;
    protected float curTime;
    protected Integer knocker;
    protected Integer roundWinnerID;
    protected Integer roundWinnerIndex;
    protected Integer gameWinnerIndex;
    protected int round;
    protected int turnInRound;
    protected Stack<Action> movesDone;

    protected State(Random seed, List<MyCard> deck, Stack<MyCard> discardPile, List<GamePlayer> players, List<PlayerState> playerStates,
                    int numberOfPlayers, int playerTurn, StepInTurn stepInTurn, int[] scores, float[] secondsPerStep, Integer knocker, int round, int turnInRound, Stack<Action> movesDone) {
        this.seed = seed;
        this.initDeck = new ArrayList<>(deck);
        this.deck = deck;
        this.discardPile = discardPile;
        this.players = players;
        this.playerStates = playerStates;
        this.numberOfPlayers = numberOfPlayers;
        this.playerTurn = playerTurn;
        this.stepInTurn = stepInTurn;
        this.scores = scores;
        this.secondsPerStep = secondsPerStep;
        this.knocker = knocker;
        this.round = round;
        this.turnInRound = turnInRound;
        this.movesDone = movesDone;
        gameWinnerIndex = null;
        endGame = false;
        curTime = secondsPerStep[stepInTurn.index];
    }

    // SETTERS
    // Quality of life methods
    protected MyCard pickDeckTop() {
        return deck.remove(deck.size() - 1);
    }

    protected MyCard pickDiscardTop() {
        return discardPile.pop();
    }

    protected void addToDiscard(MyCard card) {
        discardPile.add(card);
    }

    public void setWinnerByIndex(Integer index){
        if(index!=null){
            roundWinnerID = players.get(index).index;
            roundWinnerIndex = index;
            System.out.println(roundWinnerIndex+" ID "+roundWinnerID);
        }
    }
    // GETTERS

    public boolean endOfGame(){
        return gameWinnerIndex!=null;
    }

    public Integer getWinnerIndex(){
        return gameWinnerIndex;
    }

    public Integer getWinnerID(){
        return players.get(gameWinnerIndex).index;
    }

    public List<PlayerState> getPlayerStates() {
        return playerStates;
    }

    public List<MyCard> getDeck(){
        return new ArrayList<>(deck);
    }
    // Returns copies to avoid the changing of the inner state outside of package
    public int getPlayerNumber() {
        return getPlayer().index;
    }

    public int getPlayerIndex(){
        return playerTurn;
    }

    public GamePlayer getPlayer() {
        return players.get(playerTurn);
    }

    public PlayerState getPlayerState() {
        return playerStates.get(playerTurn);
    }

    public Integer getKnockerNumber() {
        return knocker;
    }

    public Integer getKnockerIndex() {
        Integer index = null;
        if (knocker != null) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).index == knocker) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public GamePlayer getKnocker() {
        assert knocker != null;
        return players.get(getKnockerIndex());
    }

    public PlayerState getKnockerState() {
        assert knocker != null;
        return playerStates.get(getKnockerIndex());
    }

    public StepInTurn getStep() {
        return stepInTurn;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public boolean isDiscardEmpty() {
        return discardPile.isEmpty();
    }

    public MyCard peekDiscardTop() {
        if (discardPile.size() == 0) {
            return null;
        }
        return discardPile.peek();
    }

    public MyCard peekDeckTop() {
        return deck.get(deck.size() - 1);
    }

    public float getCurTime() {
        return curTime;
    }

    public int[] getScores() {
        return scores;
    }

    public int getRound() {
        return round;
    }

    public int getTurn() {
        return turnInRound;
    }

    public int getRoundTurn() {
        return turnInRound;
    }

    public Stack<Action> viewActions() {
        return (Stack<Action>) movesDone.clone();
    }

    public Action viewLastAction() {
        if (movesDone.size() != 0) {
            return movesDone.peek();
        }
        return null;
    }

    public String toString() {
        return "Deck size: " + deck.size() +
                "\nDiscard size: " + discardPile.size() +
                "\nPlayers: " + numberOfPlayers;

    }

    public String deepToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Deck: ").append(deck.size()).append(" cards\n").append(deck).append("\n");
        sb.append("\nDiscard: ").append(discardPile.size()).append(" cards\n").append(discardPile).append("\n");
        for (int i = 0; i < players.size(); i++) {
            sb.append("\nPlayer ").append(i).append(": ")
                    .append("\nReal state: ").append(playerStates.get(i))
                    .append("\nInner state: ").append(players.get(i));
        }
        sb.append("\nMoves done:\n");
        for (Action action : movesDone) {
            sb.append(action).append(", ");
        }
        return sb.toString();
    }

    // EXTRA
    public enum StepInTurn {
        KnockOrContinue("Knock or not?", 2),
        Pick("Deck or Discard pile?", 0),
        Discard("Pick a card to discard", 1),
        LayoutConfirmation("Confirm your melds", 3),
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
            } else if (this == LayoutConfirmation) {
                return LayOff;
            } else if (this == LayOff) {
                return EndOfRound;
            }
            System.out.println("STEP ERROR??");
            return null;
        }

    }

    public State copy() {
        List<PlayerState> pStates = new ArrayList<>();
        for (PlayerState playerState : playerStates) {
            pStates.add(playerState.copy());
        }
        return new State(seed, new ArrayList<MyCard>(deck), (Stack<MyCard>) discardPile.clone(),
                new ArrayList<GamePlayer>(players), pStates,
                numberOfPlayers, playerTurn, stepInTurn, scores.clone(), secondsPerStep.clone(), knocker, round,
                turnInRound, (Stack<Action>) movesDone.clone());
    }
}
