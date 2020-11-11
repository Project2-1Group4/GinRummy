package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GametreeAI {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards cardsUnknown;
    Player player;
    private int leftInUnknownSet = 4;
    private int leftInUnknownRun = 2;
    private SetOfCards opponentHand;


    public  GametreeAI (SetOfCards pile, SetOfCards cards, Player player){
        this.discardPile = pile;
        this.hand = cards;
        this.cardsUnknown = new SetOfCards(true,false);
        this.player = player;
        for(int i = 0; i<hand.size(); i++){
           cardsUnknown.discardCard(hand.getCard(i));
        }
        for(int i = 0; i<discardPile.size(); i++) {
            cardsUnknown.discardCard(discardPile.getCard(i));
        }
        opponentHand = new SetOfCards();
    }

    public void createTree (){
        Node first = new Node(discardPile, hand, cardsUnknown, opponentHand);
        Node pass = first.addChild(new Node(discardPile,hand,cardsUnknown, opponentHand));
        createNodesOpponent(pass, true);
        Node discard1 = first.addChild(pickDiscard(discardPile,hand));
        createNodesOpponent(discard1, false);

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
                simulationDiscard(nodesPile.get(i));
                List<Node> nodesDiscard1 = monteCarloSim(false);
                for(int j = 0; j< nodesDiscard1.size(); j++){
                    nodesPile.get(i).addChild(nodesDiscard1.get(i));
                    // call createNodesAI for next layer in tree
                }
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
                        // call createNodesAI for next layer in tree
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
                // call createNodesOpponent for next layer in tree
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
                    // call createNodesOpponent for next layer in tree
                }

            }
        }

    }

    public List<Card> makeDeck(List<Card> unknown, List<Card> opponent){
        List<Card> deck = unknown;
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
        Node result = new Node(copyDiscard, copyCards, cardsUnknown, new SetOfCards());
        return result;
    }

    // boolean true if discard pile is chosen
    // if true, top card of pile will be transferred after this method
    public void simulationPick(boolean pileOrDeck, Node parent){
        // if opponent picks card from pile
        discardPile = parent.discardPile;
        cardsUnknown = parent.unknownCards;
        hand = parent.hand;
        opponentHand = parent.opponentHand;
        if(pileOrDeck){
            Card chosen = discardPile.getCard((discardPile.size()-1));
            // card must be in opponents hand
            chosen.setProb(1);
            cardsUnknown.addCard(chosen);
            discardPile.discardCard(chosen);
            // look through known cards to see how many usefull one (melts) are left in unknown setofcards
            lookThroughKnownCards(chosen);
            // update prob of unknown cards
            for(int j = 0; j<cardsUnknown.size(); j++){
                // increase prob for cards that form set with chosen card
                if(cardsUnknown.getCard(j).getValue() == chosen.getValue()){
                    //BAYES RULEE!! change this!!
                    cardsUnknown.getCard(j).setProb(1000);
                }
                // increase prob for cards that form run with chosen card
                else if(cardsUnknown.getCard(j).getSuit() == chosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - chosen.getValue()) == 1){
                    // BAYES RULEE!! change this!
                    cardsUnknown.getCard(j).setProb(1000);
                }
                // update prob of cards that don't form melts with chosen card
                else{
                    cardsUnknown.getCard(j).setProb(10000);
                }
            }
        }
        // if opponent picks card from deck and therefore doesn't use card from pile
        else{
            Card notChosen = discardPile.getCard(discardPile.size()-1);
            for(int j = 0; j< cardsUnknown.size(); j++){
                // decrease prob of cards that form set with not chosen card
                if(cardsUnknown.getCard(j).getValue() == notChosen.getValue()){
                    // Bayes rule
                    cardsUnknown.getCard(j).setProb(0);
                }
                // decrease prob of cards that form run with chosen card
                if(cardsUnknown.getCard(j).getSuit() == notChosen.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - notChosen.getValue()) == 1){
                    // Bayes rule
                    cardsUnknown.getCard(j).setProb(0);
                }
            }
        }
    }

    public void simulationDiscard(Node parent){
        discardPile = parent.discardPile;
        cardsUnknown = parent.unknownCards;
        hand = parent.hand;
        opponentHand = parent.opponentHand;
        Card discard = chooseCardToDiscard(parent.opponentHand.toList());
        discardPile.addCard(discard);
        opponentHand.discardCard(discard);
        // OPPONENT DISCARDS CARD
        for(int j = 0; j<cardsUnknown.size(); j++){
            // decrease prob of cards that form set with discarded card
            if(cardsUnknown.getCard(j).getValue() == discard.getValue()){
                cardsUnknown.getCard(j).setProb(0);
            }
            // decrease prob of cards that form run with discarded card
            if(cardsUnknown.getCard(j).getSuit() == discard.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discard.getValue()) == 1){
                cardsUnknown.getCard(j).setProb(0);
            }
        }
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
            for(int j= 0; j< 10; j++){
                opponentHand.addCard(opponentHandcur.get(j));
            }
            Node node = new Node(hand, discardPile, cardsUnknown, opponentHand);
            nodes.add(node);
        }
        return nodes;
    }



    // method that looks in known cards (hand + discardpile) for usefull cards
    public void lookThroughKnownCards(Card chosen){
        // 4 suits
        leftInUnknownSet = 4;

        // if chosen card is Ace or King you only have 1 'neighbour' for run
        if(chosen.getValue() == 1 || chosen.getValue() == 13){
            leftInUnknownRun = 1;
        }
        else{
            leftInUnknownRun = 2;
        }

        for(int k = 0; k< hand.size(); k++){
            // look through hand for cards that form set with given card
            if(hand.getCard(k).getValue() == chosen.getValue()){
                leftInUnknownSet--;
            }
            // look through hand for cards that form run with given card
            if(hand.getCard(k).getSuit() == chosen.getSuit() && Math.abs(hand.getCard(k).getValue() - chosen.getValue()) == 1){
                leftInUnknownRun--;
            }
        }
        // look through pile, same as hand
        for(int k = 0; k< discardPile.size(); k++){
            if(discardPile.getCard(k).getValue() == chosen.getValue()){
                leftInUnknownSet--;
            }
            if(discardPile.getCard(k).getSuit() == chosen.getSuit() && Math.abs(discardPile.getCard(k).getValue() - chosen.getValue()) == 1){
                leftInUnknownRun--;
            }
        }
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
        for(Card aCard : copyList){
            // Might be changed to is greater than or equals
            if(aCard.getProb() == 1){
                resultList.add(aCard);
                copyList.remove(aCard);
            }
        }

        while(resultList.size() < size){

            Card toSave = pickRandomCard(copyList);
            resultList.add(toSave);

            // TODO: Check to make sure that this method doesn't affect the original list of cards
            copyList.remove(toSave);
        }
        return resultList;
    }

    public static double calcTotalProb(List<Card> setOfCards){
        double val = 0;

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
        List<Card> current = hand.toList();
        current.add(discardCard);
        if(chooseCardToDiscard(current) == discardCard){
            return false;
        }
        else{
            return true;
        }
    }

}