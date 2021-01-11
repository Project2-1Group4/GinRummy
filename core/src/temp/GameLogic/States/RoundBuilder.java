package temp.GameLogic.States;

import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Entities.Turn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RoundBuilder {
    private Turn turn;
    private int nbOfPlayers;
    private List<List<MyCard>> players;
    private Stack<MyCard> deck;
    private Stack<MyCard> discardPile;
    private List<MyCard> unassigned;
    public RoundBuilder(){
        players = new ArrayList<>();
        deck = new Stack<>();
        discardPile = new Stack<>();
        unassigned = new ArrayList<>();
    }
    public RoundBuilder setTurn(Turn turn){
        this.turn = turn;
        return this;
    }
    public RoundBuilder setNumberOfPlayers(int nbOfPlayers){
        this.nbOfPlayers = nbOfPlayers;
        return this;
    }
    public RoundBuilder setStartingDeck(Stack<MyCard> deck){
        this.deck = (Stack<MyCard>) deck.clone();
        return this;
    }
    public RoundBuilder shuffleDeck(Random rd, int shuffles){
        Game.shuffleList(rd, shuffles,deck);
        return this;
    }
    public RoundBuilder distributeCards(int cardsPerHand){
        while(players.size()<nbOfPlayers){
            players.add(new ArrayList<MyCard>());
        }
        for (int i = 0; i < cardsPerHand; i++) {
            for (List<MyCard> player : players) {
                player.add(deck.pop());
            }
        }
        return this;
    }
    public RoundBuilder addToDiscardPile(int nbOfCards){
        for (int i = 0; i < nbOfCards; i++) {
            discardPile.add(deck.pop());
        }
        return this;
    }
    public RoundBuilder addDeckToUnassigned(){
        while(!deck.isEmpty()){
            unassigned.add(deck.pop());
        }
        return this;
    }
    public RoundState build(){
        return new RoundState(new CardsInfo(players, deck, unassigned,discardPile), turn);
    }
}
