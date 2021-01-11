package temp.GameLogic.Entities;

public enum Step{
    Pick(0, "Pick from deck or discard."),
    Discard(1, "Discard a card."),
    KnockOrContinue(2, "Knock?"),
    LayoutConfirmation(3, "Lock in your layout."),
    Layoff(4, "Choose cards to layoff in the knocker's melds."),
    EndOfRound(5, "Game is done.");
    public int index;
    public String question;
    Step(int index, String question){
        this.index = index;
        this.question = question;
    }
    public Step getNext(){
        switch(this){
            case Pick: return Discard;
            case Discard: return KnockOrContinue;
            case KnockOrContinue: return Pick;
            case LayoutConfirmation: return Layoff;
            default: return EndOfRound;
        }
    }
}
