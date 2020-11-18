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
    int handValue; //not deadwood value, constant - deadwood, (for easier implementation pruning)

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    boolean playerStop = false; // when game is over this one turns to be true
    boolean AIStop = false; // turn to be true when game is over

    public Node(SetOfCards pile, SetOfCards cards, SetOfCards unknownCards, SetOfCards opponentHand) {
        this.discardPile = pile;
        this.hand = cards;
        this.unknownCards = unknownCards;
        this.opponentHand = opponentHand;
        if(Player.scoreHand(hand.toList()) > Player.scoreHand(opponentHand.toList()) && Player.scoreHand(hand.toList())<=10){
            this.winOrLose = true;
        }
        handValue = Player.getHandValue(cards.toList());
    }

    public Node(boolean positiveInf) {
        this.discardPile = new SetOfCards(false, false);
        this.hand = new SetOfCards(false, false);
        this.opponentHand = new SetOfCards(false, false);
        this.unknownCards = new SetOfCards(false, false);

        if (positiveInf) {
            this.setHandValue(100000);
        }
        else
            this.setHandValue(-100000);
    }

    //we already have static method in Player class
    /*
    public int scoreHand(List<Card> aHand) {
        SetOfCards hand = new SetOfCards(aHand);
        Player player = new Player(hand);
        int scoreHand = player.scoreHand();
        return scoreHand;
    }

     */

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

    public int getHandValue() {
        return this.handValue;
    }

    public void setHandValue(int value) {
        this.handValue = value;
    }

    public static Node getNodeMax(Node node1, Node node2) {
        if (node1.getHandValue() > node2.getHandValue())
            return node1;
        else
            return node2;
    }

    public static Node getNodeMin(Node node1, Node node2) {
        if (node1.getHandValue() < node2.getHandValue())
            return node1;
        else
            return node2;
    }

    public static void main(String[] args) {
        //Node node = new Node(new SetOfCards(false, false), new SetOfCards(false, false), new SetOfCards(true, false));
        //System.out.println(node.parent);
    }

}

