package cardlogic;

import java.util.ArrayList;
import java.util.Collections;

public class SetOfCards {
	
	ArrayList<Card> cards;
	
	
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
	
	SetOfCards()
	{
		this(false);
	}
	
	Card drawTopCard() {

		return this.cards.remove(0);
	}
	
	void addCard(Card card) {
		this.cards.add(card);
	}
	
	void shuffleCards() {
		Collections.shuffle(this.cards);
	}
	

}
