package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GametreeAI {
    SetOfCards discardPile;
    SetOfCards hand;
    SetOfCards deck;
    Player player;

    public  GametreeAI (SetOfCards pile, SetOfCards cards, Player player){
        this.discardPile = pile;
        this.hand = cards;
        this.deck = new SetOfCards(true,false);
        this.player = player;
        for(int i = 0; i<hand.size(); i++){
           deck.discardCard(hand.getCard(i));
        }
        for(int i = 0; i<discardPile.size(); i++){
            deck.discardCard(discardPile.getCard(i));
        }
    }

    public void createTree (){
        Node first = new Node(discardPile, hand, deck);
        Node pass = first.addChild(new Node(discardPile,hand,deck));
        Node discard1 = first.addChild(pickDiscard(discardPile,hand));

    }

    public Node pickDiscard(SetOfCards current, SetOfCards discardPile){
        SetOfCards copyCards = current;
        SetOfCards copyDiscard = discardPile;
        copyCards.addCard(discardPile.getCard(discardPile.size()-1));
        copyDiscard.discardCard(discardPile.getCard(discardPile.size()-1));
        List<Card> copyList = copyCards.toList();
        Card discardCard = chooseCardToDiscard(copyList);
        copyCards.discardCard(discardCard);
        copyDiscard.addCard(discardCard);
        Node result = new Node(copyDiscard,copyCards,deck);
        return result;
    }

    // boolean true if discard pile is chosen
    // if true, top card of pile will be transfered after this method
    public void simulationPick(SetOfCards discardPile, SetOfCards hand, SetOfCards deck, boolean pick){
        SetOfCards currentpile = discardPile;
        SetOfCards currenthand = hand;
        SetOfCards currentdeck = deck;
        boolean pileOrDeck = pick;
        ArrayList<SetOfCards> possibilities = new ArrayList<>();
        if(pileOrDeck){
            Card chosen = discardPile.getCard((discardPile.size()-1));
            for(int j = 0; j<deck.size(); j++){
                // look if cards for set are in deck
                if(deck.getCard(j).getValue() == chosen.getValue()){
                    // cards with same value left in deck --> so not in hand current player or pile
                    int leftInDeck1 = 4;
                    // look through hand of AI, if this contains a card for set, prob of other cards increase
                    for(int k = 0; k< currenthand.size(); k++){
                        if(currenthand.getCard(k).getValue() == chosen.getValue()){
                            leftInDeck1--;
                        }
                    }
                    // look through pile, if this contains a card for set, prob of other cards increase
                    for(int k = 0; k< currentpile.size(); k++){
                        if(currentpile.getCard(k).getValue() == chosen.getValue()){
                            leftInDeck1--;
                        }
                    }
                    deck.getCard(j).setProb(1/(2*leftInDeck1));
                }
                // cards next to picked card for run
                int leftInDeck2 = 2;
                if(deck.getCard(j).getSuit() == chosen.getSuit() && deck.getCard(j).getValue() == chosen.getValue() +1){
                    // if card is in AI hand, it won't be in other players hand so prob = 0
                    for(int k = 0; k <currenthand.size(); k++){
                        if(currenthand.getCard(k).getSuit() == chosen.getSuit() && currenthand.getCard(k).getValue() == chosen.getValue() + 1 ){
                            leftInDeck2--;
                            deck.getCard(j).setProb(0.001);
                        }
                    }
                    // if card is in pile, it won't be in other players hand so prob = 0
                    for(int k = 0; k <currentpile.size(); k++){
                        if(currentpile.getCard(k).getSuit() == chosen.getSuit() && currentpile.getCard(k).getValue() == chosen.getValue() + 1 ){
                            leftInDeck2--;
                            deck.getCard(j).setProb(0.001);
                        }
                    }
                }
                if(deck.getCard(j).getSuit() == chosen.getSuit() && deck.getCard(j).getValue() == chosen.getValue() -1){
                    for(int k = 0; k <currenthand.size(); k++){
                        if(currenthand.getCard(k).getSuit() == chosen.getSuit() && currenthand.getCard(k).getValue() == chosen.getValue() - 1 ){
                            leftInDeck2--;
                            deck.getCard(j).setProb(0);
                        }
                    }
                    for(int k = 0; k <currentpile.size(); k++){
                        if(currentpile.getCard(k).getSuit() == chosen.getSuit() && currentpile.getCard(k).getValue() == chosen.getValue() - 1 ){
                            leftInDeck2--;
                            deck.getCard(j).setProb(0);
                        }
                    }
                }
                // if card is not in hand AI, give prob
                if(deck.getCard(j).getSuit() == chosen.getSuit() && deck.getCard(j).getValue() == chosen.getValue() +1){
                    if(leftInDeck2 == 2){
                        deck.getCard(j).setProb(0.25);
                    }
                    if(leftInDeck2 ==1){
                        if(deck.getCard(j).getProb() != 0){
                            deck.getCard(j).setProb(0.5);
                        }
                    }
                }
                //if card is not in pile, give prob
                if(deck.getCard(j).getSuit() == chosen.getSuit() && deck.getCard(j).getValue() == chosen.getValue() -1){
                    if(leftInDeck2 == 2){
                        deck.getCard(j).setProb(0.25);
                    }
                    if(leftInDeck2 ==1){
                        if(deck.getCard(j).getProb() != 0){
                            deck.getCard(j).setProb(0.5);
                        }
                    }
                }
            }
            for(int i= 1; i<= 1000; i++){
                SetOfCards handOpponent = new SetOfCards(false,false);
                for(int j = 0; j < deck.size(); j++){

                }
            }
        }
        else{
            Card notChosen = discardPile.getCard(discardPile.size()-1);
            for(int j = 0; j< deck.size(); j++){
                if(deck.getCard(j).getValue() == notChosen.getValue()){
                    deck.getCard(j).setProb(0);
                }
                if(deck.getCard(j).getSuit() == notChosen.getSuit() && deck.getCard(j).getValue() == notChosen.getValue() +1){
                    deck.getCard(j).setProb(0);
                }
                if(deck.getCard(j).getSuit() == notChosen.getSuit() && deck.getCard(j).getValue() == notChosen.getValue() -1){
                    deck.getCard(j).setProb(0);
                }
            }
            for(int i= 1; i<= 1000; i++){

            }
        }

    }

    // called after card from opponent is placed on discard pile
    public void simulationDiscard(SetOfCards discardPile, SetOfCards hand, SetOfCards deck){
        SetOfCards currentpile = discardPile;
        SetOfCards currenthand = hand;
        SetOfCards currentdeck = deck;
        Card discarded = discardPile.getCard(discardPile.size()-1);
        for(int j = 0; j<deck.size(); j++){
            //
            if(deck.getCard(j).getValue() == discarded.getValue()){
                deck.getCard(j).setProb(0);
            }
            if(deck.getCard(j).getSuit() == discarded.getSuit() && deck.getCard(j).getValue() == discarded.getValue() +1){
                deck.getCard(j).setProb(0);
            }
            if(deck.getCard(j).getSuit() == discarded.getSuit() && deck.getCard(j).getValue() == discarded.getValue() -1){
                deck.getCard(j).setProb(0);
            }
        }
        for(int i= 1; i<= 1000; i++){

        }
    }

    public static Card chooseCardToDiscard(List<Card> aHand){
        Card theCard = null;

        // It's just a low number, doesn't really matter what it is
        int highestVal = -1;

        for(Card aCard : aHand){
            // TODO: Bug-test here to make sure the copies are deep copies and not shallow
            List<Card> aList = new ArrayList<>(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now
            Player temp = new Player(new SetOfCards(aList));
            int resultingHand = temp.scoreHand();

            if(resultingHand>=highestVal){
                theCard = aCard;
                highestVal = resultingHand;
            }


        }


        return theCard;

    }
}
