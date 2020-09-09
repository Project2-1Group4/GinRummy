package cardlogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cardlogic.Card.SUITS;

public class SetOfCards {
	
	List<Card> cards;
	
	
	SetOfCards(boolean deck){
		
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
	
	public SetOfCards()
	{
		this(false);
	}

	public int size() {
		return cards.size();
	}
	Card drawTopCard() {

		return this.cards.remove(0);
	}
	
	void addCard(Card card) {
		this.cards.add(card);
	}
	
	void sortBySuits() {
		
		List<Card> resList = new ArrayList<>();
		
		for(SUITS aSuit : SUITS.values()) {
			List<Card> tempList = new ArrayList<>();
			
			for(Card aCard : this.cards) {
				if (aCard.getSuit() == aSuit) {
					tempList.add(aCard);
					this.cards.remove(aCard);
				}
				
			}
			
			Collections.sort(tempList);	
			resList.addAll(tempList);
			
		}
		
		this.cards = resList;
		
		
	}
	
	void sortByValue() {
		Collections.sort(this.cards);
	}
	
	void shuffleCards() {
		Collections.shuffle(this.cards);
	}
	

}
