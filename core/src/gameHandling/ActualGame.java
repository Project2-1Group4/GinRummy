package gameHandling;

import java.util.Collections;
import java.util.List;

import cardlogic.Card;
import cardlogic.SetOfCards;

public class ActualGame {
	
	Player p1;
	Player p2;
	SetOfCards deck;
	
	boolean setEnd = false;
	
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
	int pointObjective = 100;
	
	public ActualGame() {
		this.deck = new SetOfCards(true);
		
		this.p1 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.p2 = new Player(SetOfCards.handOutCard(10, this.deck));
		
		this.pile = new SetOfCards();
		
		this.pile.addCard(this.pile.drawTopCard());
		
		this.dealer = true;
		
	}
	
	/*
	 * The winning player is returned
	 * I'm assuming ties aren't possible in each game
	 * And I'm not calculating things like line bonus, or shutout bonus, stuff like that
	 */
	
	Player playGame() {
		
		
		
		boolean gameFinished = false;
		
		while(!gameFinished) {
			this.firstRound();
			
			boolean newRound = true;
			
			while(newRound) {
				newRound = this.playerTurn();
			}
			
			gameFinished = this.gameFinishCalculate();
			
		}
		
		/*
		 * So just return the winner here somehow
		 */
		
		return this.winningPlayer();
		
	}
	
	/*
	 * Due to the way the decision is made on who should draw the first card and stuff
	 * First round is a separate method
	 * Dealer is inverted in this method
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
	 * Returns true if there should be a new round
	 * Returns false if the round is finished and there should be a new set
	 */
	
