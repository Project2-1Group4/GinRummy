package gameHandling;

import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

public class Node {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards deck;


    private List<Node> children = new ArrayList<>();
    private Node parent = null;
    public Node(SetOfCards pile, SetOfCards cards, SetOfCards deck) {
        this.discardPile = pile;
        this.hand = cards;
        this.deck = deck;
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

}

