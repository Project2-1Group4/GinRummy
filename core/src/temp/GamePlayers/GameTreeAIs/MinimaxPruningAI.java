package temp.GamePlayers.GameTreeAIs;

import cardlogic.Card;
import cardlogic.SetOfCards;
import gameHandling.Player;
import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;

import javax.swing.*;
import java.util.*;

public class MinimaxPruningAI extends GamePlayer {
    GametreeAI tree;
    boolean AITurn;

    /*
    Main change I did was change from set of cards into List<MyCard>
    From there on I went through the code and tried to fix all of the red lines that appeared
    That's the gist of it tbh
     */

    public List<MyCard> hand;
    public List<MyCard> pile;
    public List<MyCard> unknownCards;
    boolean playerKnock = false;
    boolean AIknock = false;
    static int round = 0;
    //public static SetOfCards deck;
    int maxDepthOfTree = 3;
    private List<MyCard> backupHand;
    private double[][] probMap = new double[4][13];


    public List<MyCard> discardedCards = new ArrayList<>();

/*
    public MinimaxPruningAI(GametreeAI tree, SetOfCards pile, SetOfCards hand, SetOfCards unknown) {
        this.tree = tree;
        this.hand = hand;
        this.pile = pile;
        this.unknownCards = unknown; //include deck and opponent hand
        AITurn = true;
    }
*/
    public MinimaxPruningAI(GametreeAI tree) {
        this.tree = tree;
        this.hand = tree.hand;
        this.pile = tree.discardPile;
        this.unknownCards = tree.cardsUnknown;
    }

    public MinimaxPruningAI(){
        super();
    }


