package gameHandling;

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
	
	int undercutBonus = 25;
	int ginBonus = 25;
	
	public ActualGame() {
		this.deck = new SetOfCards(true);
		
		this.p1 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.p2 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.pile = new SetOfCards();
		
		this.pile.addCard(this.pile.drawTopCard());
		
	}
	
	/*
	 * Due to the way the decision is made on who should draw the first card and stuff
	 * First round is a separate method
	 */
	void firstRound() {
		/*
		 * Use the top card in the pile here
		 */
		
	}
	
	/*
	 * Needs to have a method to go into a different phase if someone knocks
	 */
	
	void playerTurn() {
		
		// Just a way to flip around who the player is at the end of a round
		this.player = !this.player;
	}
	
	/*
	 * Removing the deadwood from the player that did not knock
	 * Can be done automatically
	 */
	void layOff() {
		
	}
	
	void newRound() {
		
	}
	

}
