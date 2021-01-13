package GamePlayers.GameTreeAIs;

import org.jetbrains.annotations.NotNull;
import GameLogic.Entities.Meld;
import GameLogic.Entities.MyCard;
import GameLogic.Logic.Finder;

import java.util.*;

public class Node implements Comparable {

    public List<MyCard> discardPile;
    public List<MyCard> hand;
    public List<MyCard> unknownCards; // all cards that are unknown for AI, so all cards - (discard pile + own hand)
    public List<MyCard> opponentHand;
    public boolean winOrLose;
    public int handValue; // constant - deadwood (for easier implementation pruning)

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    public static final int constantScore = 100;

    private int depthTree;
    private double[][] probMap = new double[4][13];

    public boolean playerStop = false; // when game is over this one turns to be true
    public boolean AIStop = false; // turn to be true when game is over

    /*
    constructor for node with default probabilities
     */
    public Node(List<MyCard> pile, List<MyCard> cards, List<MyCard> unknownCards, List<MyCard> opponentHand, int depth) {
        this.discardPile = pile;
        this.hand = cards;
        this.unknownCards = unknownCards;
        this.opponentHand = opponentHand;
        this.depthTree = depth;

        int pScore = Finder.findBestHandLayout(hand).deadwoodValue();  // own hand
        int opHand = Finder.findBestHandLayout(opponentHand).deadwoodValue(); // opponenthand

        if((pScore < opHand) && pScore<=10){
            this.winOrLose = true;
        }
        handValue = Node.getHandValue(cards);
        this.setDefaultProbabilities();
    }

    /*
    constructor for Node with probabilities saved
     */
    public Node(List<MyCard> pile, List<MyCard> cards, List<MyCard> unknownCards, List<MyCard> opponentHand, int depth, double[][] probMap){
        this(pile, cards, unknownCards, opponentHand, depth);
        this.probMap = probMap;
    }

    /*
    default probabilities for start of round, uniform distribution for all unknown cards
     */
    void setDefaultProbabilities(){
        for(MyCard card: unknownCards){
            this.setProbability(card, 1.0/41.0);
        }
    }

    /*
    constructor used for alpha-beta search
     */
    public Node(boolean positiveInf) {
        this.discardPile = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.opponentHand = new ArrayList<>();
        this.unknownCards = new ArrayList<>();

        if (positiveInf) {
            this.setHandValue(100000); // beta
        }
        else
            this.setHandValue(-100000); // alpha
    }

    void setProbability(MyCard card, double val){
        if(val >= 1.0){
            probMap[card.suit.index][card.rank.index] = 1.0;
        }
        else if(val <= 0.0) {
            probMap[card.suit.index][card.rank.index] = 0.0;
        }
        else {
            probMap[card.suit.index][card.rank.index] = val;
        }
    }

    /*
    returns probabilities of all cards
     */
    double[][] getProbMap(){
        double[][] cloneMap = new double[probMap.length][];

        for(int i=0;i<probMap.length;i++){
            cloneMap[i] = this.probMap[i].clone();
        }

        return cloneMap;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    /*
    depth of node in tree
     */
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

    public int getHandValue() { return this.handValue; }

    public void setHandValue(int value) {
        this.handValue = value;
    }

    /*
    evaluation function alpha, returns best node
     */
    public static Node getNodeMax(Node node1, Node node2) {
        // if hand values are almost the same, also look at deadwood cards for almost melds
        if (Math.abs(node1.getHandValue() - node2.getHandValue()) <= 3){
            int almostMelds1 = almostMelds(node1.hand);
            int almostMelds2 = almostMelds(node2.hand);
            // if nodes have same amount of almost melds compare as usual
            if(almostMelds1 == almostMelds2){
                if(node1.getHandValue() > node2.getHandValue()){
                    return node1;
                }
                else{ return node2; }
            }
            // if node with lower value has more almost melds, return this node
            else if(almostMelds1 < almostMelds2){
                return node2;
            }
            else{ return node1; }
        }
        // usual comparison: node with highest hand value is returned
        else if(node1.getHandValue() > node2.getHandValue()){
            return node1;
        }
        else { return node2; }
    }

    /*
    evaluation function beta, returns best node (same method as getNodeMax())
     */
    public static Node getNodeMin(Node node1, Node node2) {
        if (Math.abs(node1.getHandValue() - node2.getHandValue()) <= 3){
            int almostMelds1 = almostMelds(node1.hand);
            int almostMelds2 = almostMelds(node2.hand);
            if(almostMelds1 == almostMelds2){
                if(node1.getHandValue() < node2.getHandValue()){
                    return node1;
                }
                else{ return node2; }
            }
            else if(almostMelds1 > almostMelds2){
                return node2;
            }
            else{ return node1; }
        }
        else if (node1.getHandValue() < node2.getHandValue()){
            return node1;
        }
        else { return node2; }
    }

    /*
    look for almost melds in deadwood cards
    almost meld = 2 out of 3 cards to form a meld
     */
    public static int almostMelds(List<MyCard> currentHand){
        int almostMelds = 0;
        List<MyCard> deadwoodCards = Finder.findBestHandLayout(currentHand).unused();
        List<Meld>  melds = Finder.findBestHandLayout(currentHand).melds();
        List<MyCard> meldCards = new ArrayList<>();
        for (Meld setOfMeld : melds) {
            meldCards.addAll(new ArrayList<>(setOfMeld.cards()));
        }
        // look through deadwood card for possible almost meld
        for(int j = 0; j< deadwoodCards.size(); j++){
            for(int i = 0; i< deadwoodCards.size(); i++){
                // look for set
                if(deadwoodCards.get(j).rank.index == deadwoodCards.get(i).rank.index && i!=j){
                    int cardinMeld = 2; // 4 suits, 2 are already in deadwood so 2 left
                    // look if cards needed to finish meld are not already used in another meld,
                    // which makes the almost meld impossible to become a actual meld
                    for(int k = 0; k< meldCards.size(); k++){
                        if(meldCards.get(k).rank.index == deadwoodCards.get(i).rank.index){
                            cardinMeld--;
                        }
                    }
                    if(cardinMeld > 0){ // if not all cards needed to finish almost meld are already used, its an possible almost meld
                        almostMelds++;
                    }
                }
                // look for run
                if(deadwoodCards.get(j).suit.index == deadwoodCards.get(i).suit.index && Math.abs(deadwoodCards.get(j).rank.index - deadwoodCards.get(i).rank.index) == 1 && i!=j){
                    int cardinMeld = 1; // run has 3 cards, 2 are already in deadwood cards
                    for(int k = 0; k< meldCards.size(); k++){
                        if(meldCards.get(k).suit.index == deadwoodCards.get(j).suit.index && Math.abs(meldCards.get(k).rank.index - deadwoodCards.get(j).rank.index) == 1 ){
                            cardinMeld--;
                        }
                    }
                    if(cardinMeld>0){
                        almostMelds++;
                    }
                }
            }
        }
        // since almost melds have 2 cards in the deadwood, all possible melds will be counted twice
        // so divide by 2 to get rid of the double counted melds
        almostMelds = almostMelds/2;
        return almostMelds;
    }


    /*
    returns hand value (= constant - deadwood score)
     */
    public static int getHandValue(List<MyCard> aHand) {
        int scoreHand = Finder.findBestHandLayout(aHand).deadwoodValue();
        return constantScore - scoreHand;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        Node node = (Node) o;
        if (this.getHandValue() > node.getHandValue())
            return 1;
        else if (this.getHandValue() == node.getHandValue())
            return 0;
        else
            return -1;
    }
}

