package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

public class GametreeAI {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards cardsUnknown;
    SetOfCards opponentHand;
    private int leftInUnknownSet = 4;
    private int leftInUnknownRun = 2;
    private int simulationNum = 3;
    private int depthTree = 0;
    private int maxDepth;
    //private HashMap<MyCard, double>;

    public  GametreeAI (SetOfCards pile, SetOfCards cards, SetOfCards deck, int maxDepth){
        this.discardPile = pile;
        this.hand = cards;
        this.cardsUnknown = deck;
        opponentHand = new SetOfCards();
        for(int i = 0; i< 10; i++){
            opponentHand.addCard(cardsUnknown.getCard(i));
        }
        this.maxDepth = maxDepth;
    }

    public void createTree (){
        Node first = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree);
        depthTree++;
        Node pass = first.addChild(new Node(discardPile,hand,cardsUnknown, opponentHand, depthTree));
        Node discard1 = first.addChild(pickDiscard(hand,discardPile));
        depthTree++;
        createNodesOpponent(pass, true);
        createNodesOpponent(discard1, false);
    }

    public Node getParentNode() {
        return new Node(this.discardPile, this.hand, this.cardsUnknown, this.opponentHand, depthTree);
    }

    // we need to determine stop statement for recursion
    // firstRound true if opponent can still pass
    public void createNodesOpponent(Node parent, boolean firstRound){
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){
            System.out.print("end creating children");
        }
        else{

            // makes nodes for if opponent picks from discard pile
            simulationPickPile(parent);
            List<Node> nodesPile = monteCarloSim(true);
            for(int i = 0; i< nodesPile.size(); i++){
                parent.addChild(nodesPile.get(i));
                simulationDiscard(nodesPile.get(i));
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
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){
            System.out.print("end creating children");
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

    //simulate if opponent picks from the discard pile
    public void simulationPickPile(Node parent){
        // if opponent picks card from pile
        copyParent(parent);
        opponentHand = new SetOfCards();
        Card chosen = discardPile.getCard((discardPile.size()-1));
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
        chosen.setProb(1.0);
        cardsUnknown.addCard(chosen);
        discardPile.discardCard(chosen);
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


    public void simulationDiscard(Node parent){
        copyParent(parent);
        Card discard = chooseCardToDiscard(parent.opponentHand.toList());
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
        for(int i= 1; i<= this.simulationNum; i++){
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

    //demo pruning algo
    // can it be removed???
    public int alphabetaPruning(Node node, int alpha, int beta, boolean maximizingPlayer ) {
        if ((node.getChildren().size() == 0) || !node.playerStop || !node.AIStop)
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
        GametreeAI AI = new GametreeAI(pile, hand,deck, 10);
        AI.createTree();
        System.out.print("heyyyy");
    }
}