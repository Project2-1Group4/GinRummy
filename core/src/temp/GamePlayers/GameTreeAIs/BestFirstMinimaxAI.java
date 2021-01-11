package temp.GamePlayers.GameTreeAIs;

//import temp.Extra.GA.GameLogic;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.Entities.HandLayout;
import temp.GamePlayers.GamePlayer;

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
    int maxDepthOfTree = 2;
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
            //System.out.println("max turn!");
            List<Node> evaluationList = new ArrayList<>();

            for (Node child : parents.getChildren()) {
                int eval = child.getHandValue();
                if (eval >= beta.getHandValue()) {
                    //System.out.println("eval and beta value: "+eval + " "+ beta.getHandValue());
                    return child;
                }

                else
                    evaluationList.add(child);

            }
            Collections.sort(evaluationList);
            Collections.reverse(evaluationList);

            if (evaluationList.size() == 1) {
                evaluationList.add(new Node(false));
            }

            if ((alpha.getHandValue() < evaluationList.get(0).getHandValue()) && (evaluationList.get(0).getHandValue() < beta.getHandValue())) {
                //System.out.println("start loop of max");

                //System.out.println("alpha before: "+alpha.getHandValue());

                Node max = Node.getNodeMax(alpha,evaluationList.get(1));
                alpha = max;
                //System.out.println("alpha after: "+alpha.getHandValue());
                Node maxNode = evaluationList.get(0);

                evaluationList.remove(maxNode);

                //System.out.println("switching to min search");
                Node attempt = searching(maxNode, alpha, beta, false);
                //System.out.println("starting for min search");
                //System.out.println("alpha, attempt, beta value of max player: "+ alpha.getHandValue() +" "+attempt.getHandValue()+" "+beta.getHandValue());

                evaluationList.add(attempt);
                Collections.sort(evaluationList);
                Collections.reverse(evaluationList);

            }
            return evaluationList.get(0);
        }
        else {
            //System.out.println("min turn");
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
                //System.out.println("start loop of min");
                //System.out.println("beta before: "+ beta.getHandValue());
                Node min = Node.getNodeMin(beta, evaluationList.get(1));
                beta = min;
                //System.out.println("beta after: "+ beta.getHandValue());

                Node minNode = evaluationList.get(0);
                evaluationList.remove(minNode);

                //System.out.println("switching to max search");
                Node attempt = searching(minNode, alpha, beta, true);
                //System.out.println("starting for max search");
                //System.out.println("alpha, attempt, beta value of min player: "+ alpha.getHandValue() +" "+attempt.getHandValue()+" "+beta.getHandValue());

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
        this.probMap = pickNode.getProbMap();

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


    /*public static void main(String[] args) {


        /*
        List<MyCard> deck = MyCard.getBasicDeck();
        Collections.shuffle(deck);
        List<MyCard> botHand = new ArrayList<>();
        int i = 0;
        while(i<10) {
            botHand.add(deck.get(0));
            deck.remove(0);
            i++;
        }
        List<MyCard> pile = new ArrayList<>();
        pile.add(deck.get(0));
        deck.remove(0);
        List<MyCard> unknownCards = GametreeAI.cloneMyCardList(deck);

        double[][] probMap = new double[4][13];
        for (int j = 0; j<probMap.length; j++) {
            for (int k = 0; k < probMap[0].length; k++) {
                probMap[j][k] = 1.0/41.0;
            }
        }

        GametreeAI tree = new GametreeAI(pile, botHand, unknownCards, 4, probMap);
        System.out.println("starting to create the first tree!!!...");
        tree.createTree(true);
        BestFirstMinimaxAI bot = new BestFirstMinimaxAI(tree);
        Node t = bot.searching(tree.getRootNode(), new Node(false), new Node(true), true);
        System.out.println("hand: "+botHand);
        System.out.println("search result: "+t.hand);

        List<MyCard> player = new ArrayList<MyCard>();
        i = 0;
        Collections.shuffle(deck);
        while(i<10) {
            player.add(deck.get(0));
            deck.remove(0);
            i++;
        }
        System.out.println("deck size: "+deck.size());
        System.out.println("unknown: "+unknownCards.size());

        while (Finder.findBestHandLayout(botHand).getDeadwood() > 10 && Finder.findBestHandLayout(player).getDeadwood() > 10) {
            System.out.println("bot hand: "+ botHand);
            System.out.println("bot deadwood: "+ Finder.findBestHandLayout(botHand).getDeadwood());
            bot.game(player, deck);
        }


    }*/



    public void game(List<MyCard> player, List<MyCard> deck) {
        //bot play
        this.AIPlay(getNodeReturn(), deck);
        //human player
        System.out.println("player turn: ");
        System.out.println("choose card: ");
        Scanner sc = new Scanner(System.in);
        String choose = sc.nextLine();

        List<MyCard> list = new ArrayList<>();
        Node current = new Node(pile, hand, unknownCards, list, 0);
        if (choose.equals("pile")) {
            this.tree.simulationPickPile(current);
            unknownCards = this.tree.cardsUnknown;
            MyCard pick = pile.get(pile.size() - 1);
            player.add(pick);
            pile.remove(pick);
            System.out.println("card pick: "+pick);
        }
        else{
            this.tree.simulationPickDeck(current);
            unknownCards = this.tree.cardsUnknown;
            MyCard pick = deck.get(deck.size() - 1);
            player.add(pick);
        }
        System.out.println("discard: ");
        int discard = sc.nextInt();

        MyCard card = player.get(discard);
        this.tree.simulationDiscard(current, card);
        this.pile.add(card);
        this.unknownCards.remove(card);
        System.out.println("card discard: "+card);
        this.tree.createTree(false);
    }

    public void AIPlay(MyCard[] pick_discard, List<MyCard> deck) {
        if (pick_discard[0] == null || !pick_discard[0].equals(pile.get(0))) {
            MyCard pick = deck.get(deck.size() - 1);
            this.hand.add(pick);
            deck.remove(pick);
            MyCard discard = GametreeAI.chooseCardToDiscard(this.hand);
            this.hand.remove(discard);
            pile.add(discard);
            this.unknownCards.remove(pick);
            System.out.println("actual pick and discard: "+pick+" "+discard);
        }
        else {
            this.hand.add(pick_discard[0]);
            this.hand.remove(pick_discard[1]);
            this.pile.add(pick_discard[1]);
            System.out.println("actual pick and discard: "+pick_discard[0]+" "+pick_discard[1]);
        }
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
        System.out.println("move");
        createTree();
        //tree simulations
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

    public void update(HandLayout realLayout) {
        super.update(realLayout.cards());
    }

    public void createTree(){
        this.tree = new GametreeAI(this.discardedCards,this.allCards, this.unknownCards, this.maxDepthOfTree, this.probMap);
    }

    public List<MyCard> findRemainingCards(List<MyCard> hand, List<MyCard> discardPile){
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
}

