package temp.GamePlayers.GameTreeAIs;

import cardlogic.SetOfCards;
import gameHandling.Player;
import temp.GameLogic.Entities.MyCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
    public SetOfCards discardPile;
    public SetOfCards hand;
    public SetOfCards unknownCards;
    public SetOfCards opponentHand;
    public boolean winOrLose;
    public int handValue; //not deadwood value, constant - deadwood, (for easier implementation pruning)

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    private int depthTree;
    protected HashMap<MyCard, Double> probMap = new HashMap<>();

    public boolean playerStop = false; // when game is over this one turns to be true
    public boolean AIStop = false; // turn to be true when game is over

    public Node(SetOfCards pile, SetOfCards cards, SetOfCards unknownCards, SetOfCards opponentHand, int depth) {
        this.discardPile = pile;
        this.hand = cards;
        this.unknownCards = unknownCards;
        this.opponentHand = opponentHand;
        this.depthTree = depth;

        int pScore = Player.scoreHand(hand.toList());
        int opHand = Player.scoreHand(opponentHand.toList());

        if((pScore < opHand) && pScore<=10){
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

    double getProbability(MyCard aCard){
        return this.probMap.get(aCard);
    }

    void updateProbability(MyCard aCard, double aVal){
        this.probMap.put(aCard, aVal);
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

    public int getDepthTree() {
        return depthTree;
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
        return hand.toString();
    }

    public int getHandValue() {
        //return Player.getHandValue(this.hand.toList());
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

