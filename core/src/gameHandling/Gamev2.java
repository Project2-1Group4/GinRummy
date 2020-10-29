package gameHandling;

import cardlogic.Card;
import cardlogic.CardBatch;
import cardlogic.SetOfCards;

import java.util.Collections;
import java.util.List;

public class Gamev2 {

    public Player player1;
    public Player player2;
    SetOfCards deck;
    SetOfCards pile;

    boolean dealer;

    /*
    If boolean is true, it's player 1's turn
    If false, it's player 2's turn
     */


    public boolean player;

    int undercutBonus = 25;
    int ginBonus = 25;
    int pointObjective = 100;

    public Gamev2(String name1, String name2, CardBatch cardsPlayer1, CardBatch cardsPlayer2, CardBatch deck, CardBatch discardPile) {

        this.deck = deck;

        this.player1 = new Player(name1, cardsPlayer1);

        this.player2 = new Player(name2, cardsPlayer2);

        this.pile = discardPile;

        this.dealer = true;
        this.player = false;
    }

    public Gamev2(Player p1, Player p2, SetOfCards deck, SetOfCards discard){
        this.player1 = p1;
        this.player2 = p2;
        this.deck = deck;
        this.pile = discard;

    }

    // Update to allow for the game to be played internally
    // Update both player and GameV2 to allow the game to be played on "command line"-ish terms
    // I'll probably change the return function in some way to display who won, and stuff like that
    void playGame(){
        boolean newRound = true;

        this.firstRound();
        while(newRound){

            Player turnP = this.findCurrentPlayer();

            boolean deckOrNot = turnP.chooseDeckOrPile(this.pile.peekTopCard());

            Card discarded = turnP.chooseCardToDiscard(this.drawCardGame(deckOrNot));

            this.pile.addCard(discarded);

            if (turnP.chooseToKnock()){
                newRound = this.knock();

                if(newRound){
                    // First round set up thingy goes here again to simulate the first round properly
                    this.resetGame();
                    
                    this.firstRound();

                }
                
            }

        }

    }


    // TODO: make sure I'm properly changing around the dealer as well when a round ends.

    Player findCurrentPlayer(){
        this.player = !this.player;
        
        // It's inverted because the previous command kinda "resets" stuff
        if(!this.player){
            return this.player1;
        } else {
            return this.player2;
        }
    }

    // true if deck
    public boolean drawCard(boolean deckOrPile){
        if(deck.size() <=2) {
            return false;
        }

        Player aPlayer;

        // Player is inverted whenever a card is drawn out
        // So here its the opposite way around
        if(!this.player){
            aPlayer = player1;
        } else {
            aPlayer = player2;
        }

        if(deckOrPile){
            aPlayer.addCard(this.deck.drawTopCard());
        } else {
            aPlayer.addCard(this.pile.drawTopCard());
        }

        return true;
    }

    // Method checks out, should bug test later just in case
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

    public Card drawCardGame(boolean deckOrPile){
        if(deck.size()<= 2){
            return null;
        }

        if(deckOrPile){
            return this.deck.drawTopCard();
        } else {
            return this.pile.drawTopCard();
        }

    }

    public void addCardToDiscard(Card aCard){
        if(this.player){
            player1.hand.discardCard(aCard);
        } else {
            player2.hand.discardCard(aCard);
        }

        this.pile.addCard(aCard);
        this.player = !this.player;
    }

