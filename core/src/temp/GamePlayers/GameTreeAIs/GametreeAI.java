package temp.GamePlayers.GameTreeAIs;

import cardlogic.Card;
import cardlogic.SetOfCards;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MyCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GametreeAI {
    /*
    Main change I did was change from set of cards into List<MyCard>
    From there on I went through the code and tried to fix all of the red lines that appeared
    That's the gist of it tbh
     */

    public List<MyCard> discardPile;
    public List<MyCard> hand;
    public List<MyCard> cardsUnknown;
    public List<MyCard> opponentHand;

    public double[][] probMap = new double[4][13];

    private int leftInUnknownSet = 4;
    private int leftInUnknownRun = 2;
    private int depthTree = 0;
    private int maxDepth;
    private Node root;

    public  GametreeAI (List<MyCard> pile, List<MyCard> cards, List<MyCard> deck, int maxDepth, double [][] probMap){
        this.discardPile = pile;
        this.hand = cards;
        this.cardsUnknown = deck;
        opponentHand = new ArrayList<>();
        for(int i = 0; i< 10; i++){
            opponentHand.add(cardsUnknown.get(i));
        }
        this.maxDepth = maxDepth;
        this.probMap = probMap;
        // TODO: Bugtest and make sure all of the nodes have the correct relevant probabilities
        root = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, probMap);
    }
    //If first round is true then generate first round rules
    public void createTree (boolean firstRound){
        if(firstRound) {
            copyParent(root);
            Node pass = root.addChild(new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap));
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
        Node copyRoot = new Node(this.discardPile, this.hand, this.cardsUnknown, this.opponentHand, 0, this.probMap);
        for(Node child : root.getChildren()){
            copyRoot.addChild(child);
        }
        return root;
    }

    // we need to determine stop statement for recursion
    // firstRound true if opponent can still pass
    public void createNodesOpponent(Node parent, boolean firstRound){
        if(finishTree(parent)){

        }
        else{

            // makes nodes for if opponent picks from discard pile
            simulationPickPile(parent);
            List<Node> nodesPile = monteCarloSim(true);
            for(int i = 0; i< nodesPile.size(); i++){
                parent.addChild(nodesPile.get(i));
                MyCard discard = chooseCardToDiscard(nodesPile.get(i).opponentHand);
                if(finishTree(nodesPile.get(i))){

                }
                else{
                    simulationDiscard(nodesPile.get(i), discard);
                    List<Node> nodesDiscard1 = monteCarloSim(false);
                    for(int j = 0; j< nodesDiscard1.size(); j++){
                        nodesPile.get(i).addChild(nodesDiscard1.get(i));
                        createNodesAI(nodesDiscard1.get(j));
                    }
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
                if(finishTree(nodesDeck.get(i))){

                }
                else{
                    if(!firstRound){
                        MyCard discard = chooseCardToDiscard(nodesDeck.get(i).opponentHand);
                        simulationDiscard(nodesDeck.get(i), discard);
                        List<Node> nodesDiscard2 = monteCarloSim(false);
                        for(int j = 0; j< nodesDiscard2.size(); j++){
                            nodesDeck.get(j).addChild(nodesDiscard2.get(i));
                            createNodesAI(nodesDiscard2.get(j));
                        }
                    }
                    else{
                        createNodesAI(nodesDeck.get(i));
                    }
                }

            }
        }
    }

    public boolean finishTree(Node parent){
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){
            System.out.println("Stopped with depth " + parent.getDepthTree());
            return true;
        }
        else{
            return false;
        }
    }


    // we need to determine stop statement for recursion
    public void createNodesAI(Node parent){
        if(finishTree(parent)){

        }
        else{
            copyParent(parent);
            MyCard topPile = discardPile.get(discardPile.size()-1);
            if(evaluate(topPile, parent.hand)){
                hand.add(topPile);
                discardPile.remove(topPile);
                MyCard discard = chooseCardToDiscard(hand);
                hand.remove(discard);
                discardPile.add(discard);
                Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap);
                parent.addChild(child);
                createNodesOpponent(child, false);
            }
            else{

                List<MyCard> deck = makeDeck(cardsUnknown, opponentHand);

                List<MyCard> copyHand = new ArrayList<>(hand);
                        //new SetOfCards(hand.toList());
                List<MyCard> copyCardUnknown = new ArrayList<>(cardsUnknown);
                        //new SetOfCards(cardsUnknown.toList());
                List<MyCard> copyPile = new ArrayList<>(discardPile);
                        //new SetOfCards(discardPile.toList());

                for(int i = 0; i< deck.size(); i++){
                    hand.add(deck.get(i));
                    cardsUnknown.remove(deck.get(i));
                    MyCard discard = chooseCardToDiscard(hand);
                    hand.remove(discard);
                    discardPile.add(discard);
                    Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap);
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

    public List<MyCard> makeDeck(List<MyCard> unknown, List<MyCard> opponent){
        List<MyCard> deck = GametreeAI.deepCloneMyCardList(unknown);
        for(int i = 0; i<opponent.size(); i++){
            deck.remove(opponent.get(i));
        }
        return deck;
    }
    // method for the first layer
    public Node pickDiscard(List<MyCard> current, List<MyCard> discardPile){
        List<MyCard> copyCards = deepCloneMyCardList(current);
        List<MyCard> copyDiscard = deepCloneMyCardList(discardPile);

        copyCards.add(discardPile.get(discardPile.size()-1));
        copyDiscard.remove(discardPile.get(discardPile.size()-1));

        List<MyCard> copyList = deepCloneMyCardList(copyCards);
        MyCard discardCard = chooseCardToDiscard(copyList);
        copyCards.remove(discardCard);
        copyDiscard.add(discardCard);
        Node result = new Node(copyDiscard, copyCards, cardsUnknown, opponentHand, depthTree, this.probMap);
        return result;
    }

    public static List<MyCard> deepCloneMyCardList(List<MyCard> aList){
        List<MyCard> deepClone = new ArrayList<>();
        for(MyCard card:aList){
            deepClone.add(new MyCard(card));
        }

        return deepClone;
    }

    public static List<MyCard> cloneMyCardList(List<MyCard> aList){
        List<MyCard> clone = new ArrayList<>();
        for(MyCard card:aList){
            clone.add(card);
        }

        return clone;
    }


    // method for the first layer
    public Node pickDeck(List<MyCard> hand, List<MyCard> cardsUnknown, List<MyCard> discardPile){
        List<MyCard> copyCards = new ArrayList<>(hand);
                //new SetOfCards(hand.toList());
        List<MyCard> copyDeck = new ArrayList<>(cardsUnknown);
                //new SetOfCards(cardsUnknown.toList());
        List<MyCard> copyDiscard = new ArrayList<>(discardPile);
                //new SetOfCards(discardPile.toList());
        Random rd = new Random(); // creating Random object
        int randomCard = rd.nextInt(copyDeck.size()-1);

        copyCards.add(cardsUnknown.get(randomCard));
        copyDeck.remove(cardsUnknown.get(randomCard));

        MyCard discardCard = chooseCardToDiscard(copyCards);
        copyCards.remove(discardCard);
        copyDiscard.add(discardCard);
        Node result = new Node(copyDiscard, copyCards, copyDeck, opponentHand, depthTree, this.probMap);
        return result;
    }

    //simulate if opponent picks from the discard pile
    public void simulationPickPile(Node parent){
        // if opponent picks card from pile
        copyParent(parent);
        opponentHand = new ArrayList<>();
        MyCard chosen = discardPile.get((discardPile.size()-1));
        discardPile.remove(chosen);
        //checkDoubles();
        lookThroughKnownCards(chosen);
        // update prob of unknown cards

        for(int j = 0; j<cardsUnknown.size(); j++) {
            MyCard currentCard = cardsUnknown.get(j);

                // increase prob for cards that form set with chosen card
            if (currentCard.rank.index == chosen.rank.index) {
                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownSet));

                this.setProbability(currentCard, prob);

                //cardsUnknown.getCard(j).setProb(prob);
            }
            // increase prob for cards that form run with chosen card
            else if (currentCard.suit.index == chosen.suit.index && Math.abs(currentCard.rank.index - chosen.rank.index) == 1) {
                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownRun));
                this.setProbability(currentCard, prob);

                /*double prob = cardsUnknown.getCard(j).getProb() / (1.0 / (2.0 * leftInUnknownRun));
                cardsUnknown.getCard(j).setProb(prob);*/
            }
        }
        cardsUnknown.add(chosen);
        this.setProbability(chosen, 1.0);
        //chosen.setProb(1.0);
    }

   public void simulationPickDeck(Node parent) {
       copyParent(parent);
       opponentHand = new ArrayList<>();

       MyCard notChosen = discardPile.get(discardPile.size() - 1);
       //checkDoubles();
       lookThroughKnownCards(notChosen);
       for (int j = 0; j < cardsUnknown.size(); j++) {
           // decrease prob of cards that form set with not chosen card
           MyCard currentCard = cardsUnknown.get(j);

           if (currentCard.rank.index == notChosen.rank.index) {
               double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownSet));
               this.setProbability(currentCard, prob);

               //cardsUnknown.getCard(j).setProb(setProb);
           }
           // decrease prob of cards that form run with chosen card
           if (currentCard.suit.index == notChosen.suit.index && Math.abs(currentCard.rank.index - notChosen.rank.index) == 1) {
               double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownRun));
               this.setProbability(currentCard, prob);

               //cardsUnknown.getCard(j).setProb(runProb);
           }
       }
   }


    public void simulationDiscard(Node parent, MyCard discard){
        copyParent(parent);
        if(finishTree(parent)){

        }
        else{
            //checkDoubles();
            lookThroughKnownCards(discard);
            discardPile.add(discard);
            cardsUnknown.remove(discard);
            opponentHand.remove(discard);

            // OPPONENT DISCARDS CARD
            for(int j = 0; j<cardsUnknown.size(); j++){
                // decrease prob of cards that form set with not chosen card
                MyCard currentCard = cardsUnknown.get(j);

                if (currentCard.rank.index == discard.rank.index) {
                    double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownSet));
                    this.setProbability(currentCard, prob);
                    //cardsUnknown.getCard(j).setProb(setProb);
                }
                // decrease prob of cards that form run with chosen card
                if (currentCard.suit.index == discard.suit.index && Math.abs(currentCard.rank.index - discard.rank.index) == 1) {
                    double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownRun));
                    this.setProbability(currentCard, prob);
                    //cardsUnknown.getCard(j).setProb(runProb);
                }
            }
        }

    }

    public void copyParent(Node parent){
        this.discardPile = GametreeAI.deepCloneMyCardList(parent.discardPile);
                //new SetOfCards(parent.discardPile.toList());
        this.cardsUnknown = GametreeAI.deepCloneMyCardList(parent.unknownCards);
        this.hand = GametreeAI.deepCloneMyCardList(parent.hand);
        this.opponentHand = GametreeAI.deepCloneMyCardList(parent.opponentHand);
        this.depthTree = parent.getDepthTree() + 1;

        this.probMap = parent.getProbMap();

    }

    // pickOrDiscard variable to indicate if you need to create hand of 10 or 11 cards, true if 11 (= picking process)
    public List<Node> monteCarloSim(boolean pickOrDiscard){
        List<Node> nodes = new ArrayList<>();
        List<MyCard> opponentHandcur;
        // simulate 100 times
        for(int i= 1; i<= 100; i++){
            if(pickOrDiscard){
                opponentHandcur = chooseRandomCards(cardsUnknown, 11);
            }
            else{
                opponentHandcur = chooseRandomCards(cardsUnknown, 10);
            }

            Node node = new Node(discardPile,hand, cardsUnknown, opponentHandcur, depthTree, this.probMap);
            nodes.add(node);
        }
        return nodes;
    }

    public void  checkDoubles (){
        for(int i = 0; i < cardsUnknown.size(); i++) {
            if (discardPile.contains(cardsUnknown.get(i))||hand.contains(cardsUnknown.get(i))) {
                //System.out.println("double = " +cardsUnknown.get(i));
                this.cardsUnknown.remove(cardsUnknown.get(i));
            }
        }
    }
    // method that looks in known cards (hand + discardpile) for usefull cards
    public void lookThroughKnownCards(MyCard chosen){
        // 4 suits
        //checkDoubles();
        leftInUnknownSet = 3;
        // if chosen card is Ace or King you only have 1 'neighbour' for run
        if(chosen.rank.index == 0 || chosen.rank.index == 12){
            leftInUnknownRun = 1;
        }
        else{
            leftInUnknownRun = 2;
        }
        for(int k = 0; k< hand.size(); k++){
            // look through hand for cards that form set with given card
            if(hand.get(k).rank.index == chosen.rank.index && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            // look through hand for cards that form run with given card
            if(hand.get(k).suit.index == chosen.suit.index && Math.abs(hand.get(k).rank.index - chosen.rank.index) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }
        // look through pile, same as hand
        for(int k = 0; k< discardPile.size(); k++){
            if(discardPile.get(k).rank.index == chosen.rank.index && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            if(discardPile.get(k).suit.index == chosen.suit.index && Math.abs(discardPile.get(k).rank.index - chosen.rank.index) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }

        //if(leftInUnknownRun ==0|| leftInUnknownSet==0){
            //System.out.println("We're in NaN territory");
            //boolean hey = true;
        //}

    }

    public static MyCard chooseCardToDiscard(List<MyCard> aHand){
        MyCard theCard = null;
        // starting value is set at a number that's higher than attainable
        // 1000 is enough
        int highestVal = 1000;

        for(MyCard aCard : aHand){
            //deep copy aList (method is in Player class already)
            List<MyCard> aList = deepCloneMyCardList(aHand);
            aList.remove(aCard);
            int resultingHand = Finder.findBestHandLayout(aList).getDeadwood();
            if(resultingHand <= highestVal){    //the result from scoreHand is counting deadwood value so it should be smaller than the previous step
                theCard = aCard;
                highestVal = resultingHand;
            }
        }

        return theCard;
    }

    // TODO: Check that the probabilities are correctly passed over
    // For now I'm assuming that the probabilities are being obtained directly from the current GameTree
    // But obviously I should ask and make sure that it's the case
    // I changed it from static to class based due to this, but yeah this is definitely something I'm not 100% confident in
    public List<MyCard> chooseRandomCards(List<MyCard> totalCards, int size){
        List<MyCard> copyList = deepCloneMyCardList(totalCards);
                //Player.copyList(totalCards);
        List<MyCard> resultList = new ArrayList<>();
        for(int i = 0; i < copyList.size();i++){
            // Might be changed to is greater than or equals
            MyCard card = copyList.get(i);
            if(this.getProbability(card) >= 1.0){
                resultList.add(copyList.get(i));
                copyList.remove(copyList.get(i));
            }
        }
        while(resultList.size() < size){
            MyCard toSave = pickRandomCard(copyList);
            if(toSave != null) {
                resultList.add(toSave);
                copyList.remove(toSave);
            }
        }

        return resultList;
    }

    // Changed it from static to class based
    // TODO: Make sure this doesn't end up screwing the probabilities in some way
    public double calcTotalProb(List<MyCard> setOfCards){
        double val = 0.0;
        for(MyCard aCard: setOfCards){
            val+=this.getProbability(aCard);
            //if( val!= val){
              //  System.out.println("We have reached NaN");
           // }

        }
        return val;

    }

    // Changed from static to class
    // Made so that I could retrieve the probability properly
    // TODO: Make sure this is done correctly
    public MyCard pickRandomCard(List<MyCard> setOfCard){
        double val = calcTotalProb(setOfCard);
        double objective = randomNumberGenerator(0,val);
        double curVal = 0.0;
        MyCard finCard = null;
        for(MyCard aCard: setOfCard){
            curVal += this.getProbability(aCard);
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
    public boolean evaluate(MyCard discardCard, List<MyCard> hand){
        List<MyCard> current = Finder.findBestHandLayout(hand).viewAllCards();
        current.add(discardCard);
        if(chooseCardToDiscard(current) == discardCard){
            return false;
        }
        else{
            return true;
        }
    }


    public List<MyCard> getCardsUnknown() {
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

    public List<MyCard> updateProbDiscard(Node current, MyCard discardCard){
        copyParent(current);

        lookThroughKnownCards(discardCard);
        // OPPONENT DISCARDS CARD
        for(int j = 0; j<cardsUnknown.size(); j++){

            MyCard currentCard = cardsUnknown.get(j);
            // decrease prob of cards that form set with discarded card
            if(currentCard.rank.index == discardCard.rank.index){
                double prob = this.getProbability(cardsUnknown.get(j)) * (1.0 / (2.0 * leftInUnknownSet));
                current.setProbability(cardsUnknown.get(j), prob);
                //cardsUnknown.getCard(j).setProb(setProb);


            }
            // decrease prob of cards that form run with discarded card
            if(currentCard.suit.index == discardCard.suit.index && Math.abs(currentCard.rank.index - discardCard.rank.index) == 1){

                double prob = this.getProbability(cardsUnknown.get(j)) * (1.0 / (2.0 * leftInUnknownRun));
                this.setProbability(cardsUnknown.get(j), prob);
            }
        }
        return cardsUnknown;
    }
    /*
    Uhh wait, so there are sometimes where the update happens in current
    And sometimes where it happens in the parent
    I'm doing the same thing no matter what, but is it something I should worry about?
     */
    public List<MyCard> updateProbPickPile(Node current, MyCard topOfDiscard){
        // if opponent picks card from pile
        copyParent(current);

        lookThroughKnownCards(topOfDiscard);
        // update prob of unknown cards
        for(int j = 0; j<cardsUnknown.size(); j++) {
            // decrease prob of cards that form set with not chosen card
            MyCard currentCard = cardsUnknown.get(j);

            if (currentCard.rank.index == topOfDiscard.rank.index) {
                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownSet));
                this.setProbability(currentCard, prob);
                //cardsUnknown.getCard(j).setProb(setProb);
            }
            // decrease prob of cards that form run with chosen card
            if (currentCard.suit.index == topOfDiscard.suit.index && Math.abs(currentCard.rank.index - topOfDiscard.rank.index) == 1) {

                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownRun));
                this.setProbability(currentCard, prob);
                //cardsUnknown.getCard(j).setProb(runProb);
            }
        }

        cardsUnknown.add(topOfDiscard);

        this.setProbability(topOfDiscard, 1.0);
        return cardsUnknown;
    }

    double getProbability(MyCard aCard){
        return probMap[aCard.suit.index][aCard.rank.index];
    }

    void updateProbability(MyCard aCard, double aVal){
        probMap[aCard.suit.index][aCard.rank.index] = probMap[aCard.suit.index][aCard.rank.index]*aVal;
    }

    void setProbability(MyCard card, double val){
        if(val!= val){
            System.out.println("cards unknown = "+ cardsUnknown.size());
            System.out.println("cards in hand = "+hand.size());
            System.out.println("cards in discardpile = "+discardPile.size());

            for(MyCard card1 : cardsUnknown){
                if(hand.contains(card1)){

                    System.out.println("hand = "+card1);

                }
                if(discardPile.contains(card1)){
                    System.out.println("discardpile =" +card1);
                }
            }
            System.out.println("We have reached Nan");
        }


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



    public static void main(String[] args){
        SetOfCards deck = new SetOfCards(true, false);
        SetOfCards hand = new SetOfCards(false, false);
        for(int i = 0; i < 10; i++){
            Card aCard = deck.drawTopCard();
            hand.addCard(aCard);
        }
        /*SetOfCards pile = new SetOfCards(false, false);
        Card discardCard = deck.drawTopCard();
        pile.addCard(discardCard);
        GametreeAI AI = new GametreeAI(pile, hand,deck, 10);
        AI.createTree(true);*/
        System.out.print("heyyyy");
    }
}