    public void pickedCard() {
        AITurn = false;
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer) {
        if ((node.getChildren().size() == 0) || node.playerStop || node.AIStop) {
            return node;
        }

        if (maxPlayer) {
            //System.out.println("run alpha beta with max player");
            Node maxNode = new Node(false); //node with negative inf hand value
            //Node maxNode = null;
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, false);
                //System.out.println("evalNode: "+evalNode);
                maxNode = Node.getNodeMax(alpha, evalNode);
                //System.out.println("max node: "+maxNode);
                alpha = Node.getNodeMax(alpha, evalNode);
                //System.out.println("alpha: "+alpha);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            //System.out.println("max node: "+maxNode);
            return maxNode;
        } else {
            //System.out.println("run alpha beta with min player");
            Node minNode = new Node(true); // node with positive inf hand value
            //Node minNode = null;
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, true);
                minNode = Node.getNodeMin(beta, evalNode);
                //System.out.println("min node: "+minNode);
                beta = Node.getNodeMin(beta, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            //System.out.println("min node: "+minNode);
            return minNode;
        }
    }

    public MyCard[] getNodeReturn() {
        //create the tree
        Node parent = tree.getRootNode();

        List<MyCard> currentHand = GametreeAI.deepCloneMyCardList(parent.hand);
        System.out.println("current bot hand: "+currentHand);
        System.out.println("deadwood = "+handLayout.getDeadwood());
        Node pickNode = alphaBetaPruning(parent, new Node(false), new Node(true), true);
        //System.out.println("pick node (hoping hand): "+pickNode);


        for(MyCard card : unknownCards){
            if(!pickNode.unknownCards.contains(card)){
                pickNode.unknownCards.add(card);
            }
        }
        unknownCards = pickNode.unknownCards;
        probMap = pickNode.getProbMap();
        System.out.println("probabilitiesCalc = " + Arrays.deepToString(probMap));
        System.out.println(" ");
        List<MyCard> newHand = GametreeAI.deepCloneMyCardList(pickNode.hand);
        MyCard pickCard = null;
        for(MyCard card : newHand) {
            if (!currentHand.contains(card)) {
                pickCard = card;
            }
        }

        //loop through newHand to get the new card

        //System.out.println("pick card: "+pickCard);
        MyCard discardCard = null;

        //if pickCard and discardCard are both null. It means that after simulating the bot does not want to change the hand
        //at current state
        //loop through old hand to get the card be discarded
        for(MyCard card : currentHand) {
            if (!newHand.contains(card)) {
                discardCard = card;
            }
        }

        //System.out.println("discard card: "+discardCard);

        return new MyCard[] {pickCard, discardCard};
    }


    public static void main(String[] args) {
        GameLogic g = new GameLogic(true, true);
        g.play(new MinimaxPruningAI(), new MinimaxPruningAI(), 0);
    }

    /*
    If true then the player knocks and the round ends
    If false then the player doesn't knock
    */
    @Override
    public Boolean knockOrContinue() {
        //System.out.println("problem score "+new SetOfCards(this.allCards, false).toList());

        //int score = Player.scoreHand(new SetOfCards(this.allCards, false).toList());
        if (this.handLayout.getDeadwood() <= 10){
            System.out.println("end Round");
            return true;
        } else {
            return false;
        }
    }

    /*
    Returns true if the deck is picked
    False if the discard pile is picked
     */
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        createTree();
        this.tree.createTree(false);
        MyCard[] pick_discard = this.getNodeReturn();
        MyCard topCard = new MyCard(topOfDiscard);
        backupHand = new ArrayList<>();
        if(pick_discard[0]==null){
            for(int i=0; i<allCards.size();i++){
                backupHand.add(allCards.get(i));

            }

            return true;
        }
        else if(!pick_discard[0].equals(topCard)){
            for(int i=0; i<allCards.size();i++){
                backupHand.add(allCards.get(i));

            }
            return true;
        }

        else {
            System.out.println("DiscardPile");
            this.discardedCards.remove(pick_discard[0]);
            return false;
        }

    }

    /*
    Returns the card that wants to be removed from the current hand

    I dont know this one does not work with this algo cuz we should have deck as param to choose whether bot wants to
    pick from deck or pile then he will have different options discard card. I will make a different one having param deck
    but you can always modify it

     */
    @Override
    public MyCard discardCard() {
        if(backupHand.size()!=0){
            for(MyCard card : allCards){
                if(!backupHand.contains(card)){
                    if(unknownCards.contains(card)){
                        //does it remove the card!!
                        unknownCards.remove(card);
                        this.setProbability(card, 0.0);
                        }
                    }
                }
            }
        MyCard aCard = GametreeAI.chooseCardToDiscard(this.allCards);
        this.discardedCards.add(aCard);
        return aCard;
    }

    /*
    It's called everytime the player's hand is changed
    So this means it's called when:
        Before the player chooses from the discard pile or from the deck
        The player chooses what to discard
        After the player has discarded a card (with the new 10 card hand)
     */
    @Override
    public void update(HandLayout realLayout) {
        super.update(realLayout);
        //SetOfCards cardList = new SetOfCards(realLayout);

    }

    public void createTree(){
        this.tree = new GametreeAI(this.discardedCards,this.allCards, this.unknownCards, this.maxDepthOfTree, this.probMap);

    }

    public static List<MyCard> findRemainingCards(List<MyCard> hand, List<MyCard> discardPile){
        List<MyCard> knownCards = new ArrayList<>(hand);
        knownCards.addAll(discardPile);

        List<MyCard> cardList = MyCard.getBasicDeck();

        // TODO: Check that this remove method is working properly
        for(MyCard aCard: knownCards){
            cardList.remove(aCard);
        }

        return cardList;
    }

    /*
    Complete round reset, so it's a new hand and a new deck and a new everything
     */
    @Override
    public void newRound(MyCard topOfDiscard) {

        this.discardedCards = new ArrayList<>();
        this.discardedCards.add(new MyCard(topOfDiscard));
        this.unknownCards = findRemainingCards(this.allCards, this.discardedCards);
        probMap = new double[4][13];
        for(MyCard card: unknownCards){
            this.setProbability(card, 1.0/41.0);
        }
        System.out.println("probabilities = " + Arrays.deepToString(probMap));
        System.out.println(" ");
        createTree();
        this.tree.createTree(true);
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
    Gives information on the other player's actions
        Just says what card the other player discarded
     */
    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        MyCard disCard = discardAction.card;
        this.discardedCards.add(disCard);
        Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0 );
        for(int i =0; i<unknownCards.size();i++){
            if(disCard.suit.index ==unknownCards.get(i).suit.index&& disCard.rank.index == unknownCards.get(i).rank.index){
                this.unknownCards = this.tree.updateProbDiscard(current,disCard);
                this.probMap = this.tree.probMap;
                this.unknownCards.remove(unknownCards.get(i));
            }
        }
    }

    /*
    Gives info on the other player's actions
        If pickAction.deck is not true, then the other player picked from the discard pile
        So we can know what he picked up
     */
    @Override
    public void playerPicked(PickAction pickAction) {

        if(!pickAction.deck){
            MyCard pickedCard = pickAction.card;
            this.discardedCards.remove(pickedCard);
            Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0 );
            this.unknownCards = this.tree.updateProbPickPile(current,pickedCard);
            this.probMap= this.tree.probMap;
        }
        else{
            Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0 );
            MyCard notChosen = new MyCard(this.discardedCards.get(discardedCards.size()-1));
            this.unknownCards = this.tree.updateProbDiscard(current,notChosen);
            this.probMap= this.tree.probMap;
        }
    }

}
