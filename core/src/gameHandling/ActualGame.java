package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

public class ActualGame {
	
	Player p1;
	Player p2;
	SetOfCards deck;
	
	/*
	 * This can probably just be made into a card that's held here, it doesn't matter
	 */
	SetOfCards pile;
	
	/*
	 * player remembers whose turn it is
	 * If true, it's p1s turn
	 * If false, it's p2s
	 */
	boolean player;
	
	/*
	 * dealer remembers who "dealt" the cards
	 * If true, it's p1
	 * If false, it's p2
	 */
	boolean dealer;
	
	int undercutBonus = 25;
	int ginBonus = 25;
	
	public ActualGame() {
		this.deck = new SetOfCards(true);
		
		this.p1 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.p2 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.pile = new SetOfCards();
		
		this.pile.addCard(this.pile.drawTopCard());
		
		this.dealer = true;
		
	}
	
	/*
	 * Due to the way the decision is made on who should draw the first card and stuff
	 * First round is a separate method
	 * 
	 * The assumption is made that player 1 is the dealing player at the start
	 * So p2 is the one that decides whether to take the upcard
	 * 
	 * That assumption sucks tho
	 * TODO: Modify the code so that the dealing player gets changed every round (it's gotta alternate)
	 * 
	 */
	void firstRound() {
		/*
		 * Use the top card in the pile here
		 */
		
		Card firstCard = this.pile.drawTopCard();
		
		Player dealer;
		Player second;
		
		if(this.dealer) {
			dealer = this.p1;
			second = this.p2;
		} else {
			dealer = this.p2;
			second = this.p1;
		}
		
		// So the player is the person that wasn't dealing
		this.player = !this.dealer;
		
		// Inverts dealer for the next game
		this.dealer = !this.dealer;
		
		
		Card thrownCard = second.chooseCardToDiscard(firstCard);
		
		if (thrownCard.equals(firstCard)) {
			thrownCard = dealer.chooseCardToDiscard(firstCard);
			
			// Inverting the player
			this.player = !this.player;
			
			// If both players pass then the non-dealing player has to draw from the stock pile
			if(thrownCard.equals(firstCard)) {
				thrownCard = second.chooseCardToDiscard(this.deck.drawTopCard());
				
				// Inverting the player again
				this.player = !this.player;
				
			}
			
		}
		
	}
	
	/*
	 * Needs to have a method to go into a different phase if someone knocks
	 */
	
	void playerTurn() {
		
		Player pTurn;
		
		if(this.player) {
			pTurn = this.p1;
		} else {
			pTurn = this.p2;
		}
		
		// If false the player chose the deck
		// If true he chose the pile
		boolean deckOrPile = pTurn.chooseDeckOrPile();
		
		Card aCard;
		
		// So the player draws from either deck or discard pile
		// Then the card gets put back into the discard pile afterwards
		if(deckOrPile) {
			aCard = pTurn.chooseCardToDiscard(this.pile.drawTopCard());
		} else {
			aCard = pTurn.chooseCardToDiscard(this.deck.drawTopCard());
		}
		
		this.pile.addCard(aCard);
		
		/*
		 * So that's pretty much if for most of the turn.
		 * TODO: Add the methods for allowing the player to knock or go gin
		 * TODO: Method for draws, and other stuff.
		 * 
		 */
		
		if(this.deck.size() == 2) {
			//TODO: this is a condition for draws, so a round restart is needed
			this.newRound();
		}
		
		
		// Just a way to flip around who the player is at the end of a round
		this.player = !this.player;
	}
	
	/*
	 * Removing the deadwood from the player that did not knock
	 * Can be done automatically
	 */
	void layOff() {
		
	}
	
	// TODO: add a way of alternating who the first and second player are
	void newRound() {
		this.deck = new SetOfCards(true);
		p1.setHand(SetOfCards.handOutCard(10, this.deck));
		p2.setHand(SetOfCards.handOutCard(10, this.deck));
		
	}
	

}