    /*
    If true then there's another round
    Else game's done and winner can be calculated
    */
    public boolean knock(){

        Player pKnock;
        Player pWait;

        /*
        Because I change the player everytime a card is discarded, that means the player who knocked
        Is the one that's not the current player
         */

        if(this.player) {
            pKnock = this.player2;
            pWait = this.player1;
        } else {
            pKnock = this.player1;
            pWait= this.player2;
        }

        int pKnockVal = pKnock.scoreHand();

        if(pKnockVal ==0) {
            int pWaitVal = pWait.scoreHand();

            int pointsToAdd = pWaitVal + this.ginBonus;
            pKnock.addPoints(pointsToAdd);

        } else {
            List<List<Card>> pTurns_melds = pKnock.getMelds();

            int pWaitVal = layOff(pWait, pTurns_melds);

            // Here we calculate who has the lowest score, before deciding who gets the points

            if(pWaitVal<=pKnockVal) {
                // Here's where the undercut happens
                int pointsToAdd = pKnockVal-pWaitVal + this.undercutBonus;
                pWait.addPoints(pointsToAdd);

            } else {
                int pointsToAdd = pWaitVal-pKnockVal;
                pKnock.addPoints(pointsToAdd);
            }

        }

        return this.gameFinishCalculate();

    }

    /*
    If the game's done then return false
    If there's a new round return true
     */
    boolean gameFinishCalculate() {

        if((this.player1.getScore() >= this.pointObjective) || (this.player2.getScore() >= this.pointObjective)) {
            return false;
        } else {
            return true;
        }

    }

    /*
    New round changes the internal logic so that a new round can be played
    There might be some problems due to how the round restart interacts with the rest of the game
     */

    public void newRound(CardBatch deck, CardBatch pile, CardBatch p1hand, CardBatch p2hand){
        this.dealer = !this.dealer;
        this.player = !this.dealer;
        /*
        At this point I need to restart the:
        Deck, Discard Pile, and the player's hands
        But I wanna talk it out to make sure this method interacts properly with the rest
         */

        this.deck = deck;
        this.pile = pile;

        this.player1.hand = p1hand;
        this.player2.hand = p2hand;

    }

    public void newRound(){
        this.deck = new SetOfCards(true,false);
        this.pile = new SetOfCards();

        SetOfCards p1HandNew = SetOfCards.handOutCard(10, this.deck);
        SetOfCards p2HandNew = SetOfCards.handOutCard(10, this.deck);

        this.player1.hand = p1HandNew;
        this.player2.hand = p2HandNew;

        this.pile.addCard(deck.drawTopCard());

    }

    /*
    This is a complete restart:
    AKA nothing is stored, and all of the player's scores are set to 0
     */
    void resetGame(){

    }

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

                            aMeld.add(0,toCheck);

                        } else {
                            aMeld.add(toCheck);
                        }


                        pWaits_deadWood.remove(toCheck);
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
                    aMeld.add(toCheck);
                    pWaits_deadWood.remove(toCheck);



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

    public static void main(String[] args){

        SetOfCards deck = new SetOfCards(true, false);
        SetOfCards pile = new SetOfCards(false, false);

        SetOfCards p1Deck = new SetOfCards(false, false);

        for(int i =1; i<7; i++){
            p1Deck.addCard(new Card(0,i+2));
        }

        p1Deck.addCard(new Card(1,13));
        p1Deck.addCard(new Card(2,13));
        p1Deck.addCard(new Card(3,13));
        p1Deck.addCard(new Card(3,2));

        SetOfCards p2Deck = new SetOfCards(false, false);

        
        p2Deck.addCard(new Card(0,10));
        p2Deck.addCard(new Card(0,9));

        p2Deck.addCard(new Card(0,13));

        for(int i=1; i<8;i++){
            p2Deck.addCard(new Card(1,i));
        }

        for(Card aCard: p1Deck.toList()){
            deck.discardCard(aCard);
        }

        for(Card aCard: p2Deck.toList()){
            deck.discardCard(aCard);
        }


        Player p1 = new Player("Player 1",p1Deck);
        Player p2 = new Player("Player 2",p2Deck);

        p2.bestCombination();
        System.out.println("permutation: "+p1.getPermutation(p1.findSets(p1.hand)));
        p1.bestCombination();
        System.out.println("chosen "+p1.bestCombination);
        Gamev2 game = new Gamev2(p1,p2,deck,pile);

        game.knock();

    }

}
