package GameLogic.States;

import GameLogic.Game;
import GameLogic.Entities.MyCard;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    /* Getters */

    public int deckSize(){
        return deck.size();
    }
    public int getNumberOfCards(){
        int sum = 0;
        for (List<MyCard> player : players) {
            sum+=player.size();
        }
        return sum+discardPile.size()+deck.size()+unassigned.size();
    }
    public boolean hasNumberOfCards(int nb){
        return getNumberOfCards() == nb;
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
    public List<MyCard> getAllCards(){
        List<MyCard> cards = new ArrayList<>(deck);
        cards.addAll(discardPile);
        cards.addAll(unassigned);
        for (List<MyCard> player : players) {
            cards.addAll(player);
        }
        return cards;
    }
    public List<MyCard> getCards(int index){
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

    public static CardsInfo getRandom(int player , Integer seed){
        Random rd;
        if(seed==null){
            rd = new Random();
        }else{
            rd= new Random(seed);
        }
        Stack<MyCard> deck = MyCard.getBasicDeck();
        Game.shuffleList(rd, 500, deck);
        List<List<MyCard>> p = new ArrayList<>();
        for (int i = 0; i < player; i++) {
            p.add(new ArrayList<MyCard>());
            if(i==0){
                for(int j = 0; j< GameRules.baseCardsPerHand; j++) {
                    p.get(i).add(deck.remove(0));
                }
            }
            else{
                for(int j=0; j< rd.nextInt(GameRules.baseCardsPerHand);j++) {
                    p.get(i).add(deck.remove(0));
                }
            }
        }
        Stack<MyCard> discard = new Stack<>();
        for(int i=0;i<rd.nextInt(deck.size()-1);i++){
            discard.add(deck.remove(i));
        }
        List<MyCard> unassigned = new Stack<>();
        for(int i=0;i<rd.nextInt(deck.size()-1);i++){
            unassigned.add(deck.remove(i));
        }

        return new CardsInfo(p, deck, unassigned,discard);
    }
    public static CardsInfo difference(CardsInfo kb1, CardsInfo kb2){

        List<List<MyCard>> playerDiff = new ArrayList<>();
        for (int i = 0; i < kb1.players.size(); i++) {
            playerDiff.add(new ArrayList<>(MyCard.intraListDifference(kb1.players.get(i), kb2.players.get(i))));
        }
        List<MyCard> unknownDiff = new ArrayList<>(MyCard.intraListDifference(kb1.unassigned, kb2.unassigned));
        Stack<MyCard> deckDiff = new Stack<>();
        deckDiff.addAll(new ArrayList<>(MyCard.intraListDifference(kb1.deck, kb2.deck)));
        Stack<MyCard> discardDiff = new Stack<>();
        discardDiff.addAll(new ArrayList<>(MyCard.intraListDifference(kb1.discardPile, kb2.discardPile)));
        return new CardsInfo(playerDiff, deckDiff, unknownDiff, discardDiff);
    }
}