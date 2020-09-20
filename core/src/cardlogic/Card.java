package cardlogic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Card implements Comparable {
	
	public enum SUITS{
		SPADES,
		CLOVERS,
		HEARTS,
		DIAMONDS
	}
	
	private SUITS suit;
	private int value;
	private final Sprite front;
	private final Sprite back;
	private boolean turned;

	public final static float CARD_WIDTH = 1f;
	public final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;

	
	/*
	 * J = 11
	 * Q = 12
	 * K = 13
	 */
	
	public Card(int suit, int value, Sprite back, Sprite front) {
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
		this.back = back;
		this.front = front;
		back.setSize(CARD_WIDTH, CARD_HEIGHT);
		front.setSize(CARD_WIDTH, CARD_HEIGHT);
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

	// set position of card in field
	public void setPosition(float x, float y) {
		front.setPosition(x - 0.5f * front.getWidth(), y - 0.5f * front.getHeight());
		back.setPosition(x - 0.5f * back.getWidth(), y - 0.5f * back.getHeight());
	}

	public void turn() {
		turned = !turned;
	}

	public void draw(Batch batch) {
		if (turned)
			back.draw(batch);
		else
			front.draw(batch);
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
		return this.suit + Integer.toString(value);
	}

	public boolean equals(Card card) {
		return (this.value == card.value && this.suit.equals(card.suit));
	}
	
}
