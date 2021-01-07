package temp.GamePlayers.GameTreeAIs;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;

import java.util.*;

public class MinimaxPruningAI extends GamePlayer {
    GametreeAI tree;

    public List<MyCard> hand;
    public List<MyCard> pile;
    public List<MyCard> unknownCards;
    int maxDepthOfTree = 4;
    private List<MyCard> backupHand;
    private double[][] probMap = new double[4][13];
    public List<MyCard> discardedCards = new ArrayList<>();
    private boolean nullMove;
    //variable for null move heuristic
    private int R;

    public MinimaxPruningAI(){
        super();
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer, int maxDepth) {
        if (node.getChildren().size() == 0 || node.playerStop || node.AIStop || node.getDepthTree() == maxDepth) {
            return node;
        }
        // null move heuristic
        if(nullMove && node.getDepthTree() >= 2){
            int move = 0;
            if(node.getChildren().size() == 1){
               move = 0;
            }
            else{
                Random moveGenerator  =new Random();
                System.out.println("children size = " +node.getChildren().size());
                move = moveGenerator.nextInt(node.getChildren().size()-1);
            }

            if(maxPlayer){
                Node evalNode = alphaBetaPruning(node.getChildren().get(move), alpha, beta, false, node.getDepthTree() -1 - R);
                if(evalNode.getHandValue() <= alpha.getHandValue()){
                    Node maxNode = alpha;
                    return maxNode;
                }
            }
            else{
                Node evalNode = alphaBetaPruning(node.getChildren().get(move), alpha, beta, true, node.getDepthTree() -1 - R);
                if(evalNode.getHandValue() >= beta.getHandValue()){
                    Node minNode = beta;
                    return minNode;
                }
            }
        }
        // basic alphabeta
        if (maxPlayer) {
            Node maxNode = new Node(false); //node with negative inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, false, maxDepthOfTree);
                maxNode = Node.getNodeMax(alpha, evalNode);
                alpha = maxNode;
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return maxNode;
        } else {
            Node minNode = new Node(true); // node with positive inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, true, maxDepthOfTree);
                minNode = Node.getNodeMin(beta, evalNode);
                beta = minNode;
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return minNode;
        }
    }
    //activation of the basic minimax with aplha beta pruning
    public Node basicAlphaBeta(Node root){
        return alphaBetaPruning(root, new Node(false), new Node(true), true, maxDepthOfTree);
    }
    // activation fo the iterative deepening
    public Node iterativeDeepening(Node root){
        Node pickNode = root;
        for(int i = 1; i <= maxDepthOfTree; i++){
            pickNode = alphaBetaPruning(root, new Node(false), new Node(true), true, i);
        }
        return pickNode;
    }

    //activation of the null move heuristic
    public Node nullMove(Node root){
        nullMove = true;
        R = 1;
        return alphaBetaPruning(root, new Node(false), new Node(true), true, maxDepthOfTree);
    }

    // method to get the node that the alpha beta pruning found
    public MyCard[] getNodeReturn() {
        //create the tree
        Node parent = tree.getRootNode();
        List<MyCard> currentHand = GametreeAI.cloneMyCardList(parent.hand);
        // pick desired alpha beta method (basicAlphaBeta, iterativeDeepening, nullMove)
        Node pickNode = nullMove(parent);
        for(MyCard card : unknownCards){
            if(!pickNode.unknownCards.contains(card)){
                pickNode.unknownCards.add(card);
            }
        }
        unknownCards = pickNode.unknownCards;
        probMap = pickNode.getProbMap();

        List<MyCard> newHand = GametreeAI.cloneMyCardList(pickNode.hand);
        MyCard pickCard = null;
        for(MyCard card : newHand) {
            if (!currentHand.contains(card)) {
                pickCard = card;
            }
        }

        //loop through newHand to get the new card
        MyCard discardCard = null;

        //if pickCard and discardCard are both null. It means that after simulating the bot does not want to change the hand
        //at current state
        //loop through old hand to get the card be discarded
        for(MyCard card : currentHand) {
            if (!newHand.contains(card)) {
                discardCard = card;
            }
        }
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
    public void  checkDoubles (){
        for(int i = 0; i < unknownCards.size(); i++) {
            if (discardedCards.contains(unknownCards.get(i))||allCards.contains(unknownCards.get(i))) {
                this.unknownCards.remove(unknownCards.get(i));
            }
        }
    }

    //the ai decides to knock or continue
    @Override
    public Boolean knockOrContinue() {
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
        System.out.println("move");
        createTree();
        this.tree.createTree(false);
        MyCard[] pick_discard = this.getNodeReturn();
        backupHand = new ArrayList<>();
        checkDoubles();
        if(pick_discard[0]==null){
            for(int i=0; i<allCards.size();i++){
                backupHand.add(allCards.get(i));
            }
            return true;
        }
        else if(!pick_discard[0].equals(topOfDiscard)){
            for(int i=0; i<allCards.size();i++){
                backupHand.add(allCards.get(i));
            }
            checkDoubles();
            return true;
        }
        else {
            for(int i = 0; i<discardedCards.size();i++){
                if(discardedCards.get(i).same(pick_discard[0])){
                    this.discardedCards.remove(discardedCards.get(i));
                }
            }
            checkDoubles();
            return false;
        }
    }


    /*
    Returns the card that wants to be removed from the current hand
     */
    @Override
    public MyCard discardCard() {
        checkDoubles();
        if(backupHand.size()!=0) {
            for (MyCard card : allCards) {
                if (!backupHand.contains(card)) {

                    for (int i = 0; i < unknownCards.size(); i++) {
                        if (card.suit.index == unknownCards.get(i).suit.index && card.rank.index == unknownCards.get(i).rank.index){
                            unknownCards.remove(unknownCards.get(i));
                        }
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
    }

    public void createTree(){
        this.tree = new GametreeAI(this.discardedCards,this.allCards, this.unknownCards, this.maxDepthOfTree, this.probMap);
    }

    public static List<MyCard> findRemainingCards(List<MyCard> hand, List<MyCard> discardPile){
        List<MyCard> knownCards = new ArrayList<>(hand);
        knownCards.addAll(discardPile);
        List<MyCard> cardList = MyCard.getBasicDeck();

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
        checkDoubles();
        this.discardedCards.add(disCard);
        Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0, this.probMap );
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
            checkDoubles();
            MyCard pickedCard = pickAction.card;
            for(int i = 0; i<discardedCards.size();i++){
                if(discardedCards.get(i).same(pickedCard)){
                    this.discardedCards.remove(discardedCards.get(i));
                }
            }
            Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0, this.probMap );
            this.unknownCards = this.tree.updateProbPickPile(current,pickedCard);
            this.probMap= this.tree.probMap;

        }
        else{
            checkDoubles();
            Node current = new Node(this.discardedCards, this.allCards, this.unknownCards,this.allCards, 0,this.probMap );
            MyCard notChosen = new MyCard(this.discardedCards.get(discardedCards.size()-1));
            this.unknownCards = this.tree.updateProbDiscard(current,notChosen);
            this.probMap= this.tree.probMap;
        }
    }
}