	boolean playerTurn() {
		
		// pTurn is the active player's turn
		// pWait is the other player
		// There are times where the other's reaction matters, so he needs to be saved
		// Might be more memory efficient ways of doing it, but it doesn't matter much
		Player pTurn;
		Player pWait;
		
		if(this.player) {
			pTurn = this.p1;
			pWait = this.p2;
		} else {
			pTurn = this.p2;
			pWait= this.p2;
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
		 * TODO: Method for draws, and other stuff.
		 */
		
		
		// TODO: Make this a separate method, all of this shouldn't be here, it's kinda cluttered
		
		if(pTurn.chooseToKnock()) {
			// TODO: Add what happens when there's a knock!
			
			int pTurnsVal = pTurn.scoreHand();
			
			if(pTurnsVal ==0) {
				int pWaitVal = pWait.scoreHand();
				
				pTurn.addPoints(pWaitVal+this.ginBonus);
				
				/*
				 * So at this point the round's over
				 * So there should be a round end and a reset of the round
				 * Not sure how to implement it correctly though, so for now i'm just putting roundEnd
				 * TODO: Modify this so that round end and new round start are done properly.
				 */
				
			} else {
				List<List<Card>> pTurns_melds = pTurn.getMelds();
				
				int pWaitVal = layOff(pWait, pTurns_melds);
				
				/*
				 * Here we calculate who has the lowest score, before deciding who gets the points
				 */
				
				if(pWaitVal<=pTurnsVal) {
					// Here's where the undercut happens
					pWait.addPoints(pTurnsVal-pWaitVal+this.undercutBonus);
					
				} else {
					pTurn.addPoints(pWaitVal-pTurnsVal);
				}
				
			}
			
			return this.setEnd;
			
			
			// TODO: If there's no deadwood on the knocking player, pWait can't remove their deadwood
			
		}
		
		
		if(this.deck.size() == 2) {
			//this.newRound();
			
			// TODO: Make sure that the round is actually finished properly
			// I'm pretty sure it's wrong with how it's going from one to the other method
			// But for now this works as placeholder code
			return this.setEnd;
		}
		
		
		
		return this.roundEnd();
	}
	
	/*
	 * Removing the deadwood from the player that did not knock
	 * Can be done automatically
	 * Returns the score of the remaining cards
	 */
	static int layOff(Player pWait, List<List<Card>> pTurnsMelds) {
		
		List<Card> pWaits_deadWood = pWait.findDeadwood();
		
		// The idea behind this is okay, but there are two important logic bugs that can bite us in the ass
		// 1: Two or more deadwood cards together can make a run. For example 2 & 3 could make a run with 4, 5 & 6
		//		Program might detect that 3 makes the run, but might screw up with 2.
		//		To aleviate this I made a silly loop that will repeat if there was some kind of change to pTurn's melds
		//		Not perfect, and in theory only affects runs.
		//		But it should also work
		// 2: Related to 1, but adding a card to one of the melds could be a worse play
		//		Ex: deadwood of 2 & 3 for pWait, pTurn has set (3,3,3) and run (4,5,6)
		//			Adding 2 and 3 to the run is the best play, but program might chose to add 3 to the set and leave 2 hanging
		//		Current fix makes it so that the program will first find if any cards belong to runs, and then decide if the belong to sets
		//		I think that should fix it, but I'm not completely sure that it's logically sound
		//	TODO: Make sure these two exceptions don't end up screwing everything.
		
		boolean runsFound = true;
		
		while(runsFound) {
			runsFound = false;
			
			for(int i=0; i<pWaits_deadWood.size();i++) {
				
				Card toCheck = pWaits_deadWood.get(i);
				
				// Now that I think about it, there might be bug here with the iterable
				// Java is rather stingy with modifying it
				// TODO: Make sure there's no bugs here!
				for(List<Card> aMeld:pTurnsMelds) {
					if(SetOfCards.findIfCardMakesRun(toCheck, aMeld)) {
						runsFound = true;
						
						// This check is done to determine whether the card must be added to the start or end of the meld
						
						// This sort might be unnecessary, in theory the runs are already sorted
						// Still, done for safety 'cause I don't feel like bugtesting
						Collections.sort(aMeld);
						
						
						// If the card to check goes at the start fo the meld, then it'll be one less than the first card
						// TODO: Bug test to make sure that the melds are organized the way I think they are
						if(aMeld.get(0).getValue()-toCheck.getValue() == 1) {
							
							// Should add the card to check at the start
							aMeld.add(0,(pWaits_deadWood.remove(i)));
							
						} else {
							aMeld.add(pWaits_deadWood.remove(i));
						}
						
						// The i-- is done to make sure no values are missed
						// So we go back one index
						i--;
						
						// Break is done because the card is used, so there's nothing else to check
						break;
						
					}
				}
				
			}
			
		}
		
		// And now the method to check if any of the cards belong to any sets
		for(int i=0;i<pWaits_deadWood.size();i++) {
			
			Card toCheck = pWaits_deadWood.get(i);
			
			for(List<Card> aMeld:pTurnsMelds) {
				if(SetOfCards.findIfCardMakesSet(toCheck, aMeld)) {
					
					// This check is done to determine whether the card must be added to the start or end of the meld
					
					// This sort might be unnecessary, in theory the runs are already sorted
					// Still, done for safety 'cause I don't feel like bugtesting
					Collections.sort(aMeld);
					
					// If the card to check goes at the start fo the meld, then it'll be one less than the first card
					// TODO: Bug test to make sure that the melds are organized the way I think they are
					if(aMeld.get(0).getValue()-toCheck.getValue() == 1) {
						
						// Should add the card to check at the start
						aMeld.add(0,(pWaits_deadWood.remove(i)));
						
					} else {
						aMeld.add(pWaits_deadWood.remove(i));
					}
					
					// The i-- is done to make sure no values are missed
					// So we go back one index
					i--;
					
					// Break is done because the card is used, so there's nothing else to check
					break;
					
				}
			}
			
		}
		
		return SetOfCards.scoreGinRummy(pWaits_deadWood);
		
	}
	
	/*
	 * After every round to check if there's a new round incoming or if a player knocked, smthng like that
	 * TODO: Implement method
	 */
	
	boolean roundEnd() {
		this.player = !this.player;
		return true;
	}
	
	/*
	 * Method to be used after every set to add up the points and check whether the game's over
	 * Will return true if there's another round to play, if not it'll return false
	 * TODO: Actually implement the method
	 */
	
	boolean gameFinishCalculate() {
		this.player = !this.player;
		
		if((this.p1.getScore() >= this.pointObjective) || (this.p2.getScore() >= this.pointObjective)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	// TODO: add a way of alternating who the first and second player are
	// I don't think that's necessary now that I think about it
	// This just resets the deck and both player's hands
	void newRound() {
		this.deck = new SetOfCards(true);
		p1.setHand(SetOfCards.handOutCard(10, this.deck));
		p2.setHand(SetOfCards.handOutCard(10, this.deck));
		
	}
	
	/*
	 * Returns whoever is the winning player
	 * In this case, I'm always assuming it's the one with the highest score
	 */
	
	Player winningPlayer() {
		if(this.p1.scoreHand()>this.p2.scoreHand()) {
			return p1;
		} else {
			return p2;
		}
		
	}
	
}
