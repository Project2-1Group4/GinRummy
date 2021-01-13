package GameLogic.GameActions;

import GameLogic.Entities.Step;
import GameLogic.Entities.Layoff;
import GameLogic.Entities.MyCard;
import GameLogic.States.RoundState;

import java.util.ArrayList;
import java.util.List;

// IMMUTABLE
public class LayoffAction extends Action {
    public final List<Layoff> layoffs;

    public LayoffAction(int playerIndex, List<Layoff> layoffs) {
        super(Step.Layoff, playerIndex);
        this.layoffs = new ArrayList<>(layoffs);
    }

    // Getters

    @Override
    protected boolean specificSame(Object other) {
        LayoffAction o = (LayoffAction) other;
        int found = 0;
        for (int i = 0; i < layoffs.size(); i++) {
            for (int j = 0; j < o.layoffs.size(); j++) {
                if(layoffs.get(i).equals(o.layoffs.get(j))){
                    found++;
                    break;
                }
            }
        }
        return found==layoffs.size();
    }
    @Override
    public boolean specificCanDo(RoundState state) {
        List<MyCard> cards = new ArrayList<>();
        for (Layoff layoff : layoffs) {
            if(!layoff.isValid()){
                return false;
            }
            cards.add(layoff.card);
        }
        return state.cards(playerIndex).containsAll(cards);
    }
    @Override
    public String specificToString() {
        StringBuilder sb = new StringBuilder();
        if(layoffs.size()==0){
            return " couldn't lay off.";
        }
        sb.append(" laid off:");
        for (Layoff layoff : layoffs) {
            sb.append("\n- ").append(layoff);
        }
        return sb.toString();
    }

    // Setters

    @Override
    protected void specificDo(RoundState state) {
        for (Layoff layoff : layoffs) {
            state.cards(playerIndex).remove(layoff.card);
            state.cards(state.knocker()).add(layoff.card);
        }
    }
    @Override
    protected void specificUndo(RoundState state) {
        for (Layoff layoff : layoffs) {
            state.cards(state.knocker()).remove(layoff.card);
            state.cards(playerIndex).add(layoff.card);
        }
    }
}