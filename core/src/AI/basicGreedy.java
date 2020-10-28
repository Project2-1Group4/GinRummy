package AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cardlogic.Card;
import cardlogic.SetOfCards;
import gameHandling.Player;

public class basicGreedy extends Player{
    public basicGreedy(SetOfCards cards){
        super(cards);
    }

    /*
    So the gist of it is that the method will look at the card in the discard pile
    It'll then evaluate if it can create a new hand that has a lower score than it's current hand
    If the card can lower the current score:
        return false
    Else
        return true

    And if it returns true that means it should pick from the deck instead (as there's a chance something good is there)
    */

    boolean evaluate(Card discardCard){
        
        return false;
    }

    /*
    This'll go through the current hand and find the card that'll lower the value the most when discarded
    */

    public static Card chooseCardToDiscard(List<Card> aHand){
        Card theCard = null;

        // It's just a high number, doesn't really matter what it is
        int lowestVal = 100000;

        for(Card aCard : aHand){
            // TODO: Bug-test here to make sure the copies are deep copies and not shallow
            List<Card> aList = new ArrayList<>(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now
            Player temp = new Player(new SetOfCards(aList));
            int resultingHand = temp.scoreHand();

            if(resultingHand<lowestVal){
                theCard = aCard;
            }


        }


        return theCard;

    }
    
}
