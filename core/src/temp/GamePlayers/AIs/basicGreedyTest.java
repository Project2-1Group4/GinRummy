package temp.GamePlayers.AIs;


import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class basicGreedyTest extends GamePlayer {

    public basicGreedyTest() {
        super();
    }

    MyCard opDiscard;

    /*
    Determines how many points the hand should have before the player knocks
    Default is 10 'cause that's the minimum knock value according to general rules
    */

    int knockValue = 10;

    /*
    Because the round resets when there's two cards,
    It might be beneficial to pre-emptively knock
    This sets how many cards should be left in the deck before the knock
    Default value is three so that the player can have a 'safety margin' for knocking
    */

    int cardsInDeckLimit = 3;

    /*
    So the gist of it is that the method will look at the card in the discard pile
    It'll then evaluate if it can create a new hand that has a lower score than it's current hand

    If the card can lower the current score:
        return true
    Else
        return false

    And if it returns false that means it should pick from the deck instead (as there's a chance something good is there)
    */

    boolean evaluate(MyCard discardCard) {
        List<MyCard> current = new ArrayList<>(this.allCards);
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
        current.add(discardCard);
        return chooseCardToDiscard(current) != discardCard;

    }

    /*
    public boolean ChooseDeckOrPile(Card aCard){
    	return this.evaluate(aCard);        
    }*/


    public MyCard chooseCardToDiscard(MyCard aCard) {
        List<MyCard> current = this.handLayout.viewAllCards();

        current.add(aCard);

        return chooseCardToDiscard(aCard);

    }

    /*
    This'll go through the current hand and find the card that'll lower the value the most when discarded
    */

    public static MyCard chooseCardToDiscard(List<MyCard> aHand) {
        MyCard theCard = null;

        // It's just a low number, doesn't really matter what it is
        int highestVal = -1;

        for (MyCard aCard : aHand) {
            // TODO: Bug-test here to make sure the copies are deep copies and not shallow
            List<MyCard> aList = new ArrayList<>(aHand);
            aList.remove(aCard);

            // This is garbage unnefficient, but I don't feel like adding a proper method now
            HandLayout layout = Finder.findBestHandLayout(aList);

            int resultingHand = layout.getDeadwood();

            if (resultingHand >= highestVal) {
                theCard = aCard;
                highestVal = resultingHand;
            }


        }


        return theCard;

    }

    @Override
    public Boolean knockOrContinue() {
        int currentHand = this.handLayout.getDeadwood();

        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }

        return currentHand <= this.knockValue;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        return evaluate(topOfDiscard);
    }

    @Override
    public MyCard discardCard() {
        return chooseCardToDiscard(this.handLayout.viewAllCards());
    }

    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        this.opDiscard = discardAction.card;
    }

    @Override
    public void playerPicked(PickAction pickAction) {

    }
}
