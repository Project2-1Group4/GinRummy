package temp.GamePlayers.GameTreeAIs;

import temp.Extra.GA.GameLogic;
import temp.GameLogic.*;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GamePlayers.GamePlayer;

import java.util.*;

//the structure, parameters are the same as depth first search so I will reuse all basic methods from depth minimax
public class BestFirstMinimaxAI extends GamePlayer {
    GametreeAI tree;
    //boolean AITurn;

    public List<MyCard> hand;
    public List<MyCard> pile;
    public List<MyCard> unknownCards;
    //boolean playerKnock = false;
    //boolean AIknock = false;
    //static int round = 0;
    //public static SetOfCards deck;
    int maxDepthOfTree = 3;
    private List<MyCard> backupHand;
    double[][] probMap = new double[4][13];


    public List<MyCard> discardedCards = new ArrayList<>();

    public BestFirstMinimaxAI(GametreeAI tree) {
        this.tree = tree;
        this.hand = tree.hand;
        this.pile = tree.discardPile;
        this.unknownCards = tree.cardsUnknown;
    }

    public BestFirstMinimaxAI() {
        super();
    }

    /*
    public double newEvaluation(Node nodeChecking) {
        List<MyCard> attemptHand = nodeChecking.hand;
        //get deadwood value from hand
        double scoreHand = Finder.findBestHandLayout(attemptHand).getDeadwood();

        //get possible new hand cards
        MyCard[] pick_discard = this.pick_discard(nodeChecking.hand);
        MyCard pickCard = pick_discard[0];

        //get probability of each card
        double cardPickedProb = this.probMap[pickCard.suit.index][pickCard.rank.index];

        double finalValue = scoreHand + cardPickedProb*100;
        if (finalValue == scoreHand)
            return 100000;  //pick card prob = 0 => impossible move => wont be chosen
        else
            return finalValue;
    }



    public MyCard[] pick_discard(List<MyCard> newAttemptHand) {

        //card pick
        MyCard pickCard = null;
        for (MyCard card : newAttemptHand) {
            if (!this.hand.contains(card)) {
                pickCard = card;
            }
        }
        //card discard
        MyCard discardCard = null;
        for (MyCard card : this.hand) {
            if (!newAttemptHand.contains(card)) {
                discardCard = card;
            }
        }
        return new MyCard[] {pickCard, discardCard};
    }

     */
    /*
    public Node searching(Node parents, Node alpha, Node beta, boolean maxPlayer) {
        if ((parents.getChildren().size() == 0) || parents.playerStop || parents.AIStop) {
            return parents;
        }
        if (maxPlayer) {
            //increasing map
            TreeMap<Integer, Node> evaluationMap = new TreeMap<>();

            for (Node child : parents.getChildren()) {
                System.out.println("hand card: "+child.hand);
                int eval = child.getHandValue();
                if (eval > beta.getHandValue()) {
                    System.out.println("eval and beta value: "+eval + " "+ beta.getHandValue() );
                    return child;
                }
                else {
                    evaluationMap.put(eval, child);
                    System.out.println("blabla");
                }

            }
            NavigableMap evaluationMapDesc = evaluationMap.descendingMap();

            List<Node> evalNodeList = new ArrayList<>(evaluationMapDesc.values());
            System.out.println("children size: "+evalNodeList.size());
            if (evalNodeList.size() == 1) {
                evalNodeList.add(new Node(false));
            }
            System.out.println("children size after if statement: "+evalNodeList.size());
            while (alpha.getHandValue() <= evalNodeList.get(0).getHandValue()
                    && evalNodeList.get(0).getHandValue() <= beta.getHandValue()) {
                alpha = Node.getNodeMax(alpha, evalNodeList.get(1));
                Node attempt = searching(evalNodeList.get(0), alpha, beta, false);

                evaluationMap.put(attempt.getHandValue(), attempt);
                evaluationMapDesc = evaluationMap.descendingMap();

                evalNodeList = new ArrayList<>(evaluationMapDesc.values());
            }
            return evalNodeList.get(0);
        }
        else {
            //increasing map
            Map<Integer, Node> evaluationMap = new TreeMap<>();

            System.out.println("children size: "+parents.getChildren().size());
            for (Node child : parents.getChildren()) {
                System.out.println("hand card: "+child.hand);
                int eval = child.getHandValue();
                if (eval < alpha.getHandValue()) {
                    System.out.println("eval and alpha value: "+eval + " "+ alpha.getHandValue() );
                    return child;
                }
                else
                    evaluationMap.put(eval, child);

            }

            System.out.println("hashmap size: "+evaluationMap.size());

            List<Node> evalNodeList = new ArrayList<>(evaluationMap.values());
            System.out.println("children size: "+evalNodeList.size());
            if (evalNodeList.size() == 1) {
                evalNodeList.add(new Node(true));
            }
            System.out.println("children size after if statement: "+evalNodeList.size());
            while (alpha.getHandValue() <= evalNodeList.get(0).getHandValue()
                    && evalNodeList.get(0).getHandValue() <= beta.getHandValue()) {
                beta = Node.getNodeMin(beta, evalNodeList.get(1));
                Node attempt = searching(evalNodeList.get(0), alpha, beta, true);

                evaluationMap.put(attempt.getHandValue(), attempt);

                evalNodeList = new ArrayList<>(evaluationMap.values());
            }
            return evalNodeList.get(0);
        }
    }

     */

