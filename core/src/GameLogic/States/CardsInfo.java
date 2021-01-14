package GameLogic.States;

import GameLogic.Entities.MyCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CardsInfo {
    public final List<List<MyCard>> players;
    public final Stack<MyCard> deck;
    public final List<MyCard> unassigned;
    public final Stack<MyCard> discardPile;

    public CardsInfo(List<List<MyCard>> players, Stack<MyCard> deck, List<MyCard> unassigned, Stack<MyCard> discardPile) {
        this.players = players;
        this.deck = deck;
        this.unassigned = unassigned;
        this.discardPile = discardPile;
    }
    public CardsInfo(CardsInfo k){
        players = new ArrayList<>();
        for (int i = 0; i < k.players.size(); i++) {
            players.add(new ArrayList<>(k.players.get(i)));
        }
        deck = (Stack<MyCard>) k.deck.clone();
        unassigned = new ArrayList<>(k.unassigned);
        discardPile = (Stack<MyCard>) k.discardPile.clone();
    }

    // Getters

    public int deckSize(){
        return deck.size();
    }
    public int numberOfCards(){
        int sum = 0;
        for (List<MyCard> player : players) {
            sum+=player.size();
        }
        return sum+discardPile.size()+deck.size()+unassigned.size();
    }
    public boolean numberOfCardsIs(int nb){
        return numberOfCards() == nb;
    }
    public boolean hasPerfectInformation(){
        return unassigned.size()==0;
    }
    public MyCard peekDeck(){
        return deck.size()==0? null : deck.peek();
    }
    public MyCard peekDiscard(){
        return discardPile.size()==0? null : discardPile.peek();
    }
    public List<MyCard> allCards(){
        List<MyCard> cards = new ArrayList<>(deck);
        cards.addAll(discardPile);
        cards.addAll(unassigned);
        for (List<MyCard> player : players) {
            cards.addAll(player);
        }
        return cards;
    }
    public List<MyCard> playerCards(int index){
        return players.get(index);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            sb.append("Player ").append(i).append(" (").append(players.get(i).size()).append("): ").append(players.get(i)).append("\n");
        }
        sb.append("Deck (").append(deck.size()).append("): ").append(deck).append("\n");
        sb.append("Discard (").append(discardPile.size()).append("): ").append(discardPile).append("\n");
        sb.append("Unassigned (").append(unassigned.size()).append("): ").append(unassigned);
        return sb.toString();
    }
}