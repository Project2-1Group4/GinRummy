package GameLogic.Entities;

public class Layoff {

    public final MyCard card;
    public final Meld meld;

    public Layoff(MyCard card, Meld meld) {
        this.card = card;
        this.meld = meld;
    }

    public boolean isValid(){
        return meld.isValidWith(card);
    }

    public boolean same(Layoff other){
        return card.same(other.card) && meld.same(other.meld);
    }

    public String toString() {
        return card + " in " + meld;
    }
}
