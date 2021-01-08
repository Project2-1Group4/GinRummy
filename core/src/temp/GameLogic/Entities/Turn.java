package temp.GameLogic.Entities;


import temp.GameLogic.Logic.Finder;
import temp.GameLogic.States.RoundState;

import static temp.GameLogic.Entities.Step.Layoff;
import static temp.GameLogic.Entities.Step.*;

// IMMUTABLE
public class Turn {
    public final int playerIndex;
    public final Step step;

    public Turn(Step step, int index){
        this.step = step;
        this.playerIndex = index;
    }
    public Turn(Turn turn){
        this(turn.step, turn.playerIndex);
    }

    // Finding next turn

    public Turn getNext(RoundState state) {
        if(step == EndOfRound){
            return this;
        }
        Step nextStep = getNextStep(state);
        int nextIndex = getNextPlayer(state);
        return new Turn(nextStep, nextIndex);
    }
    private Step getNextStep(RoundState state) {
        switch(step){
            case Pick: return Discard;
            case Discard: return KnockOrContinue;
            case KnockOrContinue:
                if(state.hasBeenKnocked()) return LayoutConfirmation;
                else return Pick;
            case LayoutConfirmation:
                if(state.knocker().equals(nextIndex(state.numberOfPlayers()))) {
                    if (Finder.findBestHandLayout(state.getCards(state.knocker())).deadwoodValue() == 0) {
                        return EndOfRound;
                    }
                    return Layoff;
                }
            case Layoff:
                if(state.knocker().equals(nextIndex(state.numberOfPlayers()))) return EndOfRound;
            default: return step;
        }
    }
    private int getNextPlayer(RoundState state) {
        switch(step){
            case Pick:
            case Discard:
                return playerIndex;
            case KnockOrContinue:
                if(state.hasBeenKnocked()) return playerIndex;
            default: return nextIndex(state.numberOfPlayers());
        }
    }
    private int nextIndex(int nbOfPlayers){
        return (playerIndex+1)%nbOfPlayers;
    }
    // Finding previous turn

    public Turn getPrevious(RoundState state){
        if(state.actions.size()==0){
            System.out.println(this+" getPrevious() Turn");
            return this;
        }
        switch(step){
            case Pick: return new Turn(KnockOrContinue, previousIndex(state.numberOfPlayers()));
            case Discard: return new Turn(Pick, playerIndex);
            case KnockOrContinue: return new Turn(Discard, playerIndex);
            case LayoutConfirmation:
                if(state.knocker().equals(playerIndex)){
                    return new Turn(KnockOrContinue, playerIndex);
                }
                else{
                    return new Turn(LayoutConfirmation, previousIndex(state.numberOfPlayers()));
                }
            case Layoff:
                if(state.knocker().equals(playerIndex)){
                    return new Turn(LayoutConfirmation, playerIndex);
                }
                else{
                    return new Turn(Layoff, previousIndex(state.numberOfPlayers()));
                }
            case EndOfRound:
                return new Turn(state.knockedWithGin()? LayoutConfirmation: Layoff, previousIndex(state.numberOfPlayers()));
        }
        return null;
    }
    private int previousIndex(int nbOfPlayers){
        return playerIndex==0 ? nbOfPlayers-1:playerIndex-1;
    }

    public void validityCheck(RoundState state){
        if(playerIndex>state.numberOfPlayers()){
            throw new IndexOutOfBoundsException("Current player ("+playerIndex+") > Total players ("+state.numberOfPlayers()+")");
        }
        if((step == LayoutConfirmation || step == Layoff) && state.knocker()==null){
            throw new AssertionError("Got to "+step+" without knocking");
        }
        if(state.knocker()!=null && (step == Pick || step == Discard)){
            throw new AssertionError("Cannot "+step+" after someone has knocked.");
        }
    }
    public String toString(){
        String turnString = "Player "+playerIndex+". ";
        String stepString = step==null? "Done." : step.question;
        return  turnString+stepString;
    }
}