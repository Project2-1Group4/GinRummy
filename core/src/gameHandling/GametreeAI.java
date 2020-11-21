package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

public class GametreeAI {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards cardsUnknown;
    private SetOfCards opponentHand;
    Player player;
    private int leftInUnknownSet = 4;
    private int leftInUnknownRun = 2;

    int test = 0;
    boolean playerPick = false;
    //private HashMap<MyCard, double>;


    public  GametreeAI (SetOfCards pile, SetOfCards cards, SetOfCards deck){
        System.out.println("constructor");
        this.discardPile = pile;
        this.hand = cards;
        System.out.println(hand.size());
        this.cardsUnknown = deck;

        opponentHand = new SetOfCards();
        for(int i = 0; i< 10; i++){
            opponentHand.addCard(cardsUnknown.getCard(i));
        }

        System.out.println("total cards: "+ hand.size() +" " +discardPile.size() + " "+cardsUnknown.size());
    }

    public void createTree (){
        Node first = new Node(discardPile, hand, cardsUnknown, opponentHand);
        Node pass = first.addChild(new Node(discardPile,hand,cardsUnknown, opponentHand));
        createNodesOpponent(pass, true);
        Node discard1 = first.addChild(pickDiscard(hand,discardPile));
        createNodesOpponent(discard1, false);
    }

    public Node getParentNode() {
        return new Node(this.discardPile, this.hand, this.cardsUnknown, this.opponentHand);
    }

    // we need to determine stop statement for recursion
    // firstRound true if opponent can still pass
    public void createNodesOpponent(Node parent, boolean firstRound){
        if(parent.winOrLose){
            System.out.print("end creating children");
        }
        else{
            // makes nodes for if opponent picks from discard pile
            simulationPick(true, parent);
            List<Node> nodesPile = monteCarloSim(true);
            for(int i = 0; i< nodesPile.size(); i++){
                parent.addChild(nodesPile.get(i));
                if(test < 2){
                    System.out.println("node " + i);
                    SetOfCards opp = nodesPile.get(i).opponentHand;
                    for(int k = 0; k< opp.size(); k++ ) {
                        System.out.println(opp.getCard(k).getProb());
                    }
                }
                simulationDiscard(nodesPile.get(i));
                List<Node> nodesDiscard1 = monteCarloSim(false);
                for(int j = 0; j< nodesDiscard1.size(); j++){
                    nodesPile.get(i).addChild(nodesDiscard1.get(i));

                    System.out.println("node " + i);
                    SetOfCards opp = nodesPile.get(i).opponentHand;
                    for(int k = 0; k< opp.size(); k++ ) {
                        System.out.println("prob: "+opp.getCard(k).getProb());
                    }

                    createNodesAI(nodesDiscard1.get(j));
                }
                test++;
            }

            // makes nodes for is player picks deck or for the first round passes
            simulationPick(false, parent);
            List<Node> nodesDeck;
            if(firstRound){
                nodesDeck = monteCarloSim(false);
            }
            else{
                nodesDeck =  monteCarloSim(true);
            }
            for(int i = 0; i< nodesDeck.size(); i++){
                parent.addChild(nodesDeck.get(i));
                if(!firstRound){
                    simulationDiscard(nodesDeck.get(i));
                    List<Node> nodesDiscard2 = monteCarloSim(false);
                    for(int j = 0; j< nodesDiscard2.size(); j++){
                        nodesDeck.get(j).addChild(nodesDiscard2.get(i));
                        createNodesAI(nodesDiscard2.get(j));
                    }
                }
            }
        }
    }

