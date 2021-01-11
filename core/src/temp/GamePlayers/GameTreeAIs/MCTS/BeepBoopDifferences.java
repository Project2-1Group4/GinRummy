package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.Entities.MyCard;

import java.util.List;
import java.util.Stack;

public class BeepBoopDifferences {
    public final List<List<MyCard>> players;
    public final List<MyCard> unassigned;
    public final List<MyCard> deck;
    public final Stack<MyCard> discardPile;

    public BeepBoopDifferences(List<List<MyCard>> players, List<MyCard> deck, List<MyCard> unknown, Stack<MyCard> discard) {
        this.players = players;
        this.deck = deck;
        this.unassigned = unknown;
        this.discardPile = discard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            sb.append("Player ").append(i).append(" :").append(players.get(i)).append("\n");
        }
        sb.append("Deck: ").append(deck).append("\n");
        sb.append("Discard: ").append(discardPile).append("\n");
        sb.append("Unassigned: ").append(unassigned);
        return sb.toString();
    }
}
