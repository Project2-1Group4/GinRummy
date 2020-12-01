package temp.Extra.GameTree.Bot;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class BotMemory {
    public final List<MyCard> player;
    public final List<MyCard> otherPlayer;
    public final List<MyCard> unknown;
    public final Stack<MyCard> discard;
    public int deckSize;

    public State.StepInTurn step;
    public int playerTurn;

    public BotMemory(List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> unknown, Stack<MyCard> discard, int deckSize, int playerTurn, State.StepInTurn step) {
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.unknown = unknown;
        this.discard = discard;
        this.deckSize = deckSize;
        this.step = step;
        this.playerTurn = playerTurn;
    }

    public int nbOfCards(){
        return player.size()+otherPlayer.size()+unknown.size()+discard.size();
    }
    public void execute(Action action){
        if (playerTurn == 0) {
            if(step == State.StepInTurn.Pick){
                if(((PickAction)action).deck){
                    if(!remove(unknown,((PickAction) action).card)){
                        System.out.println("ERRROR ERROR ERROR player pick");
                    }
                    player.add(((PickAction) action).card);
                    deckSize--;
                }else{
                    player.add(discard.pop());
                }
            }
            else if(step == State.StepInTurn.Discard){
                if(!remove(player,((DiscardAction)action).card)) {
                    System.out.println("ERRROR ERROR ERROR player discard");
                }
            }
            else{
                assert step == State.StepInTurn.KnockOrContinue;
            }
        }
        else{
            if(step == State.StepInTurn.Pick){
                if(((PickAction)action).deck){
                    if(!remove(unknown,((PickAction) action).card)){
                        System.out.println("ERRROR ERROR ERROR Other pick");
                    }
                    player.add(((PickAction) action).card);
                    deckSize--;
                }else{
                    otherPlayer.add(discard.pop());
                }
            }
            else if(step == State.StepInTurn.Discard){
                if(!remove(otherPlayer,((DiscardAction)action).card)){
                    if(!remove(unknown,((DiscardAction)action).card)){
                        System.out.println("ERRROR ERROR ERROR Other discard");
                    }
                }
            }
            else{
                assert step == State.StepInTurn.KnockOrContinue;
            }
        }
        nextStep();
    }

    private void nextStep(){
        playerTurn = step== State.StepInTurn.KnockOrContinue? playerTurn==0? 1 : 0 : playerTurn;
        step = step.getNext();
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

    public BotMemory copy(){
        return new BotMemory(new ArrayList<>(player),new ArrayList<>(otherPlayer), new ArrayList<MyCard>(unknown), (Stack<MyCard>)discard.clone(),deckSize, playerTurn, step);
    }

    public static BotMemory test(){
        List<MyCard> player = new ArrayList<>();
        List<MyCard> otherPlayer = new ArrayList<>();
        Stack<MyCard> discard = new Stack<>();
        Random rd = new Random(10);
        List<MyCard> deck = MyCard.getBasicDeck();
        Executor.shuffleList(rd,500,deck);
        for (int i = 0; i < GameRules.baseCardsPerHand; i++) {
            player.add(deck.remove(0));
        }
        discard.add(deck.remove(0));
        int deckSize = deck.size();
        List<MyCard> unknown = new ArrayList<>(deck);
        return new BotMemory(player,otherPlayer,unknown,discard,deckSize,0, State.StepInTurn.Pick);
    }
}
