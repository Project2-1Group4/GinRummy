package GameLogic.States;

import Extra.PostGameInformation.Result;
import GameLogic.Entities.MyCard;
import GameLogic.Entities.Step;
import GameLogic.Entities.Turn;
import Extra.GameRules;

import java.util.*;

public class GameState {
    public final Stack<MyCard> gameDeck;
    public final Stack<RoundState> rounds;
    public final int[] points;
    public final int nbOfPlayers;
    public final Random rd;
    private boolean locked = false;

    public GameState(int nbOfPlayers, Stack<MyCard> gameDeck,Integer seed){
        this.nbOfPlayers = nbOfPlayers;
        this.gameDeck = (Stack<MyCard>) gameDeck.clone();
        if(seed==null){
            rd = new Random();
        }
        else{
            rd = new Random(seed);
        }
        rounds = new Stack<>();
        points = new int[nbOfPlayers];
    }
    public GameState(RoundState initRound, Integer seed) {
        if(seed==null){
            rd = new Random();
        }
        else{
            rd = new Random(seed);
        }
        rounds = new Stack<>();
        rounds.add(initRound);
        nbOfPlayers = initRound.numberOfPlayers();
        points = new int[nbOfPlayers];
        gameDeck = new Stack<>();
        gameDeck.addAll(initRound.allCards());
    }

    // Getters

    public int getRoundNumber() {
        return rounds.size();
    }
    public int getTurn(){
        return rounds.peek().turnsPlayed();
    }
    public int getHighestScoreIndex(){
        int max = 0;
        int highest = -1;
        for (int i = 0; i < points.length; i++) {
            if(points[i]>max){
                max = points[i];
                highest = i;
            }
        }
        return highest;
    }
    public Integer winner(){
        int highest = getHighestScoreIndex();
        if(points[highest]>=GameRules.pointsToWin){
            return highest;
        }
        return null;
    }
    public int[] getPoints(){
        return points;
    }
    public boolean locked() {
        return locked;
    }
    public RoundState round(){
        return rounds.peek();
    }
    public RoundState round(int i){
        if(i<0 || i>=rounds.size()){
            return null;
        }
        return rounds.get(i);
    }
    public List<Result> toResult() {
        List<Result> r = new ArrayList<>();
        for (RoundState round : rounds) {
            r.add(new Result(round));
        }
        return r;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Deck: ").append(gameDeck).append("\n");
        sb.append("Points: ").append(Arrays.toString(points)).append("\n");
        for (int i = 0; i < rounds.size(); i++) {
            sb.append("\nROUND ").append(i).append("\n\n").append(rounds.get(i));
        }
        return sb.toString();
    }

    // Setters

    public void addPoints(int[] toAdd){
        for (int i = 0; i < points.length; i++) {
            points[i]+= toAdd[i];
        }
    }
    public void createNewRound(){
        RoundState newRound = new RoundBuilder()
                .setTurn(new Turn(Step.Pick, rounds.size()%nbOfPlayers))
                .setNumberOfPlayers(nbOfPlayers)
                .setStartingDeck(gameDeck)
                .shuffleDeck(rd,500)
                .distributeCards(GameRules.baseCardsPerHand)
                .addToDiscardPile(1)
                .build();
        rounds.add(newRound);
    }
    public void lock() {
        locked = true;
    }
}