package GamePlayers.GreedyAIs;


import GameLogic.GameActions.DiscardAction;
import GameLogic.GameActions.PickAction;
import GameLogic.Logic.Finder;
import GameLogic.Entities.HandLayout;
import GameLogic.Entities.MyCard;
import GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class basicGreedy extends GamePlayer {

    public basicGreedy() {
        super();
    }

    public basicGreedy(boolean debug){
        this();
        this.debugRun = debug;
    }

    MyCard opDiscard;

    /*
    Determines how many points the hand should have before the player knocks
    Default is 10 'cause that's the minimum knock value according to general rules
    */

    int knockValue = 10;

    /*
    So the gist of it is that the method will look at the card in the discard pile
    It'll then evaluate if it can create a new hand that has a lower score than it's current hand

    If the card can lower the current score:
        return true
    Else
        return false

    And if it returns false that means it should pick from the deck instead (as there's a chance something good is there)
    */

    // If debugRun is false, then the thread sleeps will happen
    // As a debug run is when we're testing out the code for how the AI plays and stuff
    public static boolean debugRun = true;

    boolean evaluate(MyCard discardCard) {
        List<MyCard> current = new ArrayList<>(this.allCards);

        if(!this.debugRun) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }
        }
        current.add(discardCard);

        /* 
        I think this is supposed to be an ==
        As if the leastValuable card is the discardCard, then I want to pick the deck
        Which is why I return true
        Still, need to check this out
        */
        return chooseCardToDiscard(current) == discardCard;

    }


    public MyCard chooseCardToDiscard(MyCard aCard) {
        List<MyCard> current = this.handLayout.cards();

        current.add(aCard);

        return chooseCardToDiscard(aCard);

    }

    /*
    This'll go through the current hand and find the card that'll lower the value the most when discarded
    The number of items in aHand should be exactly 11, as that's what's expected of the method
    */

    public static MyCard chooseCardToDiscard(List<MyCard> aHand) {

        MyCard theCard = null;

        // It's just a high number, no hand can be worth more than 101
        // So that's why it's the starting value
        int lowestVal = 101;

        for (MyCard aCard : aHand) {
            List<MyCard> aList = new ArrayList<>(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now
            HandLayout layout = Finder.findBestHandLayout(aList);

            int resultingHand = layout.deadwoodValue();

            /*
            So this means that the hand that has the lowest value is the best hand
            And the card that was discarded from this is automatically assumed to be the worst
             */
            if (resultingHand <= lowestVal) {
                theCard = aCard;
                lowestVal = resultingHand;
            }


        }


        return theCard;

    }

    @Override
    public Boolean knockOrContinue() {
        int currentHand = this.handLayout.deadwoodValue();

        if(!this.debugRun) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }
        }

        // So if the value of the hand is less than the knock value, it'll return true
        // And validate it as it being a good time to knock
        return currentHand <= this.knockValue;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return evaluate(topOfDiscard);
    }

    @Override
    public MyCard discardCard() {
        return chooseCardToDiscard(this.handLayout.cards());
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        this.opDiscard = discardAction.card;
    }

    @Override
    public void playerPicked(PickAction pickAction) {

    }

    @Override
    public String toString() {
        return "basicGreedy";
    }
}
