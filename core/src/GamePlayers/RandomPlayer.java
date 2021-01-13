package GamePlayers;

import GameLogic.Entities.MyCard;

import java.util.Random;

public class RandomPlayer extends GamePlayer{
    Random rd = new Random();
    public RandomPlayer(){

    }

    public RandomPlayer(int seed){
        rd = new Random(seed);
    }
    @Override
    public Boolean knockOrContinue() {
        if(handLayout.deadwoodValue()<=10){
            return true;
        }
        return false;
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
