package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;

import java.util.List;
import java.util.Stack;

public class BeepBoopDifferences {
    public final List<MyCard> player;
    public final List<MyCard> otherPlayer;
    public final List<MyCard> unknown;
    public final List<MyCard> deck;
    public final Stack<MyCard> discardPile;
    public State.StepInTurn[] step;
    // 0 = me, 1 = other
    public int[] turn;
    public Integer[] finished = null;
    public BeepBoopDifferences(Integer[] finished, State.StepInTurn[] step, int[] turn, List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> deck, List<MyCard> unknown, Stack<MyCard> discard) {
        this.finished = finished;
        this.step = step;
        this.turn = turn;
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.deck = deck;
        this.unknown = unknown;
        this.discardPile = discard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turn: ").append(turn[0]).append(" ").append(turn[1]).append("\n");
        sb.append("Step: ").append(step[0]).append(" ").append(step[1]).append("\n");
        sb.append("Finished: ").append(finished[0]).append(" ").append(finished[1]).append("\n");
        sb.append("Player me: ").append(MyCard.toString(player)).append("\n");
        sb.append("Player other: ").append(MyCard.toString(otherPlayer)).append("\n");
        sb.append("Deck: ").append(MyCard.toString(deck)).append("\n");
        sb.append("Discard: ").append(MyCard.toString(discardPile)).append("\n");
        sb.append("Unknown: ").append(MyCard.toString(unknown));
        return sb.toString();
    }
}
