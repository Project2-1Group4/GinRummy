package cardlogic;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class CardBatch extends SetOfCards implements RenderableProvider, Disposable {
    Renderable renderable;
    Mesh mesh;
    MeshBuilder meshBuilder;

    Material material;

    public CardBatch(Material material, boolean constructor){
        super(constructor, true);


        final int maxNumberOfCards = 52;
        final int maxNumberOfVertices = maxNumberOfCards * 8;
        final int maxNumberOfIndices = maxNumberOfCards * 12;
        mesh = new Mesh(false, maxNumberOfVertices, maxNumberOfIndices,
                VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
        meshBuilder = new MeshBuilder();
        renderable = new Renderable();
        renderable.material = material;

    }

    public static CardBatch handOutCard(int numberOdCard, CardBatch deck) {
        deck.shuffleCards();
        CardBatch setCard = new CardBatch(deck.material, false);
        for (int i = 0; i < numberOdCard; i++ ) {
            setCard.addCard(deck.drawTopCard());
        }
        return setCard;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        meshBuilder.begin(mesh.getVertexAttributes());
        meshBuilder.part("cards", GL20.GL_TRIANGLES, renderable.meshPart);
        for (Card card : cards) {
            meshBuilder.setVertexTransform(card.transform);
            meshBuilder.addMesh(card.vertices, card.indices);
        }

        meshBuilder.end(mesh);
        renderables.add(renderable);
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
