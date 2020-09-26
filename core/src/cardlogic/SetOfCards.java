package cardlogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cardlogic.Card.SUITS;
import gameHandling.Player;

public class SetOfCards {
	
	List<Card> cards;
	
	/*
	 * Assumption is being done that top of deck is the last card in the pile
	 * And bottom is index 0
	 */

	public SetOfCards(boolean deck){
		
		if (deck) {
			this.cards = new ArrayList<>();
			
			for(int i=0; i<4; i++) {
				for(int j=1;j<14;j++) {
					this.cards.add(new Card(i,j));
				}
			}
			
			Collections.shuffle(this.cards);
			
		} else {
			this.cards = new ArrayList<>();
		}
		
	}

	public List<Card> toList(){
		return this.cards;
	}

	public void fromList(List<Card> cards){
		this.cards = cards;
	}
	
	public SetOfCards()
	{
		this(false);
	}

	public int size() {
		return cards.size();
	}
	
	public Card drawTopCard() {
		return this.cards.remove(this.cards.size()-1);
	}
	
	public void addCard(Card card) {
		this.cards.add(card);
	}
	
	public Card getCard(int ind) {
		return this.cards.get(ind);
	}
	
	public boolean discardCard(Card aCard) {
		return this.cards.remove(aCard);
	}

	public void removeCard() {this.cards.remove(0);}
	
	public void sortBySuits() {
		
		List<Card> resList = new ArrayList<>();
		
		for(SUITS aSuit : SUITS.values()) {
			List<Card> tempList = new ArrayList<>();
			
			/*
			for(int i=0;i<this.cards.size();i++) {
				Card aCard = this.cards.get(i);
				
				if (aCard.getSuit() == aSuit) {
					tempList.add(aCard);
					this.cards.remove(aCard);
				}
				
			}*/
			
			
			for(Card aCard : this.cards) {
				if (aCard.getSuit() == aSuit) {
					tempList.add(aCard);
					//this.cards.remove(aCard);
				}
				
			}
			
			Collections.sort(tempList);	
			resList.addAll(tempList);
			
		}
		
		this.cards = resList;
		
		
	}

	public boolean contains(Card card) {
		return (this.cards.contains(card));
	}

	public int getCardSetSize() {
		return this.cards.size();
	}
	
	public void sortByValue() {
		Collections.sort(this.cards);
	}
	
	public void shuffleCards() {
		Collections.shuffle(this.cards);
	}

	public static int getDeadWoodValue(List<Card> listCards){
		int value = 0;
		for (Card card: listCards) {
			value += card.getGinRummyValue();
		}
		System.out.println(value);
		return value;
	}
	
	public String toString() {
		return Arrays.toString(this.cards.toArray());
	}

	// Hand out card for each player
	public static SetOfCards handOutCard(int numberOdCard, SetOfCards deck) {
		deck.shuffleCards();
		SetOfCards setCard = new SetOfCards();
		for (int i = 0; i < numberOdCard; i++ ) {
			setCard.addCard(deck.drawTopCard());
		}
		return setCard;
	}
	
	//TESTING
	/*
	public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true);
		
		Player aPlayer = new Player(deck);
		
		List<List<Card>> runs = aPlayer.findRuns();
		
		List<List<Card>> sets = aPlayer.findSets();
		
		deck.shuffleCards();
		
		System.out.println("hey");
		
	}*/
	

}
