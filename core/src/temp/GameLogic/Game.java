package temp.GameLogic;

import com.badlogic.gdx.Gdx;
import temp.Extra.PostGameInformation.Result;
import temp.GameLogic.Entities.*;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.GameState;
import temp.GameLogic.States.RoundState;
import temp.GamePlayers.CombinePlayer;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GameRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Feed through the players and either a seed, an initial round
public class Game {

    private final static List<Game> currentGames = new ArrayList<>();
    private final GameState gameState;
    private final List<GamePlayer> players;
    public int playTillRound=Integer.MAX_VALUE;
    private boolean newStep = true;
    private float time = 0;
    private float[] timeAllotted;

    public Game(List<GamePlayer> players, GameState gameState) {
        this.gameState = gameState;
        this.players = players;
        timeAllotted = new float[Step.values().length];
        Arrays.fill(timeAllotted, GameRules.DeckOrDiscardPileTime);
        if(gameState.getRoundNumber()==0) {
            startNewRound();
        }
        currentGames.add(this);
    }
    public Game(List<GamePlayer> players, Integer seed){
        this(players, new GameState(players.size(), MyCard.getBasicDeck(), seed));
    }
    public Game(GamePlayer[] players, Integer seed){
        this(Arrays.asList(players), seed);
    }
    public Game(List<GamePlayer> players, RoundState initRound, Integer seed){
        this(players, new GameState(initRound, seed));

    }

    // Game playing methods

    /**
     * Runs the game including time limit for turns
     * @param deltaT time elapsed between now and previous call
     * @return action executed.
     * Returns EndSignal.EndOfGame = false if round ended, and EndSignal.EndOfGame = true if game ended
     */
    public Action update(float deltaT){
        time+= (deltaT*GameRules.gameSpeed);
        boolean outOfTime = outOfTime();
        Turn curTurn = getTurn();
        if(outOfTime){
            players.set(getPlayerIndex(),new ForcePlayer(players.get(curTurn.playerIndex)));
        }
        Action a = continueGame();
        if(outOfTime){
            players.set(getPlayerIndex(),((ForcePlayer)players.get(curTurn.playerIndex)).player);
            time = 0;
        }
        return a;
    }
    /**
     * Runs the game from a multiple rounds perspective. Calls playTurn and creates a new round when needed
     * @return action executed.
     * Returns EndSignal.EndOfGame = false if round ended, and EndSignal.EndOfGame = true if game ended
     */
    public Action continueGame(){
        Action a = continueRound();
        if(a instanceof EndSignal){
            if(!gameState.locked()) {
                gameState.addPoints(getRound().points());
                if(gameState.gameEnded()){
                    gameState.lock();
                }
            }
            if(gameStopCondition(gameState) || playTillRound<gameState.getRoundNumber()){
                return new EndSignal(true);
            }
            startNewRound();
        }
        return a;
    }
    /**
     * Runs the game from a single round's perspective. Once the round has ended, nothing happens anymore
     * @return action executed.
     * Returns EndSignal.EndOfGame = false if round ended
     */
    public Action continueRound(){
        if(newStep){
            oncePerStep(getRound(), players);
        }
        Action action = getPlayerAction(players.get(getPlayerIndex()),getTurn(), gameState.round());
        boolean executed = action != null && action.doAction(gameState.round(), true);
        if(executed){
            newStep = true;
            if(roundStopCondition(getRound())){
                if(!getRound().locked()){
                    getRound().setLayouts();
                    getRound().setPoints(getPointsWon(getRound()));
                    getRound().lock();
                }
                return new EndSignal(false);
            }
        }
        return executed? action : null;
    }
    /**
     * Runs current round out and returns the results
     * @return returns the results of the played out round
     */
    public Result playOutRound(){
        while(true){
            Action a = continueRound();
            if(a instanceof EndSignal){
                break;
            }
        }
        return new Result(getRound());
    }
    /**
     * Runs game from current state to finish (point or round limit), saving the results of every round
     * @return results of every round
     */
    public GameState playOutGame(){
        while(true){
            Action a = continueGame();
            if(a instanceof EndSignal){
                if(((EndSignal) a).endOfGame) {
                    break;
                }
            }
        }
        return gameState;
    }
    /**
     * Undoes the last action of the last round of this game.
     * @return action that has been undone
     */
    public Action undoLastAction(){
        return getRound().undoLastAction();
    }

    // Setters

