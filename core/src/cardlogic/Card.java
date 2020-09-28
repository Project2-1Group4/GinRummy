package cardlogic;

public class Card implements Comparable {
	
	public enum SUITS{
		SPADES,
		CLOVERS,
		HEARTS,
		DIAMONDS
	}
	
	private SUITS suit;
	private int value;
	private String nameCard;
	
	//final SUITS suitList = {
	
	/*
	 * J = 11
	 * Q = 12
	 * K = 13
	 */
	
	public Card(int suit, int value) {
		if(suit == 0) {
			this.setSuit(SUITS.SPADES);
		} else if (suit ==1){
			this.setSuit(SUITS.CLOVERS);
		} else if (suit == 2) {
			this.setSuit(SUITS.HEARTS);
		} else if (suit == 3) {
			this.setSuit(SUITS.DIAMONDS);
		}
		
		this.setValue(value);
		this.nameCard = Integer.toString(value) + this.suit;
	}

	public String getNameCard() {
		return this.nameCard;
	}
	public SUITS getSuit() {
		return suit;
	}

	public void setSuit(SUITS suit) {
		this.suit = suit;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public int getGinRummyValue() {
		if (this.value>10) {
			return 10;
		} else {
			return this.value;
		}
	}

	@Override
	public int compareTo(Object arg0) {
		return this.value - ((Card)arg0).getValue();
	}

	
	public String toString() {
		return Integer.toString(value)+this.suit;
	}

	public boolean equals(Card card) {
		return (this.value == card.value && this.suit.equals(card.suit));
	}

	public static void main(String[] args) {
		Card c = new Card(2,13);
		System.out.println(c);
		System.out.println(c.getValue());
	}
	
}
