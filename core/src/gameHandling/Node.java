package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.*;

public class Node {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards unknownCards;
    SetOfCards opponentHand;
    boolean winOrLose;

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    public Node(SetOfCards pile, SetOfCards cards, SetOfCards unknownCards, SetOfCards opponentHand) {
        this.discardPile = pile;
        this.hand = cards;
        this.unknownCards = unknownCards;
        this.opponentHand = opponentHand;
        if(scoreHand(hand.toList()) > scoreHand(opponentHand.toList()) && scoreHand(hand.toList())<=10){
            this.winOrLose = true;
        }
    }

    public int scoreHand(List<Card> aHand) {
        SetOfCards hand = new SetOfCards(aHand);
        Player player = new Player(hand);
        int scoreHand = player.scoreHand();
        return scoreHand;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Node addChild(Node child){
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "node";
    }

    public static void main(String[] args) {
        //Node node = new Node(new SetOfCards(false, false), new SetOfCards(false, false), new SetOfCards(true, false));
        //System.out.println(node.parent);
    }

}

