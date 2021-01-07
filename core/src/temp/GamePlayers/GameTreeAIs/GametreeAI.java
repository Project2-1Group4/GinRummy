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

    public List<MyCard> discardPile;
    public List<MyCard> hand;
    public List<MyCard> cardsUnknown; // all cards that are not in your hand or the discard pile, which equal the deck + opponent hand
    public List<MyCard> opponentHand;

    /*
    map of probabilities how likely a card is to be in the opponents hand
    if a opponent picks a card from the discard pile, it's likely that the they also have cards that can form a melt with the picked card
    if a opponent picks a card from the deck, it's likely that they don't have cards that can form a melt with the rejected card
     */
    public double[][] probMap;
    private int leftInUnknownSet = 4; // cards that can form a set with a certain card, that are not in the discard pile or your hand
    private int leftInUnknownRun = 2; // cards that can form a run with a certain card, that are not in the discard pile or your hand

    private int depthTree = 1;
    private int maxDepth;
    private Node root;

    public  GametreeAI (List<MyCard> pile, List<MyCard> cards, List<MyCard> deck, int maxDepth, double [][] probMap){
        this.discardPile = pile;
        this.hand = cards;
        this.cardsUnknown = deck;
        opponentHand = new ArrayList<>();
        // generate random opponent hand at start
        for(int i = 0; i< 10; i++){
            opponentHand.add(cardsUnknown.get(i));
        }
        this.maxDepth = maxDepth;
        this.probMap = probMap;
        root = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, probMap);
    }

    /*
    Initiates the tree building, takes into account the first turn if needed
     */
    public void createTree (boolean firstRound){
        //If the game is in the first turn where you only can take from discard pile
        if(firstRound) {
            copyParent(root);
            Node pass = root.addChild(new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap)); // decide to pass
            copyParent(root);
            Node discard1 = root.addChild(pickDiscard(hand, discardPile)); // pick from discard pile
            createNodesOpponent(pass, true);
            createNodesOpponent(discard1, false);
        }
        // tree created at a general turn
        else{
            copyParent(root);
            Node deck = root.addChild(pickDeck(hand,cardsUnknown,discardPile)); // pick from deck pile
            copyParent(root);
            Node discard1 = root.addChild(pickDiscard(hand, discardPile)); // pick from discard pile
            createNodesOpponent(deck, false);
            createNodesOpponent(discard1, false);
        }
    }

    /*
    get copy of node at the root of the tree for search
     */
    public Node getRootNode() {
        Node copyRoot = new Node(this.discardPile, this.hand, this.cardsUnknown, this.opponentHand, 0, this.probMap);
        for(Node child : root.getChildren()){
            copyRoot.addChild(child);
        }
        return root;
    }

    /*
    Create nodes for the moves of the opponent player
     */
    public void createNodesOpponent(Node parent, boolean firstRound){
        if(finishTree(parent)){

        }
        else{
            simulationPickPile(parent); // adjust probabilities of cardsunknown for if opponent picks from discard pile
            List<Node> nodesPile = monteCarloSim(true); // create nodes with monte carlo simulation based on the new probs
            for(int i = 0; i< nodesPile.size(); i++){
                parent.addChild(nodesPile.get(i)); // add newly created nodes as children to their parents
                MyCard discard = chooseCardToDiscard(nodesPile.get(i).opponentHand);
                if(finishTree(nodesPile.get(i))){ // check if you have already reached the max depth of the tree

                }
                else{
                    simulationDiscard(nodesPile.get(i), discard); // update probabilities for cards unknown based on discarded card
                    List<Node> nodesDiscard1 = monteCarloSim(false);
                    for(int j = 0; j< nodesDiscard1.size(); j++){
                        nodesPile.get(i).addChild(nodesDiscard1.get(i)); // add new nodes to their parents as children
                        createNodesAI(nodesDiscard1.get(j)); // create nodes for own turn
                    }
                }

            }
            // make nodes for if opponent picks deck or for the opponent passing in the first round
            simulationPickDeck(parent); // update probabilities
            List<Node> nodesDeck;
            if(firstRound){
                nodesDeck = monteCarloSim(false); //nodes for pass in first round
            }
            else{
                nodesDeck =  monteCarloSim(true); // nodes for picking deck
            }
            for(int i = 0; i< nodesDeck.size(); i++){
                parent.addChild(nodesDeck.get(i));
                if(finishTree(nodesDeck.get(i))){

                }
                else{
                    if(!firstRound){ // if opponent picks from deck it will also discard a card
                        MyCard discard = chooseCardToDiscard(nodesDeck.get(i).opponentHand);
                        simulationDiscard(nodesDeck.get(i), discard);
                        List<Node> nodesDiscard2 = monteCarloSim(false);
                        for(int j = 0; j< nodesDiscard2.size(); j++){
                            nodesDeck.get(j).addChild(nodesDiscard2.get(i));
                            createNodesAI(nodesDiscard2.get(j));
                        }
                    }
                    else{ // if opponent passed, it won't discard so next layer is our turn
                        createNodesAI(nodesDeck.get(i));
                    }
                }
            }
        }
    }

    /*
    Base case tree building, stop creating children if true:
    - hands value > opponents hand value and deadwood <10 --> knock
    - deck has less then 2 cards, round is finished
    - you reached max depth of tree
     */
    public boolean finishTree(Node parent){
        if(parent.winOrLose || cardsUnknown.size() < 12 || parent.getDepthTree() == maxDepth){
            return true;
        }
        else{
            return false; // continue creating next layer of children
        }
    }


    /*
    Create nodes for own turn
     */
    public void createNodesAI(Node parent){
        if(finishTree(parent)){

        }
        else{
            copyParent(parent);
            MyCard topPile = discardPile.get(discardPile.size()-1); // top card of discard pile
            if(evaluate(topPile, parent.hand)){ // if card is worth taking, create node
                hand.add(topPile);
                discardPile.remove(topPile);
                MyCard discard = chooseCardToDiscard(hand);
                hand.remove(discard);
                discardPile.add(discard);
                Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap);
                parent.addChild(child);
                createNodesOpponent(child, false); // our turn is over, next layer is opponents turn
            }
            else{
                // copy current situation
                List<MyCard> deck = makeDeck(cardsUnknown, opponentHand);
                List<MyCard> copyHand = new ArrayList<>(hand);
                List<MyCard> copyCardUnknown = new ArrayList<>(cardsUnknown);
                List<MyCard> copyPile = new ArrayList<>(discardPile);
                // make node for every option of card that you can get from deck
                for(int i = 0; i< deck.size(); i++){
                    hand.add(deck.get(i));
                    cardsUnknown.remove(deck.get(i));
                    MyCard discard = chooseCardToDiscard(hand);
                    hand.remove(discard);
                    discardPile.add(discard);
                    Node child = new Node(discardPile, hand, cardsUnknown, opponentHand, depthTree, this.probMap);
                    parent.addChild(child);
                    createNodesOpponent(child, false);
                    //reset situation for next possible card in deck
                    hand = copyHand;
                    cardsUnknown = copyCardUnknown;
                    discardPile = copyPile;
                }
            }
        }
    }

    /*
    make estimate of current deck based on assumed opponent hand
     */
    public List<MyCard> makeDeck(List<MyCard> unknown, List<MyCard> opponent){
        List<MyCard> deck = GametreeAI.deepCloneMyCardList(unknown);
        for(int i = 0; i<opponent.size(); i++){
            deck.remove(opponent.get(i));
        }
        return deck;
    }

    /*
    create nodes for first layer in tree
     */
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

    /*
    create nodes for first layer in tree
     */
    public Node pickDeck(List<MyCard> hand, List<MyCard> cardsUnknown, List<MyCard> discardPile){
        List<MyCard> copyCards = new ArrayList<>(hand);
        List<MyCard> copyDeck = new ArrayList<>(cardsUnknown);
        List<MyCard> copyDiscard = new ArrayList<>(discardPile);
        Random rd = new Random();
        int randomCard = rd.nextInt(copyDeck.size()-1);

        copyCards.add(cardsUnknown.get(randomCard));
        copyDeck.remove(cardsUnknown.get(randomCard));

        MyCard discardCard = chooseCardToDiscard(copyCards);
        copyCards.remove(discardCard);
        copyDiscard.add(discardCard);
        Node result = new Node(copyDiscard, copyCards, copyDeck, opponentHand, depthTree, this.probMap);
        return result;
    }

    /*
    adjust probabilities for if the opponent would pick from the discard pile
     */
    public void simulationPickPile(Node parent){
        copyParent(parent);
        opponentHand = new ArrayList<>();
        MyCard chosen = discardPile.get((discardPile.size()-1)); // which card is on top of discard pile
        discardPile.remove(chosen);
        lookThroughKnownCards(chosen);
        increaseProb(chosen);
        cardsUnknown.add(chosen);
        this.setProbability(chosen, 1.0);
    }

    /*
    adjust probabilities for if the opponent picks from deck and thus rejects the discard pile
     */
   public void simulationPickDeck(Node parent) {
       copyParent(parent);
       opponentHand = new ArrayList<>();
       MyCard notChosen = discardPile.get(discardPile.size() - 1);
       lookThroughKnownCards(notChosen);
       decreaseProb(notChosen);
   }

    /*
    adjust probabilities for if the opponents discards a card
     */
    public void simulationDiscard(Node parent, MyCard discard){
        copyParent(parent);
        lookThroughKnownCards(discard);
        discardPile.add(discard);
        cardsUnknown.remove(discard);
        opponentHand.remove(discard);
        decreaseProb(discard);
    }

    public void increaseProb(MyCard topOfDiscard){
        for(int j = 0; j<cardsUnknown.size(); j++) {
            MyCard currentCard = cardsUnknown.get(j);

            if (currentCard.rank.index == topOfDiscard.rank.index) {
                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownSet));
                this.setProbability(currentCard, prob);
            }
            if (currentCard.suit.index == topOfDiscard.suit.index && Math.abs(currentCard.rank.index - topOfDiscard.rank.index) == 1) {
                double prob = this.getProbability(currentCard) / (1.0 / (2.0 * leftInUnknownRun));
                this.setProbability(currentCard, prob);
            }
        }
    }

    public void decreaseProb(MyCard discardCard){
        for(int j = 0; j<cardsUnknown.size(); j++){
            MyCard currentCard = cardsUnknown.get(j);

            if(currentCard.rank.index == discardCard.rank.index){ // decrease prob of cards that form set with discardCard
                double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownSet));
                this.setProbability(currentCard, prob);
            }
            // decrease prob of cards that form run with discardCard
            if(currentCard.suit.index == discardCard.suit.index && Math.abs(currentCard.rank.index - discardCard.rank.index) == 1){
                double prob = this.getProbability(currentCard) * (1.0 / (2.0 * leftInUnknownRun));
                this.setProbability(currentCard, prob);
            }
        }
    }

    /*
    Use probabilities to generate possibilities of the opponents hand with monte carlo simulation
    Make next layer of nodes with these possibilities
     */
    public List<Node> monteCarloSim(boolean pickOrDiscard){
        List<Node> nodes = new ArrayList<>();
        List<MyCard> opponentHandcur;

        for(int i= 1; i<= 100; i++){ // generate 100 possibilities
            if(pickOrDiscard){ // if opponent has picked a card, they still need to discard so they have 11 cards
                opponentHandcur = chooseRandomCards(cardsUnknown, 11);
            }
            else{ // if opponents has discarded or the first round the opponent has 10 cards
                opponentHandcur = chooseRandomCards(cardsUnknown, 10);
            }
            Node node = new Node(discardPile,hand, cardsUnknown, opponentHandcur, depthTree, this.probMap);
            nodes.add(node);
        }
        return nodes;
    }

    /*
    look through the cards that we do know in order to calculate how many cards are left in the unknown cards
    the probability increases when there are few cards in the unknown cards
    only look for the cards relevant to the action of the opponent, so if they pick 3 of Hearts
    look for all 3's of other suits and 2 , 4 of Hearts, since they can form a melt
     */
    public void lookThroughKnownCards(MyCard chosen){
        // 4 suits - the suit that was picked (e.g. Hearts)
        leftInUnknownSet = 3;
        // if chosen card is Ace or King you only have 1 'neighbour' for run
        if(chosen.rank.index == 0 || chosen.rank.index == 12){
            leftInUnknownRun = 1;
        }
        else{
            leftInUnknownRun = 2;
        }
        for(int k = 0; k< hand.size(); k++){ // look through hand for cards that form set with given card
            if(hand.get(k).rank.index == chosen.rank.index && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            // look through hand for cards that form run with given card
            if(hand.get(k).suit.index == chosen.suit.index && Math.abs(hand.get(k).rank.index - chosen.rank.index) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }
        for(int k = 0; k< discardPile.size(); k++){ // look through pile, same as hand
            if(discardPile.get(k).rank.index == chosen.rank.index && leftInUnknownSet > 0){
                leftInUnknownSet--;
            }
            if(discardPile.get(k).suit.index == chosen.suit.index && Math.abs(discardPile.get(k).rank.index - chosen.rank.index) == 1 && leftInUnknownRun > 0){
                leftInUnknownRun--;
            }
        }
    }

    /*
    Choose which card to discard by picking the card that decreases the deadwood value the most
    Returns the card that should be discarded
     */
    public static MyCard chooseCardToDiscard(List<MyCard> aHand){
        MyCard theCard = null;
        int highestVal = 1000;

        for(MyCard aCard : aHand){
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


    /*
        create opponent hand from unknown cards based on probabilities
        returns possible opponent hand
     */
    public List<MyCard> chooseRandomCards(List<MyCard> totalCards, int size){
        List<MyCard> copyList = deepCloneMyCardList(totalCards);
        List<MyCard> resultList = new ArrayList<>();
        for(int i = 0; i < copyList.size();i++){
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

    /*
    pick card that is likely to be in the opponents hand
     */
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

    /*
    copy parent situation to adjust and create children without messing up the parents information
     */
    public void copyParent(Node parent){
        this.discardPile = GametreeAI.deepCloneMyCardList(parent.discardPile);
        this.cardsUnknown = GametreeAI.deepCloneMyCardList(parent.unknownCards);
        this.hand = GametreeAI.deepCloneMyCardList(parent.hand);
        this.opponentHand = GametreeAI.deepCloneMyCardList(parent.opponentHand);
        this.depthTree = parent.getDepthTree() + 1;
        this.probMap = parent.getProbMap();
    }

    /*
    returns true if you want to pick from discard pile, otherwise pick from the deck
     */
    public boolean evaluate(MyCard discardCard, List<MyCard> hand){
        List<MyCard> current = Finder.findBestHandLayout(hand).viewAllCards();
        current.add(discardCard);
        if(chooseCardToDiscard(current) == discardCard){
            return false;
        }
        else{ return true; }
    }

    /*
    update probabilities based on the actual move of the opponent, used in minimaxpruning class
    discardCard is either the discarded card of the opponent, or the top card of the discard pile
    if the opponents picks deck and thus rejects the discard pile card
    return cardsunknown with updated probabilities
     */
    public List<MyCard> updateProbDiscard(Node current, MyCard discardCard){
        copyParent(current);
        lookThroughKnownCards(discardCard);
        decreaseProb(discardCard);
        return cardsUnknown;
    }

    /*
        update probabilities based on the actual move of the opponent, used in minimaxpruning class
        topOfDiscard is the top card of the discard pile that opponent picked
        return cardsunknown with updated probabilities
     */
    public List<MyCard> updateProbPickPile(Node current, MyCard topOfDiscard){
        copyParent(current);
        lookThroughKnownCards(topOfDiscard);
        increaseProb(topOfDiscard);
        cardsUnknown.add(topOfDiscard);
        this.setProbability(topOfDiscard, 1.0);
        return cardsUnknown;
    }

    double getProbability(MyCard aCard){
        return probMap[aCard.suit.index][aCard.rank.index];
    }

    void setProbability(MyCard card, double val){
        if(val!= val){
            System.out.println("We have reached Nan");
        }
        if(val >= 1.0){ probMap[card.suit.index][card.rank.index] = 1.0; }
        else if(val <= 0.0) { probMap[card.suit.index][card.rank.index] = 0.0; }
        else { probMap[card.suit.index][card.rank.index] = val; }
    }

    public double calcTotalProb(List<MyCard> setOfCards){
        double val = 0.0;
        for(MyCard aCard: setOfCards){
            val+=this.getProbability(aCard);
        }
        return val;
    }

    /*
    Deep clone cards so you can alter them for children without messing up the parents cards
    */
    public static List<MyCard> deepCloneMyCardList(List<MyCard> aList){
        List<MyCard> deepClone = new ArrayList<>();
        for(MyCard card:aList){
            deepClone.add(new MyCard(card));
        }
        return deepClone;
    }

    public static double randomNumberGenerator(double min, double max){
        return min+(Math.random()*(max-min));
    }

    public static List<MyCard> cloneMyCardList(List<MyCard> hand) {
        List<MyCard> attempt = new ArrayList<>();
        attempt.addAll(hand);
        return attempt;
    }
}