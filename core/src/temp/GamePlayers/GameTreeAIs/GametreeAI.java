package temp.GamePlayers.GameTreeAIs;

import cardlogic.Card;
import cardlogic.SetOfCards;
import gameHandling.Player;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;

import java.util.*;

public class GametreeAI {
    public SetOfCards discardPile;
    public SetOfCards hand;
    public SetOfCards cardsUnknown;
    public SetOfCards opponentHand;
    private int leftInUnknownSet = 4;
    private int leftInUnknownRun = 2;
    private int depthTree = 0;
    private int maxDepth;
    private Node root;

    public  GametreeAI (SetOfCards pile, SetOfCards cards, SetOfCards deck, int maxDepth){
        this.discardPile = pile;
        this.hand = cards;
        this.cardsUnknown = deck;
        opponentHand = new SetOfCards();
        for(int i = 0; i< 10; i++){
            opponentHand.addCard(cardsUnknown.getCard(i));
        }
        this.maxDepth = maxDepth;
        root = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree);
    }
    //If first round is true then generate first round rules
    public void createTree (boolean firstRound){
        if(firstRound) {
            copyParent(root);
            Node pass = root.addChild(new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree));
            copyParent(root);
            Node discard1 = root.addChild(pickDiscard(hand, discardPile));
            createNodesOpponent(pass, true);
            createNodesOpponent(discard1, false);
        }
        else{
            copyParent(root);
            Node deck = root.addChild(pickDeck(hand,cardsUnknown,discardPile));
            copyParent(root);
            Node discard1 = root.addChild(pickDiscard(hand, discardPile));
            createNodesOpponent(deck, false);
            createNodesOpponent(discard1, false);
        }
    }


    public Node getRootNode() {
        //copyParent(root);
        Node copyRoot = new Node(this.discardPile, this.hand, this.cardsUnknown, this.opponentHand, 0);
        for(Node child : root.getChildren()){
            copyRoot.addChild(child);
        }
        return root;
    }

    // we need to determine stop statement for recursion
    // firstRound true if opponent can still pass
    public void createNodesOpponent(Node parent, boolean firstRound){
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){

        }
        else{

            // makes nodes for if opponent picks from discard pile
            simulationPickPile(parent);
            List<Node> nodesPile = monteCarloSim(true);
            for(int i = 0; i< nodesPile.size(); i++){
                parent.addChild(nodesPile.get(i));
                Card discard = chooseCardToDiscard(nodesPile.get(i).opponentHand.toList());
                simulationDiscard(nodesPile.get(i), discard);
                List<Node> nodesDiscard1 = monteCarloSim(false);
                for(int j = 0; j< nodesDiscard1.size(); j++){
                    nodesPile.get(i).addChild(nodesDiscard1.get(i));
                    createNodesAI(nodesDiscard1.get(j));
                }
            }
            // makes nodes for is player picks deck or for the first round passes
            simulationPickDeck(parent);
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
                    Card discard = chooseCardToDiscard(nodesDeck.get(i).opponentHand.toList());
                    simulationDiscard(nodesDeck.get(i), discard);
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
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){

        }
        else{
            copyParent(parent);
            Card topPile = discardPile.getCard(discardPile.size()-1);
            if(evaluate(topPile, parent.hand)){
                hand.addCard(topPile);
                discardPile.discardCard(topPile);
                Card discard = chooseCardToDiscard(hand.toList());
                hand.discardCard(discard);
                discardPile.addCard(discard);
                Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree);
                parent.addChild(child);
                createNodesOpponent(child, false);
            }
            else{

                List<Card> deck = makeDeck(cardsUnknown.toList(), opponentHand.toList());
                SetOfCards copyHand = new SetOfCards(hand.toList());
                SetOfCards copyCardUnknown = new SetOfCards(cardsUnknown.toList());
                SetOfCards copyPile = new SetOfCards(discardPile.toList());

                for(int i = 0; i< deck.size(); i++){
                    hand.addCard(deck.get(i));
                    cardsUnknown.discardCard(deck.get(i));
                    Card discard = chooseCardToDiscard(hand.toList());
                    hand.discardCard(discard);
                    discardPile.addCard(discard);
                    Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree);
                    parent.addChild(child);
                    createNodesOpponent(child, false);
                    //reset setofcards for next possible card in deck
                    hand = copyHand;
                    cardsUnknown = copyCardUnknown;
                    discardPile = copyPile;
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
    // method for the first layer
    public Node pickDiscard(SetOfCards current, SetOfCards discardPile){
        SetOfCards copyCards = new SetOfCards(current.toList());
        SetOfCards copyDiscard = new SetOfCards(discardPile.toList());

        copyCards.addCard(discardPile.getCard(discardPile.size()-1));
        copyDiscard.discardCard(discardPile.getCard(discardPile.size()-1));

        List<Card> copyList = copyCards.toList();
        Card discardCard = chooseCardToDiscard(copyList);
        copyCards.discardCard(discardCard);
        copyDiscard.addCard(discardCard);
        Node result = new Node(copyDiscard, copyCards, cardsUnknown, opponentHand, depthTree);
        return result;
    }

    // method for the first layer
    public Node pickDeck(SetOfCards hand, SetOfCards cardsUnknown, SetOfCards discardPile){
        SetOfCards copyCards = new SetOfCards(hand.toList());
        SetOfCards copyDeck = new SetOfCards(cardsUnknown.toList());
        SetOfCards copyDiscard = new SetOfCards(discardPile.toList());
        Random rd = new Random(); // creating Random object
        int randomCard = rd.nextInt(copyDeck.size()-1);

        copyCards.addCard(cardsUnknown.getCard(randomCard));
        copyDeck.discardCard(cardsUnknown.getCard(randomCard));

        List<Card> copyList = copyCards.toList();
        Card discardCard = chooseCardToDiscard(copyList);
        copyCards.discardCard(discardCard);
        copyDiscard.addCard(discardCard);
        Node result = new Node(copyDiscard, copyCards, copyDeck, opponentHand, depthTree);
        return result;
    }

    //simulate if opponent picks from the discard pile
    public void simulationPickPile(Node parent){
        // if opponent picks card from pile
        copyParent(parent);

        opponentHand = new SetOfCards();
        Card chosen = discardPile.getCard((discardPile.size()-1));
        discardPile.discardCard(chosen);
        lookThroughKnownCards(chosen);
        // update prob of unknown cards
        for(int j = 0; j<cardsUnknown.size(); j++) {
            double setProb = 0;
            double runProb = 0;
                // increase prob for cards that form set with chosen card
            if (cardsUnknown.getCard(j).getValue() == chosen.getValue()) {
                setProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownSet));
                cardsUnknown.getCard(j).setProb(setProb);
            }
            // increase prob for cards that form run with chosen card
            else if (cardsUnknown.getCard(j).getSuit() == chosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - chosen.getValue()) == 1) {
                runProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownRun));
                cardsUnknown.getCard(j).setProb(runProb);
            }
        }
        cardsUnknown.addCard(chosen);
        chosen.setProb(1.0);
    }

   public void simulationPickDeck(Node parent) {
       copyParent(parent);
       opponentHand = new SetOfCards();
       Card notChosen = discardPile.getCard(discardPile.size() - 1);
       lookThroughKnownCards(notChosen);
       for (int j = 0; j < cardsUnknown.size(); j++) {
           double setProb = 0;
           double runProb = 0;
           // decrease prob of cards that form set with not chosen card
           if (cardsUnknown.getCard(j).getValue() == notChosen.getValue()) {
               setProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownSet));
               cardsUnknown.getCard(j).setProb(setProb);
           }
           // decrease prob of cards that form run with chosen card
           if (cardsUnknown.getCard(j).getSuit() == notChosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - notChosen.getValue()) == 1) {
               runProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownRun));
               cardsUnknown.getCard(j).setProb(runProb);
           }
       }
   }


    public void simulationDiscard(Node parent, Card discard){
        copyParent(parent);
        lookThroughKnownCards(discard);
        discardPile.addCard(discard);
        cardsUnknown.discardCard(discard);
        opponentHand.discardCard(discard);
        // OPPONENT DISCARDS CARD
        for(int j = 0; j<cardsUnknown.size(); j++){
            double setProb = 0.0;
            double runProb = 0.0;
            // decrease prob of cards that form set with discarded card
            if(cardsUnknown.getCard(j).getValue() == discard.getValue()){
                setProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownSet));
                cardsUnknown.getCard(j).setProb(setProb);
            }
            // decrease prob of cards that form run with discarded card
            if(cardsUnknown.getCard(j).getSuit() == discard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discard.getValue()) == 1){
                runProb = cardsUnknown.getCard(j).getProb()*(1.0 / (2.0 * leftInUnknownRun));
                cardsUnknown.getCard(j).setProb(runProb);
            }
        }
    }

    public void copyParent(Node parent){
        this.discardPile = new SetOfCards(parent.discardPile.toList());
        this.cardsUnknown = new SetOfCards(parent.unknownCards.toList());
        this.hand = new SetOfCards(parent.hand.toList());
        this.opponentHand = new SetOfCards(parent.opponentHand.toList());
        this.depthTree = parent.getDepthTree() + 1;
    }

    // pickOrDiscard variable to indicate if you need to create hand of 10 or 11 cards, true if 11 (= picking process)
    public List<Node> monteCarloSim(boolean pickOrDiscard){
        List<Node> nodes = new ArrayList<>();
        List<Card> opponentHandcur;
        // simulate 100 times
        for(int i= 1; i<= 100; i++){
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
            Node node = new Node(discardPile,hand, cardsUnknown, opponent, depthTree);
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
    }


    public static Card chooseCardToDiscard(List<Card> aHand){
        Card theCard = null;
        //List<Card> deadwood = Player.
        // starting value of a hand
        int highestVal = Player.scoreHand(aHand);

        for(Card aCard : aHand){
            //deep copy aList (method is in Player class already)
            List<Card> aList = Player.copyList(aHand);
            aList.remove(aCard);
            int resultingHand = Player.scoreHand(aList);
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
                copyList.remove(copyList.get(i));
            }
        }
        while(resultList.size() < size){
            Card toSave = pickRandomCard(copyList);
            if(toSave != null) {
                resultList.add(toSave);
                copyList.remove(toSave);
            }
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
        double curVal = 0.0;
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
        return min+(Math.random()*(max-min));
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


    public SetOfCards getCardsUnknown() {
        return cardsUnknown;
    }

    public void printOutTree(Node node) {
        if (node.getChildren().size() == 0) {
            System.out.println(node);
        }
        for (Node child : node.getChildren()) {
            printOutTree(child);
        }
    }
    public SetOfCards updateProbDiscard(Node current, Card discardCard){
        copyParent(current);
        lookThroughKnownCards(discardCard);
        // OPPONENT DISCARDS CARD
        for(int j = 0; j<cardsUnknown.size(); j++){
            double setProb = 0.0;
            double runProb = 0.0;
            // decrease prob of cards that form set with discarded card
            if(cardsUnknown.getCard(j).getValue() == discardCard.getValue()){
                setProb = cardsUnknown.getCard(j).getProb() * (1.0 / (2.0 * leftInUnknownSet));
                cardsUnknown.getCard(j).setProb(setProb);
            }
            // decrease prob of cards that form run with discarded card
            if(cardsUnknown.getCard(j).getSuit() == discardCard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discardCard.getValue()) == 1){
                runProb = cardsUnknown.getCard(j).getProb()*(1.0 / (2.0 * leftInUnknownRun));
                cardsUnknown.getCard(j).setProb(runProb);
            }
        }
        return cardsUnknown;
    }
    public SetOfCards updateProbPickPile(Node current, Card topOfDiscard){
        // if opponent picks card from pile
        copyParent(current);
        lookThroughKnownCards(topOfDiscard);
        // update prob of unknown cards
        for(int j = 0; j<cardsUnknown.size(); j++) {
            double setProb = 0;
            double runProb = 0;
            // increase prob for cards that form set with chosen card
            if (cardsUnknown.getCard(j).getValue() == topOfDiscard.getValue()) {
                setProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownSet));
                cardsUnknown.getCard(j).setProb(setProb);
            }
            // increase prob for cards that form run with chosen card
            else if (cardsUnknown.getCard(j).getSuit() == topOfDiscard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - topOfDiscard.getValue()) == 1) {
                runProb = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownRun));
                cardsUnknown.getCard(j).setProb(runProb);
            }
        }
        cardsUnknown.addCard(topOfDiscard);
        topOfDiscard.setProb(1.0);
        return cardsUnknown;
    }



    public static void main(String[] args){
        List<MyCard> cards = new ArrayList<>();

        for(int i=0; i<4; i++){
            cards.add(new MyCard(0,i));
        }

        for(int i =1;i<4;i++){
            cards.add(new MyCard(i,4));
        }

        cards.add(new MyCard(1,10));
        cards.add(new MyCard(3,10));

        HandLayout handLayout = Finder.findBestHandLayout(cards);

        MyCard discard = new MyCard(0,4);

        MinimaxPruningAI ai = new MinimaxPruningAI();





        long startTime = System.nanoTime();
        ai.update(handLayout);
        long endTime = System.nanoTime();
        long length = (endTime-startTime);

        System.out.println("To update internally, it took " + length);



        startTime = System.nanoTime();
        ai.newRound(discard);
        endTime = System.nanoTime();
        length = (endTime-startTime);

        System.out.println("For a new round, it took " + length);


        startTime = System.nanoTime();
        ai.pickDeckOrDiscard(40,discard);
        endTime = System.nanoTime();
        length = (endTime-startTime);

        System.out.println("To pick whether deck or pile it took " + length);

        /*SetOfCards deck = new SetOfCards(true, false);
        SetOfCards hand = new SetOfCards(false, false);
        for(int i = 0; i < 10; i++){
            Card aCard = deck.drawTopCard();
            hand.addCard(aCard);
        }
        SetOfCards pile = new SetOfCards(false, false);
        Card discardCard = deck.drawTopCard();
        pile.addCard(discardCard);
        GametreeAI AI = new GametreeAI(pile, hand,deck, 10);
        AI.createTree(true);
        System.out.print("heyyyy");*/
    }
}