package GameLogic.Entities;

public class Layoff {

    public final MyCard card;
    public final Meld meld;

    public Layoff(MyCard card, Meld meld) {
        this.card = card;
        this.meld = meld;
    }

    // Getters

    public boolean isValid(){
        return meld.isValidWith(card);
    }
    @Override
    public boolean equals(Object other){
        if(!(other instanceof Layoff)){
            return false;
        }
        Layoff o = (Layoff) other;
        return card.equals(o.card) && meld.equals(o.meld);
    }
    public String toString() {
        return card + " in " + meld;
    }
}
