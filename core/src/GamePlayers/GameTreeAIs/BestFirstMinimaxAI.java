package GamePlayers.GameTreeAIs;

//import temp.Extra.GA.GameLogic;
import GameLogic.Entities.MyCard;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.HandLayout;
import GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

//the structure, parameters are the same as depth first search so I will reuse all basic methods from depth minimax
public class BestFirstMinimaxAI extends GamePlayer {
    GametreeAI tree;

    public List<MyCard> hand;
    public List<MyCard> pile;
    public List<MyCard> unknownCards;
    int maxDepthOfTree = 5;
    private List<MyCard> backupHand;
    double[][] probMap = new double[4][13];

    public List<MyCard> discardedCards = new ArrayList<>();

    public BestFirstMinimaxAI(GametreeAI tree) {
        this.tree = tree;
        this.hand = tree.hand;
        this.pile = tree.discardPile;
        this.unknownCards = tree.cardsUnknown;
        this.probMap = tree.probMap;
    }

    public BestFirstMinimaxAI() {
        super();
    }



    public Node searching(Node parents, Node alpha, Node beta, boolean maxPlayer) {
        if ((parents.getChildren().size() == 0) || parents.playerStop || parents.AIStop) {
            return parents;
        }
        if (maxPlayer == true) {
            List<Node> evaluationList = new ArrayList<>();

            for (Node child : parents.getChildren()) {
                int eval = child.getHandValue();
                if (eval >= beta.getHandValue()) {
                    return child;
                }

                else
                    evaluationList.add(child);

            }
            Collections.sort(evaluationList);
            //decreasing order
            Collections.reverse(evaluationList);

            if (evaluationList.size() == 1) {
                evaluationList.add(new Node(false));
            }

            if ((alpha.getHandValue() < evaluationList.get(0).getHandValue()) && (evaluationList.get(0).getHandValue() < beta.getHandValue())) {


                Node max = Node.getNodeMax(alpha,evaluationList.get(1));
                alpha = max;
                Node maxNode = evaluationList.get(0);

                evaluationList.remove(maxNode);
                //search for the best next node
                Node attempt = searching(maxNode, alpha, beta, false);

                evaluationList.add(attempt);
                Collections.sort(evaluationList);
                Collections.reverse(evaluationList);

            }
            //return the first item of the list
            return evaluationList.get(0);
        }
        else {
            //increasing list
            List<Node> evaluationList = new ArrayList<>();

            for (Node child : parents.getChildren()) {
                int eval = child.getHandValue();
                if (eval <= alpha.getHandValue()) {
                    return child;
                }

                else
                    evaluationList.add(child);

            }

            Collections.sort(evaluationList);


            if (evaluationList.size() == 1) {
                evaluationList.add(new Node(true));
            }
            if ((alpha.getHandValue() < evaluationList.get(0).getHandValue()) && (evaluationList.get(0).getHandValue() < beta.getHandValue())) {

                Node min = Node.getNodeMin(beta, evaluationList.get(1));
                beta = min;

                Node minNode = evaluationList.get(0);
                evaluationList.remove(minNode);

                Node attempt = searching(minNode, alpha, beta, true);

                evaluationList.add(attempt);
                Collections.sort(evaluationList);
            }
            return evaluationList.get(0);
        }
    }

    public MyCard[] getNodeReturn() {
        //create the tree
        Node parent = tree.getRootNode();
        List<MyCard> currentHand = parent.hand;
        Node pickNode = this.searching(parent, new Node(false), new Node(true), true);
        for(MyCard card : unknownCards){
            if(!pickNode.unknownCards.contains(card)){
                pickNode.unknownCards.add(card);
            }
        }
        unknownCards = pickNode.unknownCards;
        probMap = pickNode.getProbMap();

        List<MyCard> newHand = pickNode.hand;
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
        if (this.handLayout.deadwoodValue() <= 10){
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
        //System.out.println("move");
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
    public void update(List<MyCard> realLayout) {
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
            MyCard pickedCard = pickAction.card();
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

