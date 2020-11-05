package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

public interface PlayerGameInteractions {
	
	public Boolean chooseDeckOrPile(Card discardTop);
	
	public Card chooseCardToDiscard(Card drawnCard);
	
	public Boolean chooseToKnock();
	
	public void checkTopOfDiscard(Card discardTop);
	
	/*
	 * 	This also applies to the chooseDeckOrPile method
	 *  If false:
	 *  	Other player chose from the deck
	 *  If true:
	 *  	Other player chose from the discard
	 */
	public void otherPlayerDeckOrPile(boolean deckOrPile);
	
	// Don't know if the following two are necessary for the UI interactions, but still
	public void setHand(SetOfCards newHand);
	
	public void addPoints(int points);
	
}