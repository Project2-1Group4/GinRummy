package temp.GameLogic.States;

import temp.GameLogic.GameActions.Action;
import temp.GameLogic.Logic.Finder;
import temp.GameLogic.Entities.HandLayout;
import temp.GameLogic.Entities.MyCard;
import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.Turn;

import java.util.*;

public class RoundState {
    private boolean locked;
    private CardsInfo cards;
    private Turn currentTurn;
    private HandLayout[] confirmedLayouts;
    private Integer knocker;
    private int[] pointsWonFromRound;
    public Stack<Action> actions;

    public RoundState(CardsInfo cards, Turn turn, HandLayout[] layouts, Stack<Action> actions, Integer knocker){
        this.cards = cards;
        this.currentTurn = turn;
        confirmedLayouts = layouts;
        this.actions = actions;
        this.knocker = knocker;
    }
    public RoundState(CardsInfo cards, Turn turn, HandLayout[] layouts){
        this(cards, turn, layouts, new Stack<Action>(), null);
    }
    public RoundState(CardsInfo cards, Turn turn){
        this(cards, turn, new HandLayout[cards.players.size()]);
    }
    public RoundState(CardsInfo cards){
        this(cards, null);
    }
    public RoundState(RoundState state){
        this(new CardsInfo(state.cards),state.currentTurn, state.layouts().clone(), (Stack<Action>) state.actions.clone(), state.knocker);
    }

    // Getters

    public int turnsPlayed(){
        int i = actions.size()-1;
        int k =0;
        if(i>0) {
            while (actions.get(i-k).step == Step.Layoff ||
                    actions.get(i-k).step == Step.LayoutConfirmation ||
                    actions.get(i-k).step == Step.EndOfRound) {
                k++;
                if(i-k<=0){
                    break;
                }

            }
        }
        return (actions.size()-k-1)/(3*cards.players.size());
    }
    public int numberOfPlayers(){
        return cards.players.size();
    }
    public int deckSize(){
        return cards.deckSize();
    }
    public int getPlayerIndex() {
        return turn().playerIndex;
    }
    public int[] points(){
        return pointsWonFromRound;
    }
    public Integer winner(){
        if(pointsWonFromRound==null){
            return null;
        }
        for (int i = 0; i < pointsWonFromRound.length; i++) {
            if(pointsWonFromRound[i]!=0){
                return i;
            }
        }
        return null;
    }
    public Integer knocker(){
        return knocker;
    }
    public boolean hasBeenKnocked(){
        return knocker!=null;
    }
    public boolean hasPerfectInformation(){
        return cards.hasPerfectInformation() && currentTurn!=null;
    }
    public boolean knockedWithGin() {
        if(knocker!=null){
            for (HandLayout confirmedLayout : confirmedLayouts) {
                if(confirmedLayout!=null && confirmedLayout.deadwoodValue()==0){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean locked(){
        return locked;
    }
    public Turn turn(){
        return currentTurn;
    }
    public Action getLastAction() {
        return actions.size()==0?null:actions.peek();
    }
    public MyCard peekDeck(){
        return cards.peekDeck();
    }
    public MyCard peekDiscard(){
        return cards.peekDiscard();
    }
    public List<MyCard> getCards(int index){
        return cards.getCards(index);
    }
    public List<List<MyCard>> getAllPlayerCards(){
        return cards.players;
    }
    public List<MyCard> getAllCards(){
        return cards.getAllCards();
    }
    public Stack<MyCard> deck(){
        return cards.deck;
    }
    public Stack<MyCard> discardPile(){
        return cards.discardPile;
    }
    public List<MyCard> unassigned() {
        return cards.unassigned;
    }
    public CardsInfo cards(){
        return cards;
    }
    public HandLayout[] layouts(){
        return confirmedLayouts;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Knocked: ").append(knocker).append(" and won ").append(Arrays.toString(pointsWonFromRound)).append("\n");
        sb.append("Turn: ").append(currentTurn).append("\n");
        sb.append(cards);
        for (int i = 0; i < confirmedLayouts.length; i++) {
            sb.append("\nPlayer ").append(i).append("'s layout:\n").append(confirmedLayouts[i]);
        }
        return sb.toString();
    }

    // Setters

    public Action undoLastAction(){
        Action toUndo = actions.size()==0? null : actions.peek();
        if(toUndo!=null) {
            toUndo.undoAction(this);
        }
        return toUndo;
    }
    public void knocker(Integer knocker){
        this.knocker = knocker;
    }
    public void turn(Turn turn){
        this.currentTurn = turn;
    }
    public void setLayouts(){
        for (int i = 0; i < cards.players.size(); i++) {
            confirmedLayouts[i] = Finder.findBestHandLayout(getCards(i));
        }
    }
    public void setPoints(int[] points){
        this.pointsWonFromRound = points;
    }
    public void lock(){
        locked = true;
    }
}