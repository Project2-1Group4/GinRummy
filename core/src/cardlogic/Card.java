package cardlogic;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class Card extends Renderable implements Comparable {

	public enum SUITS{
		SPADES,
		CLOVERS,
		HEARTS,
		DIAMONDS
	}
	
	private SUITS suit;
	//private int suitInt;
	private int value;
	private String nameCard;

	private TextureAtlas atlas;

	/*
	These two guys were originally final, I changed them to not be
	Done so that some of the refactoring for bug testing could be implemented
	 */

	private Sprite front;
	private Sprite back;
	private boolean turned;

	public final static float CARD_WIDTH = 1f;
	public final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;
	
	//final SUITS suitList = {
	
	/*
	 * J = 11
	 * Q = 12
	 * K = 13
	 */

	public Card(int suit, int value){
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
	}

	public void addVisualInfo(){
		if(this.atlas == null){
			atlas = new TextureAtlas();
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

		this.back = back;
		this.front = front;

		back.setSize(CARD_WIDTH, CARD_HEIGHT);
		front.setSize(CARD_WIDTH, CARD_HEIGHT);

		front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
		back.setPosition(-back.getWidth() * 0.5f, -back.getHeight() * 0.5f);

		// Why do we need nameCard? The toString method already does this thing
		this.nameCard = Integer.toString(value) + this.suit;

		material = new Material(
				TextureAttribute.createDiffuse(front.getTexture()),
				new BlendingAttribute(false, 1f),
				FloatAttribute.createAlphaTest(0.5f)
		);

		float[] vertices = convert(front.getVertices(), back.getVertices());
		short[] indices = new short[] {0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4 };


		meshPart.mesh = new Mesh(true, 8, 12, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		meshPart.mesh.setVertices(vertices);
		meshPart.mesh.setIndices(indices);
		meshPart.offset = 0;
		meshPart.size = meshPart.mesh.getNumIndices();
		meshPart.primitiveType = GL20.GL_TRIANGLES;
		meshPart.update();

	}
	
	public Card(int suit, int value,Sprite back, Sprite front)  {
		this(suit,value);

		this.back = back;
		this.front = front;

		back.setSize(CARD_WIDTH, CARD_HEIGHT);
		front.setSize(CARD_WIDTH, CARD_HEIGHT);

		front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
		back.setPosition(-back.getWidth() * 0.5f, -back.getHeight() * 0.5f);

		// Why do we need nameCard? The toString method already does this thing
		this.nameCard = Integer.toString(value) + this.suit;

		material = new Material(
				TextureAttribute.createDiffuse(front.getTexture()),
				new BlendingAttribute(false, 1f),
				FloatAttribute.createAlphaTest(0.5f)
		);

		float[] vertices = convert(front.getVertices(), back.getVertices());
		short[] indices = new short[] {0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4 };


		meshPart.mesh = new Mesh(true, 8, 12, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		meshPart.mesh.setVertices(vertices);
		meshPart.mesh.setIndices(indices);
		meshPart.offset = 0;
		meshPart.size = meshPart.mesh.getNumIndices();
		meshPart.primitiveType = GL20.GL_TRIANGLES;
		meshPart.update();
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


/*	public void setPosition(float x, float y) {
		front.setPosition(x - 0.5f * front.getWidth(), y - 0.5f * front.getHeight());
		back.setPosition(x - 0.5f * back.getWidth(), y - 0.5f * back.getHeight());
	}
*/
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
		
	public void turn() {
			turned = !turned;
		}

	public void draw(Batch batch) {
			if (turned)
				back.draw(batch);
			else
				front.draw(batch);
		}


	public String getNameCard() {
		return this.nameCard;
	}
	public SUITS getSuit() {
		return suit;
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


	
}
