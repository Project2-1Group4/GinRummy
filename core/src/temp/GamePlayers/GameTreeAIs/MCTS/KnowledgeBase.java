package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.KnockAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// SMH Unfortunate.
public class KnowledgeBase {
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

    public KnowledgeBase(State.StepInTurn step, int turn, List<MyCard> player, List<MyCard> otherPlayer, List<MyCard> deck, List<MyCard> unknown, Stack<MyCard> discard) {
        this.step = step;
        this.turn = turn;
        this.player = player;
        this.otherPlayer = otherPlayer;
        this.deck = deck;
        this.unknown = unknown;
        this.discardPile = discard;
        actions = new Stack<>();
        assert isValid();
    }

    public State toState(GamePlayer p1, GamePlayer p2, Integer seed){
        State curState = new StateBuilder()
                .setSeed(seed)
                .addPlayer(p1)
                .addPlayer(p2)
                .build();
        curState = Executor.startGame(500,curState);
        curState.deck = new ArrayList<>(deck);
        curState.playerTurn = turn;
        curState.playerStates.get(0).handLayout = new HandLayout(player);
        curState.playerStates.get(1).handLayout = new HandLayout(otherPlayer);
        curState.discardPile = (Stack<MyCard>) discardPile.clone();
        curState.stepInTurn = step;
        for (int i = 0; i < curState.playerStates.size(); i++) {
            curState.players.get(i).update(curState.playerStates.get(i).viewHandLayout());
            curState.players.get(i).newRound(curState.peekDiscardTop());
        }
        return curState;
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
            step = step.getNext();
            if(step == State.StepInTurn.Pick) {
                turn = turn == 0 ? 1 : 0;
            }
            actions.add(action);
        }
        else{
            System.out.println("L87 Knowledge !!!!! "+action);
        }
    }

    private boolean pickAction(PickAction pick){
        if(pick.deck){
            if(!deck.get(deck.size()-1).same(pick.card)){
                if(!remove(unknown, pick.card)){
                    return false;
                }
            }
            else{
                remove(deck, pick.card);
            }
        }
        else{
            if(!discardPile.peek().same(pick.card)){
                return false;
            }
            discardPile.pop();
        }
        if (pick.playerIndex == 0) {
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
            else if(unknown.size() !=0 && remove(unknown, discard.card)){
                discardPile.add(discard.card);
                return true;
            }
        }
        return false;
    }

    private boolean knockAction(KnockAction knock){
        if(knock.knock){
            if(turn==0){
                if(new HandLayout(player).getDeadwood()>=GameRules.minDeadwoodToKnock){
                    return false;
                }
            }
            else if(turn==1){
                if(unknown.size()==0 && new HandLayout(otherPlayer).getDeadwood()>=GameRules.minDeadwoodToKnock){
                    return false;
                }
                //TODO what to do when using unknowns instead of simulation?
            }
            finished = turn;
        }
        return true;
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
            System.out.println("L177 Knowledge !!!!! "+action);
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
            if(unknown.size()==0){
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

    public boolean isValid(){
        int sum = discardPile.size()+player.size()+otherPlayer.size()+ (unknown.size()==0?deck.size():unknown.size());
        return sum == 52;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turn: player ").append(turn).append(" step ").append(step).append("\n");
        sb.append("Player me: ").append(MyCard.toString(player)).append("\n");
        sb.append("Player other: ").append(MyCard.toString(otherPlayer)).append("\n");
        sb.append("Deck: ").append(MyCard.toString(deck)).append("\n");
        sb.append("Discard: ").append(MyCard.toString(discardPile)).append("\n");
        sb.append("Unknown: ").append(MyCard.toString(unknown));
        return sb.toString();
    }

    public KnowledgeBase copy(){
        return new KnowledgeBase(step, turn, new ArrayList<>(player), new ArrayList<>(otherPlayer), new ArrayList<MyCard>(deck),
                new ArrayList<MyCard>(unknown), (Stack<MyCard>) discardPile.clone());
    }

    public static KnowledgeBase getRandom(Integer seed){
        Random rd;
        if(seed==null){
            rd = new Random();
        }else{
            rd= new Random(seed);
        }
        List<MyCard> deck = MyCard.getBasicDeck();
        Executor.shuffleList(rd, 500, deck);
        List<MyCard> p = new ArrayList<>();
        List<MyCard> o = new ArrayList<>();
        Stack<MyCard> d = new Stack<>();
        for(int i=0;i<10;i++){
            p.add(deck.remove(0));
        }
        for(int i=0;i<3;i++){
            o.add(deck.remove(0));
            d.add(deck.remove(0));
        }
        List<MyCard> u = new ArrayList<>(deck);
        return new KnowledgeBase(State.StepInTurn.Pick, 0, p, o, new ArrayList<MyCard>(), u, d);
    }

    //TODO either delete or make clearer
    public static BeepBoopDifferences difference(KnowledgeBase kb1, KnowledgeBase kb2){
        List<MyCard> playerDiff = new ArrayList<>(MyCard.intraListDifference(kb1.player, kb2.player));
        List<MyCard> otherDiff = new ArrayList<>(MyCard.intraListDifference(kb1.otherPlayer, kb2.otherPlayer));
        List<MyCard> deckDiff = new ArrayList<>(MyCard.intraListDifference(kb1.deck, kb2.deck));
        List<MyCard> unknownDiff = new ArrayList<>(MyCard.intraListDifference(kb1.unknown, kb2.unknown));
        Stack<MyCard> discardDiff = new Stack<>();
        discardDiff.addAll(new ArrayList<>(MyCard.intraListDifference(kb1.discardPile, kb2.discardPile)));
        State.StepInTurn[] stepDiff = new State.StepInTurn[2];
        if(kb1.step!=kb2.step) {
            System.out.println("difference");
            stepDiff[0] = kb1.step;
            stepDiff[1] = kb2.step;
        }
        int[] turnDiff = new int[2];
        if(kb1.turn!=kb2.turn) {
            turnDiff[0] = kb1.turn;
            turnDiff[1] = kb2.turn;
        }
        Integer[] finishedDiff = new Integer[2];
        if(kb1.finished !=null && kb2.finished != null && !kb1.finished.equals(kb2.finished)
                || (kb1.finished==null && kb2.finished!=null)
                || (kb1.finished!=null && kb2.finished==null)){
            finishedDiff[0] = kb1.finished; finishedDiff[1] = kb2.finished;
        }

        return new BeepBoopDifferences(finishedDiff, stepDiff, turnDiff, playerDiff, otherDiff, deckDiff, unknownDiff, discardDiff);
    }
}
