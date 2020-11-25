package cardlogic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import temp.GameLogic.MyCard;

public class Card extends Actor implements Comparable {

	/*
	0 = spades
	1 = clovers
	2 = hearts
	3 = diamonds
	 */
	public enum SUITS{
		SPADES,
		CLOVERS,
		HEARTS,
		DIAMONDS
	}

	private static TextureAtlas atlas;
	private SUITS suit;
	private int suitVal;
	private int value;
	private String nameCard;

	private Sprite front;
	private Sprite back;
	private boolean turned;

	// location of card in field
	private float pointX;
	private float pointY;

	private double prob = 1.0/41.0;

	public float[] vertices;
	public short[] indices;
	public final Matrix4 transform = new Matrix4();

	public final static float CARD_WIDTH = 1f;
	public final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;
	
	/*
	 * J = 11
	 * Q = 12
	 * K = 13
	 */

	
	public Card(int suit, int value){
		this.setSuit(suit);
		this.setValue(value);
	}

	public Card(MyCard aCard){
		this.setValue(aCard.rank.index);

		this.setSuit(aCard.suit.index);

	}

	private void setSuit(int suit){
		this.suitVal = suit;
		if(suit == 0) {
			this.setSuit(SUITS.SPADES);
		} else if (suit ==1){
			this.setSuit(SUITS.CLOVERS);
		} else if (suit == 2) {
			this.setSuit(SUITS.HEARTS);
		} else if (suit == 3) {
			this.setSuit(SUITS.DIAMONDS);
		} else{
			System.out.println("An invalid value of " + suit + " has been given as a suit");
		}

	}

	public void addVisualInfo(){
		if(atlas == null){
			this.atlas = new TextureAtlas("carddeck.atlas");
		}

		Sprite back = atlas.createSprite("back", 1);

		String suit = null;
		if(this.suit == SUITS.SPADES) {
			suit = "spades";
		} else if (this.suit == SUITS.CLOVERS){
			suit = "clubs";
		} else if (this.suit == SUITS.HEARTS) {
			suit = "hearts";
		} else if (this.suit == SUITS.DIAMONDS) {
			suit = "diamonds";
		}

		Sprite front = atlas.createSprite(suit, this.value);

		assert(front.getTexture() == back.getTexture());

		back.setSize(CARD_WIDTH, CARD_HEIGHT);
		front.setSize(CARD_WIDTH, CARD_HEIGHT);

		front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
		back.setPosition(-back.getWidth() * 0.5f, -back.getHeight() * 0.5f);

		this.nameCard = Integer.toString(value) + this.suit;

		vertices = convert(front.getVertices(), back.getVertices());
		indices = new short[] {0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4 };

	}

	void setSuit(SUITS aSuit){
		this.suit = aSuit;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

	private static float[] convert(float[] front, float[] back) {

		return new float[]{
				front[Batch.X2], front[Batch.Y2], 0, 0, 0, 1, front[Batch.U2], front[Batch.V2],
				front[Batch.X1], front[Batch.Y1], 0, 0, 0, 1, front[Batch.U1], front[Batch.V1],
				front[Batch.X4], front[Batch.Y4], 0, 0, 0, 1, front[Batch.U4], front[Batch.V4],
				front[Batch.X3], front[Batch.Y3], 0, 0, 0, 1, front[Batch.U3], front[Batch.V3],

				back[Batch.X1], back[Batch.Y1], 0, 0, 0, -1, back[Batch.U1], back[Batch.V1],
				back[Batch.X2], back[Batch.Y2], 0, 0, 0, -1, back[Batch.U2], back[Batch.V2],
				back[Batch.X3], back[Batch.Y3], 0, 0, 0, -1, back[Batch.U3], back[Batch.V3],
				back[Batch.X4], back[Batch.Y4], 0, 0, 0, -1, back[Batch.U4], back[Batch.V4]};
	}

	public SUITS getSuit() {
		return suit;
	}

	public int getSuitVal(){
		return this.suitVal;
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

	public float getPointX() {
		return pointX;
	}

	public float getPointY() {
		return pointY;
	}

	public void setPointX(float pointX) {
		this.pointX = pointX;
	}

	public void setPointY(float pointY) {
		this.pointY = pointY;
	}

	public double getProb() { return prob; }

	// TO DO: change else statement!!
	public void setProb(double prob) {
		if(prob <= 1.0 && prob >= 0.0){
			this.prob = prob;
		}
		else if(prob > 1.0){
			this.prob = 1.0;
		}

	}


	
}
