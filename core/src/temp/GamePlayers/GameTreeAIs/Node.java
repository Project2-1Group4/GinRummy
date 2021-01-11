package temp.GamePlayers.GameTreeAIs;

import org.jetbrains.annotations.NotNull;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.Meld;
import temp.GameLogic.Entities.MyCard;

import java.util.*;

public class Node implements Comparable {
    /*
    Main change I did was change from set of cards into List<MyCard>
    From there on I went through the code and tried to fix all of the red lines that appeared
    That's the gist of it tbh
     */

    public List<MyCard> discardPile;
    public List<MyCard> hand;
    public List<MyCard> unknownCards;
    public List<MyCard> opponentHand;
    public boolean winOrLose;
    public int handValue; //not deadwood value, constant - deadwood, (for easier implementation pruning)

    private List<Node> children = new ArrayList<>();
    private Node parent = null;

    public static final int constantScore = 100;

    private int depthTree;
    //protected HashMap<MyCard, Double> probMap = new HashMap<>();
    private double[][] probMap = new double[4][13];

    public boolean playerStop = false; // when game is over this one turns to be true
    public boolean AIStop = false; // turn to be true when game is over

    public Node(List<MyCard> pile, List<MyCard> cards, List<MyCard> unknownCards, List<MyCard> opponentHand, int depth) {
        this.discardPile = pile;
        this.hand = cards;
        this.unknownCards = unknownCards;
        this.opponentHand = opponentHand;
        this.depthTree = depth;

        int pScore = Finder.findBestHandLayout(hand).deadwoodValue(); //Player.scoreHand(hand.toList());
        int opHand = Finder.findBestHandLayout(opponentHand).deadwoodValue();//Player.scoreHand(opponentHand.toList());
       // System.out.println("probabilities = " + Arrays.deepToString(probMap));
        //System.out.println(" ");
        if((pScore < opHand) && pScore<=10){
            this.winOrLose = true;
        }
        handValue = Node.getHandValue(cards);

        this.setDefaultProbabilities();
    }

    public Node(List<MyCard> pile, List<MyCard> cards, List<MyCard> unknownCards, List<MyCard> opponentHand, int depth, double[][] probMap){
        this(pile, cards, unknownCards, opponentHand, depth);
        this.probMap = probMap;
    }

    void setDefaultProbabilities(){
        for(MyCard card: unknownCards){
            this.setProbability(card, 1.0/41.0);
        }
    }

    public Node(boolean positiveInf) {
        this.discardPile = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.opponentHand = new ArrayList<>();
        this.unknownCards = new ArrayList<>();

        if (positiveInf) {
            this.setHandValue(100000);
        }
        else
            this.setHandValue(-100000);
    }

    double getProbability(MyCard aCard){
        return probMap[aCard.suit.index][aCard.rank.index];
    }

    void updateProbability(MyCard aCard, double aVal){
        probMap[aCard.suit.index][aCard.rank.index] = probMap[aCard.suit.index][aCard.rank.index]*aVal;
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

    // TODO: Make sure it's a deep copy being made
    double[][] getProbMap(){
        double[][] cloneMap = new double[probMap.length][];

        for(int i=0;i<probMap.length;i++){
            cloneMap[i] = this.probMap[i].clone();
        }

        return cloneMap;
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
        if (Math.abs(node1.getHandValue() - node2.getHandValue()) <= 3){
            int almostMelds1 = almostMelds(node1.hand);
            int almostMelds2 = almostMelds(node2.hand);
            if(almostMelds1 == almostMelds2){
                if(node1.getHandValue() > node2.getHandValue()){
                    return node1;
                }
                else{
                    return node2;
                }
            }
            else if(almostMelds1 < almostMelds2){
                return node2;
            }
            else{
                return node1;
            }

        }
        else if(node1.getHandValue() > node2.getHandValue()){
            return node1;
        }
        else {
            return node2;
        }
    }

    public static Node getNodeMin(Node node1, Node node2) {
        if (Math.abs(node1.getHandValue() - node2.getHandValue()) <= 3){
            int almostMelds1 = almostMelds(node1.hand);
            int almostMelds2 = almostMelds(node2.hand);
            if(almostMelds1 == almostMelds2){
                if(node1.getHandValue() < node2.getHandValue()){
                    return node1;
                }
                else{
                    return node2;
                }
            }
            else if(almostMelds1 > almostMelds2){
                return node2;
            }
            else{
                return node1;
            }

        }
        else if (node1.getHandValue() < node2.getHandValue()){
            return node1;
        }
        else {
            return node2;
        }
    }

    public static int almostMelds(List<MyCard> currentHand){
        int almostMelds = 0;
        List<MyCard> deadwoodCards = Finder.findBestHandLayout(currentHand).unused();
        List<Meld>  melds = Finder.findBestHandLayout(currentHand).melds();
        List<MyCard> meldCards = new ArrayList<>();
        for (Meld setOfMeld : melds) {
            meldCards.addAll(new ArrayList<>(setOfMeld.viewMeld()));
        }
        for(int j = 0; j< deadwoodCards.size(); j++){
            for(int i = 0; i< deadwoodCards.size(); i++){
                if(deadwoodCards.get(j).rank.index == deadwoodCards.get(i).rank.index && i!=j){
                    int cardinMeld = 2;
                    for(int k = 0; k< meldCards.size(); k++){
                        if(meldCards.get(k).rank.index == deadwoodCards.get(i).rank.index){
                            cardinMeld--;
                        }
                    }
                    if(cardinMeld > 0){
                        almostMelds++;
                    }
                }
                if(deadwoodCards.get(j).suit.index == deadwoodCards.get(i).suit.index && Math.abs(deadwoodCards.get(j).rank.index - deadwoodCards.get(i).rank.index) == 1 && i!=j){
                    int cardinMeld = 1;
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
        almostMelds = almostMelds/2;
        return almostMelds;
    }


    /*
    No idea why the original handValue method was implemented as it is, but this is my attempt at bringing it to the new game system
    All I did was copy the code and alter the deadwood method to use what exists in the new game logic
    So hopefully this works perfectly
     */
    public static int getHandValue(List<MyCard> aHand) {
        int scoreHand = Finder.findBestHandLayout(aHand).deadwoodValue();
        return constantScore - scoreHand;
    }

    public static void main(String[] args) {
        List<MyCard> cardList = MyCard.getBasicDeck();
        List<MyCard> tryHand = new ArrayList<>();
        Random getcard = new Random();
        int num =0;
        for(int i= 0; i<10; i++){
            num = getcard.nextInt(51);
            tryHand.add(cardList.get(num));
        }
        System.out.println("hand "+tryHand);
        int melds = almostMelds(tryHand);
        System.out.println("almost melds amount = "+melds);

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