    public Node searching(Node parents, Node alpha, Node beta, boolean maxPlayer) {
        if ((parents.getChildren().size() == 0) || parents.playerStop || parents.AIStop) {
            return parents;
        }
        if (maxPlayer) {
            List<Node> evaluationList = new ArrayList<>();

            for (Node child : parents.getChildren()) {
                System.out.println("hand card: "+child.hand);
                int eval = child.getHandValue();
                if (eval > beta.getHandValue()) {
                    System.out.println("eval and beta value: "+eval + " "+ beta.getHandValue());
                    return child;
                }
                else {
                    evaluationList.add(child);
                }

            }
            Collections.sort(evaluationList);
            //decreasing list
            Collections.reverse(evaluationList);

            System.out.println("children size: "+evaluationList.size());
            if (evaluationList.size() == 1) {
                evaluationList.add(new Node(false));
            }
            System.out.println("children size after if statement: "+evaluationList.size());
            while (alpha.getHandValue() <= evaluationList.get(0).getHandValue()
                    && evaluationList.get(0).getHandValue() <= beta.getHandValue()) {
                alpha = Node.getNodeMax(alpha, evaluationList.get(1));
                Node attempt = searching(evaluationList.get(0), alpha, beta, false);
                System.out.println("alpha, attempt, beta value of max player: "+ alpha.getHandValue() +" "+attempt.getHandValue()+" "+beta.getHandValue());
                evaluationList.remove(evaluationList.get(0));
                evaluationList.add(attempt);
                Collections.sort(evaluationList);
                Collections.reverse(evaluationList);
            }
            return evaluationList.get(0);
        }
        else {
            //increasing list
            List<Node> evaluationList = new ArrayList<>();

            System.out.println("children size: "+parents.getChildren().size());
            for (Node child : parents.getChildren()) {
                int eval = child.getHandValue();
                if (eval < alpha.getHandValue()) {
                    System.out.println("eval and alpha value: "+eval + " "+ alpha.getHandValue());
                    return child;
                }
                else {
                    evaluationList.add(child);
                }
            }

            System.out.println("list size: "+evaluationList.size());

            Collections.sort(evaluationList);

            if (evaluationList.size() == 1) {
                evaluationList.add(new Node(true));
            }

            System.out.println("children size after if statement: "+evaluationList.size());
            while (alpha.getHandValue() <= evaluationList.get(0).getHandValue()
                    && evaluationList.get(0).getHandValue() <= beta.getHandValue()) {
                beta = Node.getNodeMin(beta, evaluationList.get(1));
                Node attempt = searching(evaluationList.get(0), alpha, beta, true);
                System.out.println("alpha, attempt, beta value of min player: "+ alpha.getHandValue() +" "+attempt.getHandValue()+" "+beta.getHandValue());
                evaluationList.remove(evaluationList.get(0));
                evaluationList.add(attempt);
                Collections.sort(evaluationList);
            }
            return evaluationList.get(0);
        }
    }

    public MyCard[] getNodeReturn() {
        //create the tree
        Node parent = tree.getRootNode();

        List<MyCard> currentHand = GametreeAI.cloneMyCardList(parent.hand);
        System.out.println("current bot hand: "+currentHand);
        System.out.println("deadwood = "+handLayout.getDeadwood());
        Node pickNode = searching(parent, new Node(false), new Node(true), true);
        //System.out.println("pick node (hoping hand): "+pickNode);


        for(MyCard card : unknownCards){
            if(!pickNode.unknownCards.contains(card)){

                pickNode.unknownCards.add(card);
            }
        }

        unknownCards = pickNode.unknownCards;
        probMap = pickNode.getProbMap();
        discardedCards = pickNode.discardPile;

        List<MyCard> newHand = GametreeAI.cloneMyCardList(pickNode.hand);
        MyCard pickCard = null;
        for(MyCard card : newHand) {
            if (!currentHand.contains(card)) {
                pickCard = card;
            }
        }

        MyCard discardCard = null;

        for(MyCard card : currentHand) {
            if (!newHand.contains(card)) {
                discardCard = card;
            }
        }

        return new MyCard[] {pickCard, discardCard};
    }


    public static void main(String[] args) {

        GameLogic g = new GameLogic(true, true);
        g.play(new BestFirstMinimaxAI(), new BestFirstMinimaxAI(), 0);



        //List<Integer> list = new ArrayList<Integer>();
        //list.add(1); list.add(2); list.add(-1);
    }


    public void  checkDoubles (){
        for(int i = 0; i < unknownCards.size(); i++) {
            if (discardedCards.contains(unknownCards.get(i))||allCards.contains(unknownCards.get(i))) {
                this.unknownCards.remove(unknownCards.get(i));
            }
        }
    }
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