    private void startNewRound(){
        gameState.createNewRound();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).update(new ArrayList<>(gameState.round().getCards(i)));
            players.get(i).newRound(gameState.round().peekDiscard());
        }
    }
    private void oncePerStep(RoundState state, List<GamePlayer> players){
        notifyPlayers(state.getLastAction(), players);
        players.get(state.getPlayerIndex()).update(new ArrayList<>(state.getCards(state.getPlayerIndex())));
        if(players.get(state.getPlayerIndex()) instanceof CombinePlayer || players.get(state.getPlayerIndex()).getProcessor()!=null) {
            Gdx.input.setInputProcessor(players.get(state.getPlayerIndex()).getProcessor());
        }
    }
    private void notifyPlayers(Action a, List<GamePlayer> players){
        if(a!=null) {
            players.get(a.playerIndex).executed(a);
            for (int i = 0; i < players.size(); i++) {
                if (i != a.playerIndex) {
                    if (a instanceof PickAction && ((PickAction) a).deck) {
                        players.get(i).playerActed(new PickAction(a.playerIndex, ((PickAction) a).deck, null));
                    } else {
                        players.get(i).playerActed(a);
                    }
                }
            }
        }
    }

    // Getters

    private boolean outOfTime(){
        return time >= timeAllotted[getStep().index];
    }
    public MyCard peekDiscard(){
        return getRound().peekDiscard();
    }
    public int deckSize(){
        return getRound().deckSize();
    }
    public int getTurnNumber(){
        return gameState.getTurn();
    }
    public int getRoundNumber(){
        return gameState.getRoundNumber();
    }
    public int[] getPoints(){
        return gameState.getPoints();
    }
    public RoundState getRound(){
        return gameState.round();
    }
    public boolean gameEnded(){
        return gameState.gameEnded();
    }
    public Integer getWinner(){
        if(gameEnded()){
            return gameState.getHighestScoreIndex();
        }else{
            return null;
        }
    }
    public Turn getTurn(){
        return gameState.round().turn();
    }
    public GamePlayer getGamePlayer(){
        return players.get(getPlayerIndex());
    }
    public Step getStep(){
        return gameState.round().turn().step;
    }
    public float getCurTime(){
        return time;
    }
    public int getPlayerIndex(){
        return gameState.round().turn().playerIndex;
    }
    private List<MyCard> getPlayerCards(){
        return gameState.round().getCards(getPlayerIndex());
    }
    private Action getLastAction(){
        return gameState.round().actions.size()==0? null : gameState.round().actions.peek();
    }

    // Static Other

    public static boolean roundStopCondition(RoundState roundState){
        return roundState.turn().step == Step.EndOfRound
                || roundState.deckSize()<=GameRules.minCardsInDeck
                || roundState.turnsPlayed()>= GameRules.maxTurnsInARound;
    }
    public static boolean gameStopCondition(GameState gameState){
        int[] points = gameState.getPoints();
        for (int point : points) {
                if(point>= GameRules.pointsToWin){
                    return true;
                }
        }
        return false;
    }
    public static int[] getPointsWon(RoundState state){
        int[] points = new int[state.numberOfPlayers()];
        if(state.knocker()==null){
            return points;
        }
        int winningPlayerIndex = Finder.findLowestDeadwoodIndex(Arrays.asList(state.layouts()), state.layouts()[state.knocker()].getDeadwood(), state.knocker());
        int deadwoodDifferences = Finder.getPointsToAdd(Arrays.asList(state.layouts()), state.layouts()[winningPlayerIndex].getDeadwood());
        int bonus = Finder.getBonusPoints(state.knocker(), winningPlayerIndex, state.layouts()[state.knocker()].getDeadwood());
        points[winningPlayerIndex] = deadwoodDifferences+bonus;
        return points;
    }
    public static void shuffleList(Random rd, int shuffles, List<MyCard> list){
        for (int i = 0; i < shuffles; i++) {
            MyCard c =list.remove(rd.nextInt(list.size()));
            list.add(c);
        }
    }

    // Static player information

    private static Integer[] getIndices(GamePlayer p){
        Integer gameIndex= null;
        Integer playerIndex = null;
        for (int game = 0; game < currentGames.size(); game++) {
            for (int player = 0; player < currentGames.get(game).players.size(); player++) {
                if(currentGames.get(game).players.get(player) == p){
                    gameIndex = game;
                    playerIndex = player;
                    break;
                }
            }
            if(gameIndex!=null){
                break;
            }
        }
        return new Integer[]{gameIndex, playerIndex};
    }
    /**
     * Returns all points in the game currently
     * @param p asking player
     * @return index 0 = asker, 1 = next, etc.. for every player
     */
    public static int[] getPoints(GamePlayer p){
        Integer[] indices = getIndices(p);
        int[] points = currentGames.get(indices[0]).getPoints();
        int[] pointsUpdate = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            pointsUpdate[i] = points[(indices[1]+i)%points.length];
        }
        return pointsUpdate;
    }
    /**
     * Returns the player's game
     * @param p asking player
     * @return current player cards
     */
    public static List<MyCard> getCards(GamePlayer p){
        Integer[] indices = getIndices(p);
        return new ArrayList<>(currentGames.get(indices[0]).getRound().getCards(indices[1]));
    }

    // Action getters

    private Action getPlayerAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state) {
        switch(currentTurn.step){
            case Pick: return pickAction(currentPlayer, currentTurn, state);
            case Discard: return discardAction(currentPlayer, currentTurn, state);
            case KnockOrContinue: return knockAction(currentPlayer, currentTurn, state);
            case LayoutConfirmation: return layoutConfirmationAction(currentPlayer, currentTurn, state);
            case Layoff: return layoffAction(currentPlayer, currentTurn, state);
            default: return null;
        }
    }
    private PickAction pickAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state){
        Boolean deck = currentPlayer.pickDeckOrDiscard(state.deckSize(), state.peekDiscard());
        return deck==null? null : new PickAction(currentTurn.playerIndex, deck, deck ? state.peekDeck() : state.peekDiscard());
    }
    private DiscardAction discardAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state){
        MyCard cardToDiscard = currentPlayer.discardCard();
        return cardToDiscard==null? null : new DiscardAction(currentTurn.playerIndex, cardToDiscard);
    }
    private KnockAction knockAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state){
        Boolean knock = currentPlayer.knockOrContinue();
        return knock==null? null : new KnockAction(currentTurn.playerIndex, knock, null);
    }
    private LayoutConfirmationAction layoutConfirmationAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state){
        HandLayout layout = currentPlayer.confirmLayout();
        return layout==null? null : new LayoutConfirmationAction(currentTurn.playerIndex, layout);
    }
    private LayoffAction layoffAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state){
        List<Layoff> layoffs = currentPlayer.layOff(new HandLayout(state.getCards(state.knocker())).viewMelds());
        return new LayoffAction(currentTurn.playerIndex, layoffs);
    }
}