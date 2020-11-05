package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;
import java.util.List;

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

    public void simulation (){

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
