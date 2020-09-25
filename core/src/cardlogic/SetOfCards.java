package cardlogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cardlogic.Card.SUITS;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import gameHandling.Player;

public class SetOfCards {
	
	List<Card> cards;
	TextureAtlas atlas;
	
	/*
	 * Assumption is being done that top of deck is the last card in the pile
	 * And bottom is index 0
	 */
	
	public SetOfCards(boolean deck){
		atlas = new TextureAtlas("carddeck.atlas");
		if (deck) {
			this.cards = new ArrayList<>();

			String suit = "clubs";
			for(int i=0; i<4; i++) {
				for(int j=1;j<14;j++) {
					if(i == 0) {
						suit = "spades";
					} else if (i ==1){
						suit = "clubs";
					} else if (i == 2) {
						suit = "hearts";
					} else if (i == 3) {
						suit = "diamonds";
					}
					this.cards.add(new Card(i,j, atlas.createSprite("back", 1), atlas.createSprite(suit, j)));
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
	
	public static int scoreGinRummy(List<Card> cardSet) {
		int totScore = 0;
		
		for(Card aCard:cardSet) {
			totScore+=aCard.getGinRummyValue();
		}
		return totScore;
		
	}
	
	public static boolean findIfCardMakesMeld(Card aCard, List<Card> setOrRun) {
		return findIfCardMakesRun(aCard, setOrRun)||findIfCardMakesSet(aCard,setOrRun);
	}
	
	/*
	 * So the idea is that the difference between the first and last card in a run has to be one
	 * For the card to be valid in the run
	 * But! This can be at the beginning or end of the run
	 * So I try to take that into account
	 */
	
	public static boolean findIfCardMakesRun(Card aCard, List<Card> run) {
		boolean temp1 = Math.abs(aCard.getValue()-run.get(0).getValue())==1;
		boolean temp2 = Math.abs(aCard.getValue()-run.get(run.size()-1).getValue())==1;
		return temp1||temp2;
	}
	
	/*
	 * This method assumes that the given list is a set
	 * So the card just has to have the same value as one of the cards in the set
	 */
	public static boolean findIfCardMakesSet(Card aCard, List<Card> set) {
		return aCard.getValue() == set.get(0).getValue();
	}

	public void removeCard() {this.cards.remove(0);}
	public List<Card> toList(){
		return this.cards;
	}

	public void fromList(List<Card> cards){
		this.cards = cards;
	}
	
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
	
	/*
	 * TODO: Bugtest this small method to make sure the equals method isn't going to screw me over
	 */
	
	public boolean contains(Card aCard) {
		return this.cards.contains(aCard);
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
	
	public String toString() {
		return Arrays.toString(this.cards.toArray());
	}

	// Hand out card for each player
	public static SetOfCards handOutCard(int numberOdCard, SetOfCards deck) {
		deck.shuffleCards();
		SetOfCards setCard = new SetOfCards();
		for (int i = 0; i < numberOdCard; i++ ) {
			setCard.addCard(deck.drawTopCard());
			//deck.removeCard();
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
