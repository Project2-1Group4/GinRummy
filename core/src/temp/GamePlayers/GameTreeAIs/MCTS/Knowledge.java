package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GameRules;

import java.util.List;
import java.util.Stack;

// SMH Unfortunate.
public class Knowledge {
    public final List<MyCard> player;
    public final List<MyCard> otherPlayer;
    public final List<MyCard> unknown;
    public final List<MyCard> deck;
    public final Stack<MyCard> discardPile;
    private final Stack<Action> actions;
    public State.StepInTurn step;
    // 0 = me, 1 = other
    public int turn;
    public Integer finished = null;

    public Knowledge(List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> deck,List<MyCard> unknown, Stack<MyCard> discard) {
        assert (deck == null && unknown != null) || (deck != null && unknown == null);
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.deck = deck;
        this.unknown = unknown;
        this.discardPile = discard;
        actions = new Stack<>();
    }

    public void execute(Action action){
        assert step == action.getStep();
        assert action.playerIndex == turn;
        boolean executed = false;
        if(action instanceof PickAction){
            executed = pickAction((PickAction)action);
        }
        else if(action instanceof DiscardAction){
            executed = discardAction((DiscardAction)action);
        }
        else if(action instanceof KnockAction){
            executed = knockAction((KnockAction)action);
        }
        if(executed) {
            step.getNext();
            if(step == State.StepInTurn.KnockOrContinue) {
                turn = turn == 0 ? 1 : 0;
            }
            actions.add(action);
        }
        else{
            System.out.println("L55 Knowledge !!!!!");
        }
    }

    private boolean pickAction(PickAction pick){
        if(pick.deck){
            if(deck!=null){
                if(!deck.get(deck.size()-1).same(pick.card)){
                    return false;
                }
                remove(deck, pick.card);
            }
            else{
                if(!remove(unknown, pick.card)){
                    return false;
                }
            }
        }
        else{
            if(!discardPile.peek().same(pick.card)){
                return false;
            }
            discardPile.pop();
        }
        if (turn == 0) {
            player.add(pick.card);
        } else {
            otherPlayer.add(pick.card);
        }
        return true;
    }

    private boolean discardAction(DiscardAction discard){
        if(turn==0){
            if(remove(player, discard.card)){
                discardPile.add(discard.card);
                return true;
            }
        }
        else{
            if(remove(otherPlayer,discard.card)){
                discardPile.add(discard.card);
                return true;
            }
        }
        return false;
    }

    private boolean knockAction(KnockAction knock){
        if(knock.knock){
            if(turn==0){
                if(new HandLayout(player).getDeadwood()<=GameRules.minDeadwoodToKnock){
                    finished = turn;
                    return true;
                }
            }
            else if(turn==1){
                if(deck!=null && new HandLayout(otherPlayer).getDeadwood()<=GameRules.minDeadwoodToKnock){
                    finished = turn;
                    return true;
                }
                //TODO what to do when using unknowns instead of simulation?
            }
        }
        return false;
    }

    public void undo(Action action){
        assert action.same(actions.peek());
        boolean executed = false;
        if(action instanceof PickAction){
            executed = undoPick((PickAction)action);
        }
        else if(action instanceof DiscardAction){
            executed = undoDiscard((DiscardAction)action);
        }
        else if(action instanceof KnockAction){
            executed = undoKnock((KnockAction)action);
        }
        if(executed) {
            step = action.getStep();
            if(step == State.StepInTurn.KnockOrContinue) {
                turn = turn == 0 ? 1 : 0;
            }
            actions.pop();
        }
        else{
            System.out.println("L142 Knowledge !!!!!");
        }
    }

    private boolean undoPick(PickAction pick){
        if(pick.playerIndex==0){
            if(!remove(player, pick.card)){
                return false;
            }
        }
        else{
            if(!remove(otherPlayer, pick.card)){
                return false;
            }
        }
        if(pick.deck){
            if(deck!=null){
                deck.add(pick.card);
            }
            else{
                unknown.add(pick.card);
            }
        }
        else{
            discardPile.add(pick.card);
        }
        return true;
    }

    private boolean undoDiscard(DiscardAction discard){
        if(discardPile.peek().same(discard.card)){
            if(discard.playerIndex==0){
                player.add(discardPile.pop());
            }
            else{
                otherPlayer.add(discardPile.pop());
            }
            return true;
        }
        return false;
    }

    private boolean undoKnock(KnockAction knock){
        if(knock.knock){
            finished = null;
        }
        return true;
    }

    private boolean remove(List<MyCard> list, MyCard card){
        for (int i = 0; i < list.size(); i++) {
            if(card.same(list.get(i))){
                list.remove(i);
                return true;
            }
        }
        return false;
    }
}
