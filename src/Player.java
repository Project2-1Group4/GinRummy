import cardlogic.Card;

import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Card> hand;
    private int score;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<Card>();
        score = 0;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getHandSize() {
        return hand.size();
    }
    /* getcard
    setcard
     */
    public int getScore() {
        return this.score;
    }
    public void evaluateScore(int value) { //subtract or add score in case of loss or win
        score += value;
    }



}
