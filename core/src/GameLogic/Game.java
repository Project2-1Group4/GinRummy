package GameLogic;

import Extra.PostGameInformation.Result;
import GameLogic.Entities.*;
import GameLogic.GameActions.*;
import GameLogic.Logic.Finder;
import GameLogic.States.GameState;
import GameLogic.States.RoundState;
import GamePlayers.CombinePlayer;
import GamePlayers.ForcePlayer;
import GamePlayers.GamePlayer;
import com.badlogic.gdx.Gdx;
import temp.GameRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    private final static List<Game> currentGames = new ArrayList<>();
    private final List<GamePlayer> players;
    private float time = 0;
    private boolean newStep = true;
    public final GameState gameState;

    public float[] timeAllotted;
    public int playTillRound=Integer.MAX_VALUE;
    public boolean printTurns = false;
    public boolean printRounds = false;
    public boolean printGame = false;

    public Game(List<GamePlayer> players, GameState gameState) {
        currentGames.add(this);
        this.gameState = gameState;
        this.players = players;
        timeAllotted = new float[Step.values().length];
        Arrays.fill(timeAllotted, GameRules.DeckOrDiscardPileTime);
        init();
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
        Turn curTurn = turn();
        if(outOfTime){
            players.set(curPlayerIndex(),new ForcePlayer(players.get(curTurn.playerIndex), null));
        }
        Action a = continueGame();
        if(a!=null || outOfTime){
            time=0;
            if(outOfTime){
                players.set(curPlayerIndex(),((ForcePlayer)players.get(curTurn.playerIndex)).player);
            }
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
            if(gameStopCondition(gameState) || playTillRound<gameState.getRoundNumber()){
                if(!gameState.locked()) {
                    gameState.lock();
                    if (printGame) {
                        System.out.println("Game locked with points: " + Arrays.toString(gameState.getPoints())+"\n");
                    }
                }
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
            oncePerStep(round(), players);
            newStep = false;
        }
        Action action = getPlayerAction(players.get(curPlayerIndex()), turn(), gameState.round());
        boolean executed = action != null && action.doAction(gameState.round(), true);
        if(executed){
            if(printTurns){
                System.out.println("Turn "+ turnNumber()+" Action: "+action);
            }
            newStep = true;
            if(action instanceof EndSignal || roundStopCondition(round())){
                if(!round().locked()){
                    round().setLayouts();
                    round().setPoints(pointsWon(round()));
                    gameState.addPoints(round().points());
                    round().lock();
                    if(printRounds){
                        System.out.println("Round "+ roundNumber()+" locked with points: "+ Arrays.toString(round().points()));
                        for (int i = 0; i < round().layouts().length; i++) {
                            System.out.println("Player "+i+"\n"+ round().layouts()[i]);
                            System.out.println("Value: "+ round().layouts()[i].evaluate());
                        }
                        System.out.println("Current game points: "+ Arrays.toString(points())+"\n");
                    }
                }
                round().turn(new Turn(Step.EndOfRound, curPlayerIndex()));
            }
        }
        return executed? action : null;
    }
    /**
     * Runs current round out and returns the results
     * @return returns the results of the played out round
     */
    public Result playOutRound(){
        updateAllPlayers();
        while(true){
            Action a = continueRound();
            if(a instanceof EndSignal){
                break;
            }
        }
        return new Result(round());
    }
    /**
     * Runs game from current state to finish (point or round limit), saving the results of every round
     * @return results of every round
     */
    public GameState playOutGame(){
        updateAllPlayers();
        while(true){
            Action a = continueGame();
            if(a instanceof EndSignal){
                if(((EndSignal) a).endOfGame) {
                    break;
                }
            }
        }
        remove();
        return gameState;
    }
    /**
     * Undoes the last action of the last round of this game.
     * @return action that has been undone
     */
    public Action undoLastAction(){
        return round().undoLastAction();
    }

    // Game <=> Player interaction

    //Player information setters
    private void startNewRound(){
        gameState.createNewRound();
        if(printRounds){
            printRound();
        }
        updateAllPlayers();
    }
    private void updateAllPlayers(){
        for (int i = 0; i < players.size(); i++) {
            players.get(i).index = i;
            players.get(i).update(new ArrayList<>(gameState.round().cards(i)));
            players.get(i).newRound(gameState.round().peekDiscard());
        }
    }
    private void oncePerStep(RoundState state, List<GamePlayer> players){
        notifyPlayers(state.lastAction(), players);
        players.get(state.curIndex()).update(new ArrayList<>(state.cards(state.curIndex())));
        if(players.get(state.curIndex()) instanceof CombinePlayer || players.get(state.curIndex()).getProcessor()!=null) {
            Gdx.input.setInputProcessor(players.get(state.curIndex()).getProcessor());
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
    //Player action getters
    private Action getPlayerAction(GamePlayer currentPlayer, Turn currentTurn, RoundState state) {
        switch(currentTurn.step){
            case Pick: return pickAction(currentPlayer, currentTurn, state);
            case Discard: return discardAction(currentPlayer, currentTurn, state);
            case KnockOrContinue: return knockAction(currentPlayer, currentTurn, state);
            case LayoutConfirmation: return layoutConfirmationAction(currentPlayer, currentTurn, state);
            case Layoff: return layoffAction(currentPlayer, currentTurn, state);
            case EndOfRound: return new EndSignal(false);
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
        List<Layoff> layoffs = currentPlayer.layOff(new HandLayout(state.cards(state.knocker())).melds());
        return new LayoffAction(currentTurn.playerIndex, layoffs);
    }
    // Getters for GamePlayers
    private static Integer[] indices(GamePlayer p){
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
        return gameIndex==null? null : new Integer[]{gameIndex, playerIndex};
    }
    /**
     * Returns all points in the game currently
     * @param p asking player
     * @return index 0 = asker, 1 = next, etc.. for every player
     */
    public static int[] points(GamePlayer p){
        Integer[] indices = indices(p);
        if(indices==null){
            return null;
        }
        int[] points = currentGames.get(indices[0]).points();
        int[] pointsUpdate = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            pointsUpdate[i] = points[(indices[1]+i)%points.length];
        }
        return pointsUpdate;
    }
    /**
     * Returns the player's playerCards
     * @param p asking player
     * @return current player playerCards
     */
    public static List<MyCard> getCards(GamePlayer p){
        Integer[] indices = indices(p);
        if(indices==null){
            return null;
        }
        return new ArrayList<>(currentGames.get(indices[0]).round().cards(indices[1]));
    }
    public static int numberOfPlayers(GamePlayer p) {
        Integer[] indices = indices(p);
        if(indices==null){
            return 0;
        }
        return currentGames.get(indices[0]).numberOfPlayers();
    }

    // Getters

    public int numberOfPlayers(){
        return players.size();
    }
    public int deckSize(){
        return round().deckSize();
    }
    public int turnNumber(){
        return gameState.getTurn();
    }
    public int roundNumber(){
        return gameState.getRoundNumber();
    }
    public int curPlayerIndex(){
        return gameState.round().turn().playerIndex;
    }
    public Integer gameWinner(){
        if(gameEnded()){
            return gameState.getHighestScoreIndex();
        }else{
            return null;
        }
    }
    public int[] points(){
        return gameState.getPoints();
    }
    public float curTime(){
        return time;
    }
    private boolean outOfTime(){
        return time >= timeAllotted[step().index];
    }
    public boolean gameEnded(){
        return gameState.locked();
    }
    public Turn turn(){
        return gameState.round().turn();
    }
    public Step step(){
        return gameState.round().turn().step;
    }
    private Action lastAction(){
        return gameState.round().actions.size()==0? null : gameState.round().actions.peek();
    }
    public MyCard peekDiscard(){
        return round().peekDiscard();
    }
    public RoundState round(){
        return gameState.round();
    }
    public RoundState round(int i){
        return gameState.round(i);
    }
    public GamePlayer curGamePlayer(){
        return players.get(curPlayerIndex());
    }
    private List<MyCard> curPlayerCards(){
        return cards(curPlayerIndex());
    }
    public List<MyCard> cards(int playerIndex) {
        return round().cards(playerIndex);
    }

    // Setters

    public void init(){
        if(gameState.getRoundNumber()==0) {
            startNewRound();
        }
    }
    public void remove(){
        currentGames.remove(this);
    }
    public void print(boolean pt, boolean pr, boolean pg){
        printTurns = pt;
        printRounds = pr;
        printGame = pg;
        if(printRounds){
            printRound();
        }
    }

    // Misc

    public void printRound(){
        System.out.println("Round "+ roundNumber()+" started with:");
        gameState.round().setLayouts();
        for (int i = 0; i < round().layouts().length; i++) {
            System.out.println("Player "+i+"\n"+ round().layouts()[i]);
            System.out.println("Value: "+ round().layouts()[i].evaluate());
        }
        System.out.println();
    }
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
    public static int[] pointsWon(RoundState state){
        int[] points = new int[state.numberOfPlayers()];
        if(!roundStopCondition(state) || state.knocker()==null){
            return points;
        }
        int winningPlayerIndex = Finder.findLowestDeadwoodIndex(Arrays.asList(state.layouts()), state.layouts()[state.knocker()].deadwoodValue(), state.knocker());
        int deadwoodDifferences = Finder.getPointsToAdd(Arrays.asList(state.layouts()), state.layouts()[winningPlayerIndex].deadwoodValue());
        int bonus = Finder.getBonusPoints(state.knocker(), winningPlayerIndex, state.layouts()[state.knocker()].deadwoodValue());
        points[winningPlayerIndex] = deadwoodDifferences+bonus;
        return points;
    }
    public static void shuffleList(Random rd, int shuffles, List<MyCard> list){
        if(list.size()<=1) {
            return;
        }
        for (int i = 0; i < shuffles; i++) {
            MyCard c =list.remove(rd.nextInt(list.size()));
            list.add(c);
        }
    }
}