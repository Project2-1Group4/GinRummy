package GamePlayers;

import GameLogic.Game;
import GameLogic.GameActions.Action;
import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Entities.MyCard;
import GameLogic.Entities.Step;
import GameLogic.States.CardsInfo;
import GamePlayers.GameTreeAIs.MCTS.MCTSv1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public abstract class MemoryPlayer extends GamePlayer {
    protected CardsInfo cardsMemory;
    protected Step step;
    protected int turn;
    protected int round;

    public MemoryPlayer() {
        round = 0;
    }

    // Game <=> Player interaction

    @Override
    public final Boolean knockOrContinue() {
        step = Step.KnockOrContinue;
        return KnockOrContinue();
    }
    public abstract Boolean KnockOrContinue();
    @Override
    public final Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        step = Step.Pick;
        return PickDeckOrDiscard(remainingCardsInDeck, topOfDiscard);
    }
    public abstract Boolean PickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard);
    @Override
    public final MyCard discardCard() {
        step = Step.Discard;
        return DiscardCard();
    }
    public abstract MyCard DiscardCard();
    @Override
    public void newRound(MyCard topOfDiscard) {
        super.newRound(topOfDiscard);
        List<List<MyCard>> players = new ArrayList<>();
        for (int i = 0; i < Game.numberOfPlayers(this); i++) {
            players.add(new ArrayList<MyCard>());
        }
        players.set(index, getHand());
        Stack<MyCard> discard = new Stack<>();
        discard.add(topOfDiscard);
        Stack<MyCard> unknown = MyCard.getBasicDeck();
        unknown.removeAll(players.get(index));
        unknown.remove(topOfDiscard);
        cardsMemory = new CardsInfo(players, new Stack<MyCard>(),unknown,discard);
        round++;
    }
    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        boolean found = cardsMemory.players.get(discardAction.playerIndex).remove(discardAction.card);
        if(!found){
            if(discardAction.playerIndex==index) {
                System.out.println("uuh oh MemoryPlayer playerDiscarded()");
            }
            else{
                cardsMemory.unassigned.remove(discardAction.card);
            }
        }
        cardsMemory.discardPile.add(discardAction.card);
    }
    @Override
    public void playerPicked(PickAction pickAction) {
        if(pickAction.playerIndex>=cardsMemory.players.size()){
            cardsMemory.players.add(new ArrayList<MyCard>());
        }
        if(pickAction.deck){
            if(pickAction.card()!=null){
                assert pickAction.playerIndex==index;
                cardsMemory.unassigned.remove(pickAction.card());
                cardsMemory.players.get(pickAction.playerIndex).add(pickAction.card());
            }
        }
        else{
            assert pickAction.card().equals(cardsMemory.peekDiscard());
            cardsMemory.players.get(pickAction.playerIndex).add(cardsMemory.discardPile.pop());
        }
    }
    @Override
    public void executed(Action action) {
        if (action instanceof PickAction) {
            playerPicked((PickAction) action);
        } else if (action instanceof DiscardAction) {
            playerDiscarded((DiscardAction) action);
        }
    }

    // Getters

    /**
     * Returns a list of what cards the enemy might have using a heuristic
     * -add all cards directly in the vicinity of what he picked 2x,
     * -add all cards 2 away, 1x
     * @param player index of player you want
     * @return weighted list
     */
    protected List<MyCard> getWeightedList(int player){
        HashMap<MyCard, Integer> vicinity = getVicinity(cardsMemory.getCards(player));
        List<MyCard> keysToRemove = new ArrayList<>();
        for (MyCard card : vicinity.keySet()) {
            if(cardsMemory.unassigned.contains(card)){
                continue;
            }
            keysToRemove.add(card);
        }
        for (MyCard key : keysToRemove) {
            vicinity.remove(key);
        }
        List<MyCard> weightedList = new ArrayList<>(cardsMemory.unassigned);
        for (MyCard key : vicinity.keySet()) {
            weightedList.remove(key);
            int weight = vicinity.get(key);
            for (int i = 0; i < weight; i++) {
                weightedList.add(key);
            }
        }
        return weightedList;
    }
    private HashMap<MyCard, Integer> getVicinity(List<MyCard> c){
        HashMap<MyCard, Integer> vicinity = new HashMap<>();
        for (MyCard myCard : c) {
            HashMap<MyCard, Integer> dirV = getVicinity(myCard);
            for (MyCard dirVKey : dirV.keySet()) {
                if(vicinity.containsKey(dirVKey)){
                    vicinity.put(dirVKey, vicinity.get(dirVKey)+dirV.get(dirVKey));
                }
                else {
                    vicinity.put(dirVKey, dirV.get(dirVKey));
                }

            }
        }
        return vicinity;
    }
    private HashMap<MyCard, Integer> getVicinity(MyCard c){
        HashMap<MyCard, Integer> directVicinity = new HashMap<>();
        for (MyCard.Suit suit : MyCard.Suit.values()) {
            if(suit!=c.suit){
                directVicinity.put(new MyCard(suit, c.rank), 2);
            }
        }
        for (int i = -2; i <= 2; i++) {
            if(i != 0) {
                assert c.rank != null;
                if (c.rank.index+i>=0 && c.rank.index+i < MyCard.Rank.values().length) {
                    directVicinity.put(new MyCard(c.suit, MyCard.Rank.getRank(c.rank.index + i)), 3 - Math.abs(i));
                }
            }
        }
        return directVicinity;
    }
}