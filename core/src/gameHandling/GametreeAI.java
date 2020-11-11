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
        Node first = new Node(discardPile, hand, cardsUnknown, new SetOfCards(false, false));
        Node pass = first.addChild(new Node(discardPile,hand,cardsUnknown, new SetOfCards(false, false)));
        createNodes(pass);
        Node discard1 = first.addChild(pickDiscard(discardPile,hand));
        createNodes(discard1);

    }

    public void createNodes(Node parent){
        simulationPick(true);
        ArrayList<Node> nodes = monteCarloSim();
        for(int i = 0; i< nodes.size(); i++){
            parent.addChild(nodes.get(i));
        }
        // simulate discard possibilities
        // calculate prob for all those possibilities
        simulationDiscard();
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
        Node result = new Node(copyDiscard, copyCards, cardsUnknown, new SetOfCards(false, false));
        return result;
    }

    // boolean true if discard pile is chosen
    // if true, top card of pile will be transferred after this method
    public void simulationPick(boolean pileOrDeck){
        // if opponent picks card from pile
        if(pileOrDeck){
            Card chosen = discardPile.getCard((discardPile.size()-1));
            // card must be in opponents hand
            chosen.setProb(1);
            opponentHand.addCard(chosen);
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

    public void simulationDiscard(){
        // OPPONENT DISCARDS CARD
        Card discarded = discardPile.getCard(discardPile.size()-1);
        for(int j = 0; j<cardsUnknown.size(); j++){
            // decrease prob of cards that form set with discarded card
            if(cardsUnknown.getCard(j).getValue() == discarded.getValue()){
                cardsUnknown.getCard(j).setProb(0);
            }
            // decrease prob of cards that form run with discarded card
            if(cardsUnknown.getCard(j).getSuit() == discarded.getSuit() && Math.abs(cardsUnknown.getCard(j).getValue() - discarded.getValue()) == 1){
                cardsUnknown.getCard(j).setProb(0);
            }
        }
    }



    public ArrayList<Node> monteCarloSim(){
        Random randomGenerator = new Random();
        ArrayList<Node> nodes = new ArrayList<>();
        // these are cards that are for sure in opponents hand (e.g. chosen from discard pile by opponent)
        SetOfCards opponentHandBasis = opponentHand;
        // simulate 100 times
        for(int i= 1; i<= 100; i++){
            for(int j = 0; j<cardsUnknown.size(); j++){
                int value = randomGenerator.nextInt(100);
                // SET BOUND TO RIGHT VALUE!
                if(cardsUnknown.getCard(j).getProb() * value > 25 && opponentHand.size() < 10 ){
                    opponentHand.addCard(cardsUnknown.getCard(j));
                }
            }
            Node node = new Node(hand, discardPile, cardsUnknown, opponentHand);
            nodes.add(node);
            opponentHand = opponentHandBasis;
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


    public static List<Card> choose10RandomCards(List<Card> totalCards){
        List<Card> copyList = Player.copyList(totalCards);

        List<Card> resultList = new ArrayList<>();
        
        for(Card aCard : copyList){
            // Might be changed to is greater than or equals
            if(aCard.getProb() == 1){
                resultList.add(aCard);
                copyList.remove(aCard);
            }

        }



        while(resultList.size() < 10){

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

}