    // we need to determine stop statement for recursion
    public void createNodesAI(Node parent){
        if(parent.winOrLose){
            System.out.print("end creating children");
        }
        else{
            discardPile = parent.discardPile;
            cardsUnknown = parent.unknownCards;
            hand = parent.hand;
            opponentHand = parent.opponentHand;
            Card topPile = parent.discardPile.getCard(discardPile.size()-1);
            if(evaluate(topPile, parent.hand)){
                hand.addCard(topPile);
                discardPile.discardCard(topPile);
                Card discard = chooseCardToDiscard(hand.toList());
                hand.discardCard(discard);
                discardPile.addCard(discard);
                Node child = new Node(discardPile, hand, cardsUnknown, opponentHand);
                parent.addChild(child);
                createNodesOpponent(child, false);
            }
            else{
                List<Card> deck = makeDeck(cardsUnknown.toList(), opponentHand.toList());
                for(int i = 0; i< deck.size(); i++){
                    hand.addCard(deck.get(i));
                    Card discard = chooseCardToDiscard(hand.toList());
                    hand.discardCard(discard);
                    discardPile.addCard(discard);
                    Node child = new Node(discardPile, hand, cardsUnknown, opponentHand);
                    parent.addChild(child);
                    createNodesOpponent(child, false);
                }

            }
        }

    }

    public List<Card> makeDeck(List<Card> unknown, List<Card> opponent){
        List<Card> deck = Player.copyList(unknown);
        for(int i = 0; i<opponent.size(); i++){
            deck.remove(opponent.get(i));
        }
        return deck;
    }



    public Node pickDiscard(SetOfCards current, SetOfCards discardPile){
        SetOfCards copyCards = new SetOfCards(current.toList());
        SetOfCards copyDiscard = new SetOfCards(discardPile.toList());

        copyCards.addCard(discardPile.getCard(discardPile.size()-1));
        copyDiscard.discardCard(discardPile.getCard(discardPile.size()-1));

        List<Card> copyList = copyCards.toList();
        Card discardCard = chooseCardToDiscard(copyList);
        copyCards.discardCard(discardCard);
        copyDiscard.addCard(discardCard);
        Node result = new Node(copyDiscard, copyCards, cardsUnknown, opponentHand);
        return result;
    }

