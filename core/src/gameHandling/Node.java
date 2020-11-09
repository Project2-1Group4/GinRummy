package gameHandling;

import cardlogic.SetOfCards;

import java.util.*;

public class Node {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards deck;
    SetOfCards opponentHand;


    private List<Node> children = new ArrayList<>();
    private Node parent = null;
    public Node(SetOfCards pile, SetOfCards cards, SetOfCards deck, SetOfCards opponentHand) {
        this.discardPile = pile;
        this.hand = cards;
        this.deck = deck;
        this.opponentHand = opponentHand;
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

