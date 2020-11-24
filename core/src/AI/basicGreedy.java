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
    Determines how many points the hand should have before the player knocks
    Default is 10 'cause that's the minimum knock value according to general rules
    */

    int knockValue = 10;

    /*
    Because the round resets when there's two cards,
    It might be beneficial to pre-emptively knock
    This sets how many cards should be left in the deck before the knock
    Default value is three so that the player can have a 'safety margin' for knocking
    */

    int cardsInDeckLimit = 3;

    /*
    So the gist of it is that the method will look at the card in the discard pile
    It'll then evaluate if it can create a new hand that has a lower score than it's current hand

    If the card can lower the current score:
        return true
    Else
        return false

    And if it returns false that means it should pick from the deck instead (as there's a chance something good is there)
    */

    boolean evaluate(Card discardCard){
        List<Card> current = this.hand.toList();
        
        current.add(discardCard);
        if(chooseCardToDiscard(current) == discardCard){
            return false;
        }
        else{
            return true;
        }
        
    }

    public boolean ChooseDeckOrPile(Card aCard){
    	return this.evaluate(aCard);        
    }
    
    public Card chooseCardToDiscard(Card aCard) {
    	List<Card> current = this.hand.toList();
    	
    	current.add(aCard);
    	
    	return chooseCardToDiscard(aCard);
    	
    }
    
    public boolean chooseToKnock() {
    	int currentHand = this.scoreHand();
    	
    	if(currentHand <= this.knockValue) {
    		return true;
    	} else {
    		return false;
    	}
    	
    	
    }

    /*
    This'll go through the current hand and find the card that'll lower the value the most when discarded
    */

    public static Card chooseCardToDiscard(List<Card> aHand){
        Card theCard = null;

        // It's just a low number, doesn't really matter what it is
        int highestVal = -1;

        for(Card aCard : aHand){
            // TODO: Bug-test here to make sure the copies are deep copies and not shallow
            List<Card> aList = new ArrayList<>(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now
            int resultingHand = evaluateHand(aList);

            // less deadwood => less value on hand so the resultingHand should be less than highestVal...
            if(resultingHand>=highestVal){
                theCard = aCard;
                highestVal = resultingHand;
            }


        }


        return theCard;

    }
    // We already have static method for scoring a hand, dont need it here (more reuseab)
    public static int evaluateHand(List<Card> aHand){
        Player temp = new Player(new SetOfCards(aHand));
        return temp.scoreHand();
    }
    
}
