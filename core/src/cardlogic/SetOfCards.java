package cardlogic;

import cardlogic.Card.SUITS;
import gameHandling.Player;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.MyCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetOfCards {
	
	List<Card> cards;
	//TextureAtlas atlas = new TextureAtlas("carddeck.atlas");;
	
	/*
	 * Assumption is being done that top of deck is the last card in the pile
	 * And bottom is index 0
	 */


	/*
	boolean deck is true if deck is created, for hand / discardpile will be false
	boolean visual is true if we want to add all visual info to the cards, only false for testing
	 */

	public SetOfCards(boolean deck, boolean visual){
		this.cards = new ArrayList<>();

		if (deck) {
			for(int i=0; i<4; i++) {
				for(int j=1;j<14;j++) {
					Card card = new Card(i,j);
					this.cards.add(card);
				}
			}

		} else {

		}

		if(visual){
			for(Card aCard: this.cards){
				aCard.addVisualInfo();
			}
		}
		Collections.shuffle(this.cards);
	}

	public SetOfCards(HandLayout layout){
		this(layout.viewAllCards(), false);
	}

	public SetOfCards(List<MyCard> cardList, boolean garbage){
		this.cards = new ArrayList<>();
		for(MyCard myCard: cardList){
			cards.add(new Card(myCard));
		}

	}

	public SetOfCards(List<Card> someCards){
		this.cards = Player.copyList(someCards);
	}

	public List<Card> toList(){
		return new ArrayList<>(this.cards);
	}

	public void fromList(List<Card> cards){
		this.cards = cards;
	}
	
	public SetOfCards()
	{
		this(false,false);
	}

	public int size() {
		return cards.size();
	}
	
	public Card drawTopCard() {
		return this.cards.remove(this.cards.size()-1);
	}

	public Card peekTopCard(){
		return this.cards.get(this.cards.size()-1);
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
	
	public void sortBySuits() {
		
		List<Card> resList = new ArrayList<>();
		
		for(SUITS aSuit : SUITS.values()) {
			List<Card> tempList = new ArrayList<>();
			
			for(Card aCard : this.cards) {
				if (aCard.getSuit() == aSuit) {
					tempList.add(aCard);
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
	
	public String toString() {
		return Arrays.toString(this.cards.toArray());
	}



	// Hand out card for each player
	public static SetOfCards handOutCard(int numberOfCard, SetOfCards deck) {
		deck.shuffleCards();
		SetOfCards setCard = new SetOfCards();
		for (int i = 0; i < numberOfCard; i++ ) {
			setCard.addCard(deck.drawTopCard());
		}
		return setCard;
	}

	public static void main(String[] args) {
		SetOfCards deck = new SetOfCards(true, false);
		System.out.println(deck.size());
	}

}
