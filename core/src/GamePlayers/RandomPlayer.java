package GamePlayers;

import GameLogic.Entities.MyCard;

import java.util.Random;

public class RandomPlayer extends GamePlayer{
    Random rd;

    public RandomPlayer(Integer seed){
        if(seed==null){
            rd = new Random();
        }
        else{
            rd = new Random(seed);
        }
    }
    public RandomPlayer(){
        this(null);
    }

    // Game <=> Player interaction

    @Override
    public Boolean knockOrContinue() {
        return handLayout.deadwoodValue() <= 10;
    }
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return rd.nextBoolean();
    }
    @Override
    public MyCard discardCard() {
        return allCards.get(rd.nextInt(allCards.size()));
    }
}
