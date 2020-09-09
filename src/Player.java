import cardlogic.Card;
import cardlogic.SetOfCards;

import java.util.ArrayList;

public class Player {
    private String name;
    private SetOfCards hand;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new SetOfCards();
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