    // boolean true if discard pile is chosen
    // if true, top card of pile will be transferred after this method
    public void simulationPick(boolean pileOrDeck, Node parent){
        // if opponent picks card from pile
        discardPile = parent.discardPile;
        cardsUnknown = parent.unknownCards;
        hand = parent.hand;
        opponentHand = new SetOfCards();

        if(pileOrDeck){
            Card chosen = discardPile.getCard((discardPile.size()-1));
            // card must be in opponents hand

            // look through known cards to see how many usefull one (melts) are left in unknown setofcards
            lookThroughKnownCards(chosen);
            // update prob of unknown cards
            double probSpecial = 0 ;
            for(int j = 0; j<cardsUnknown.size(); j++) {
                double setProb = 0;
                double runProb = 0;
                // increase prob for cards that form set with chosen card
                if (cardsUnknown.getCard(j).getValue() == chosen.getValue()) {
                    //BAYES RULEE!! change this!!
                    if (leftInUnknownSet > 0) {
                        setProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownSet));
                    }
                    else {
                        setProb = 0.0;
                    }
                    cardsUnknown.getCard(j).setProb(setProb);
                    System.out.println(setProb);
                    probSpecial += setProb;
                }
                // increase prob for cards that form run with chosen card
                else if (cardsUnknown.getCard(j).getSuit() == chosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - chosen.getValue()) == 1) {
                    // BAYES RULEE!! change this!
                    System.out.println("start Prob " + cardsUnknown.getCard(j).getProb());
                    System.out.println("left in run " + leftInUnknownRun);
                    if (leftInUnknownRun > 0) {
                        runProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownRun));
                    }
                    else {
                        runProb = 0.0;
                    }
                    //System.out.println(1.0 / (2.0 * leftInUnknownRun));
                    cardsUnknown.getCard(j).setProb(runProb);
                    System.out.println("run Prob " + runProb);
                    probSpecial += runProb;
                }
            }
            // calculate the probability of the remaining cards
            int size = cardsUnknown.size() - leftInUnknownRun - leftInUnknownSet;
            //double leftProb = 1.0-probSpecial;
            //double unknownProb;
            double newUnknownProb;
            // update prob of cards that don't form melts with chosen card
            for(int j = 0; j<cardsUnknown.size(); j++) {
                if (cardsUnknown.getCard(j).getValue() != chosen.getValue() && !(cardsUnknown.getCard(j).getSuit() == chosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - chosen.getValue()) == 1)) {
                    //unknownProb = leftProb/size;
                    newUnknownProb = cardsUnknown.getCard(j).getProb();

                    cardsUnknown.getCard(j).setProb(newUnknownProb);
                }
            }
            chosen.setProb(1.0);
            cardsUnknown.addCard(chosen);
            discardPile.discardCard(chosen);
        }
        // if opponent picks card from deck and therefore doesn't use card from pile
        else{

            Card notChosen = discardPile.getCard(discardPile.size()-1);
            lookThroughKnownCards(notChosen);
            double probSpecial = 0 ;
            for(int j = 0; j< cardsUnknown.size(); j++){
                double setProb = 0;
                double runProb = 0;
                // decrease prob of cards that form set with not chosen card
                if(cardsUnknown.getCard(j).getValue() == notChosen.getValue()){
                    // Bayes rule
                    if (leftInUnknownSet > 0) {
                        setProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownSet));
                    }
                    else {
                        setProb = 0.0;
                    }
                    cardsUnknown.getCard(j).setProb(setProb);
                    probSpecial += setProb;
                }
                // decrease prob of cards that form run with chosen card
                if(cardsUnknown.getCard(j).getSuit() == notChosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - notChosen.getValue()) == 1){
                    // Bayes rule
                    if (leftInUnknownRun > 0) {
                        runProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownRun));
                    }
                    else {
                        runProb = 0.0;
                    }
                    cardsUnknown.getCard(j).setProb(runProb);
                    probSpecial += runProb;
                }
            }
            int size = cardsUnknown.size() - leftInUnknownRun - leftInUnknownSet;
            //double leftProb = 1.0 -probSpecial;
            //double unknownProb;
            double newUnknownProb;
            for(int j = 0; j<cardsUnknown.size(); j++) {
                if (cardsUnknown.getCard(j).getValue() != notChosen.getValue() && !(cardsUnknown.getCard(j).getSuit() == notChosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - notChosen.getValue()) == 1)) {
                    //unknownProb = leftProb/size;
                    newUnknownProb = cardsUnknown.getCard(j).getProb();
                    cardsUnknown.getCard(j).setProb(newUnknownProb);
                }

            }
        }
    }

    public void simulationDiscard(Node parent){

        discardPile = parent.discardPile;
        cardsUnknown = parent.unknownCards;
        hand = parent.hand;
        opponentHand = parent.opponentHand;
        System.out.println("total cards: "+ hand.size() + " "+discardPile.size() +" "+ cardsUnknown.size());
        Card discard = chooseCardToDiscard(parent.opponentHand.toList());
        discardPile.addCard(discard);
        if(cardsUnknown.discardCard(discard)) {
            System.out.println("Card discard: "+discard);
        }
        else {
            System.out.println("Doesnt discard");
        }
        System.out.println("total cards: "+ hand.size() + " "+discardPile.size() +" "+ cardsUnknown.size());
        opponentHand.discardCard(discard);
        double probSpecial = 0 ;
        lookThroughKnownCards(discard);
        // OPPONENT DISCARDS CARD
        for(int j = 0; j<cardsUnknown.size(); j++){
            double setProb = 0.0;
            double runProb = 0.0;
            // decrease prob of cards that form set with discarded card
            if(cardsUnknown.getCard(j).getValue() == discard.getValue()){
                if (leftInUnknownSet > 0) {
                    setProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownSet));
                }
                else {
                    setProb = 0.0;
                }
                cardsUnknown.getCard(j).setProb(setProb);
                probSpecial += setProb;;
            }
            // decrease prob of cards that form run with discarded card
            if(cardsUnknown.getCard(j).getSuit() == discard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discard.getValue()) == 1){
                if (leftInUnknownRun > 0) {
                    runProb = cardsUnknown.getCard(j).getProb()*(1.0 / (2.0 * leftInUnknownRun));
                }
                else {
                    runProb = 0.0;
                }
                cardsUnknown.getCard(j).setProb(runProb);
                probSpecial += runProb;
            }

        }
        int size = cardsUnknown.size() - leftInUnknownRun - leftInUnknownSet;
        //double leftProb = 1-probSpecial;
        //double unknownProb;
        double newUnknownProb;
        for(int j = 0; j<cardsUnknown.size(); j++) {
            if (cardsUnknown.getCard(j).getValue() != discard.getValue() && !(cardsUnknown.getCard(j).getSuit() == discard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discard.getValue()) == 1)) {
                //unknownProb = leftProb/size;
                newUnknownProb = cardsUnknown.getCard(j).getProb();
                System.out.println("new unknown prob: "+ newUnknownProb);
                cardsUnknown.getCard(j).setProb(newUnknownProb);
            }

        }

        System.out.println("total cards: "+ hand.size() + " "+discardPile.size() +" "+ cardsUnknown.size());
    }


    // pickOrDiscard variable to indicate if you need to create hand of 10 or 11 cards, true if 11 (= picking process)
    public List<Node> monteCarloSim(boolean pickOrDiscard){
        List<Node> nodes = new ArrayList<>();
        List<Card> opponentHandcur;
        // simulate 100 times
        for(int i= 1; i<= 10; i++){
            if(pickOrDiscard){
                opponentHandcur = chooseRandomCards(cardsUnknown.toList(), 11);
            }
            else{
                opponentHandcur = chooseRandomCards(cardsUnknown.toList(), 10);
            }
            SetOfCards opponent = new SetOfCards(false, false);
            for(int j= 0; j< opponentHandcur.size(); j++){
                opponent.addCard(opponentHandcur.get(j));
            }
            Node node = new Node(discardPile,hand, cardsUnknown, opponent);
            nodes.add(node);
        }
        return nodes;
    }



    // method that looks in known cards (hand + discardpile) for usefull cards
    public void lookThroughKnownCards(Card chosen){
        // 4 suits
        leftInUnknownSet = 3;

        // if chosen card is Ace or King you only have 1 'neighbour' for run
        if(chosen.getValue() == 1 || chosen.getValue() == 13){
            leftInUnknownRun = 1;
        }
        else{
            leftInUnknownRun = 2;
        }

        for(int k = 0; k< hand.size(); k++){
            // look through hand for cards that form set with given card
            if(hand.getCard(k).getValue() == chosen.getValue() && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            // look through hand for cards that form run with given card
            if(hand.getCard(k).getSuit() == chosen.getSuit() && Math.abs(hand.getCard(k).getValue() - chosen.getValue()) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }
        // look through pile, same as hand
        for(int k = 0; k< discardPile.size(); k++){
            if(discardPile.getCard(k).getValue() == chosen.getValue() && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            if(discardPile.getCard(k).getSuit() == chosen.getSuit() && Math.abs(discardPile.getCard(k).getValue() - chosen.getValue()) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }

        System.out.println("unknown run: "+leftInUnknownRun);
        System.out.println("unknown set: "+leftInUnknownSet);

    }


    public static Card chooseCardToDiscard(List<Card> aHand){
        Card theCard = null;


        // starting value of a hand
        int highestVal = Player.scoreHand(aHand);

        for(Card aCard : aHand){
            // TODO: Bug-test here to make sure the copies are deep copies and not shallow
            //List<Card> aList = new ArrayList<>(aHand);
            //deep copy aList (method is in Player class already)
            List<Card> aList = Player.copyList(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now

            int resultingHand = Player.scoreHand(aList);

            //we should consider the card that not in "potential melds" not in entire deadwood. Not sure we have this method already or not xD
            if(resultingHand <= highestVal){    //the result from scoreHand is counting deadwood value so it should be smaller than the previous step
                theCard = aCard;
                highestVal = resultingHand;
            }
        }
        return theCard;
    }


    public static List<Card> chooseRandomCards(List<Card> totalCards, int size){
        List<Card> copyList = Player.copyList(totalCards);

        List<Card> resultList = new ArrayList<>();
        for(int i = 0; i < copyList.size();i++){
            // Might be changed to is greater than or equals
            if(copyList.get(i).getProb() == 1){
                resultList.add(copyList.get(i));
                //System.out.println("card remove : "+copyList.get(i));
                copyList.remove(copyList.get(i));
            }
        }
        //System.out.println();

        while(resultList.size() < size){

            Card toSave = pickRandomCard(copyList);
            if(toSave != null) {
                resultList.add(toSave);
                //System.out.println("card remove : "+toSave);
                copyList.remove(toSave);
            }
            // TODO: Check to make sure that this method doesn't affect the original list of cards

        }
        return resultList;
    }

    public static double calcTotalProb(List<Card> setOfCards){
        double val = 0.0;

        for(Card aCard: setOfCards){
            val+=aCard.getProb();
        }
        return val;

    }

    public static Card pickRandomCard(List<Card> setOfCard){
        double val = calcTotalProb(setOfCard);

        double objective = randomNumberGenerator(0,val);

        double curVal = 0;
        Card finCard = null;

        for(Card aCard: setOfCard){
            curVal += aCard.getProb();

            if(curVal >= objective){
                finCard = aCard;
                break;
            }

        }

        return finCard;

    }

    public static double randomNumberGenerator(double min, double max){
        return (Math.random()*(max-min+1)+min);
    }

    // returns true if you want to pick from discard pile
    public boolean evaluate(Card discardCard, SetOfCards hand){
        List<Card> current = Player.copyList(hand.toList());
        current.add(discardCard);
        if(chooseCardToDiscard(current) == discardCard){
            return false;
        }
        else{
            return true;
        }
    }

    //demo pruning algo
    public int alphabetaPruning(Node node, int alpha, int beta, boolean maximizingPlayer ) {
        if ((node.getChildren().size() == 0) || !node.playerStop || !!node.AIStop)
            return node.handValue;
        if (maximizingPlayer) {
            int maxEval = -100000; // negative infinity
            for (Node child : node.getChildren()) {
                //update tree here
                int eval = alphabetaPruning(child, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        }
        else {
            int minEval = 100000; //positive infinity
            for (Node child : node.getChildren()) {
                int eval = alphabetaPruning(child, alpha, beta, true);
                minEval = Math.min(beta, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer) {
        if ((node.getChildren().size() == 0) || !node.playerStop || !!node.AIStop) {
            return node;
        }

        if (maxPlayer) {
            Node maxNode = new Node(false); //node with negative inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, false);
                maxNode = Node.getNodeMax(alpha, evalNode);
                alpha = Node.getNodeMax(alpha, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return maxNode;
        }
        else {
            Node minNode = new Node(true); // node with positive inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, true);
                minNode = Node.getNodeMin(beta, evalNode);
                beta = Node.getNodeMin(beta, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return minNode;
        }
    }

    public static void main(String[] args){
        SetOfCards deck = new SetOfCards(true, false);
        SetOfCards hand = new SetOfCards(false, false);
        for(int i = 0; i < 10; i++){
            Card aCard = deck.drawTopCard();
            hand.addCard(aCard);
        }
        SetOfCards pile = new SetOfCards(false, false);
        Card discardCard = deck.drawTopCard();
        pile.addCard(discardCard);
        GametreeAI AI = new GametreeAI(pile, hand,deck);
        AI.createTree();

        System.out.println("Hand: "+AI.getParentNode().hand);
    }


}