package temp.GamePlayers.GameTreeAIs;

import temp.GameLogic.*;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import java.util.*;

//the structure, parameters are the same as depth first search so I will reuse all basic methods from depth minimax
public class BestFirstMinimaxAI extends MinimaxPruningAI {
    double heuristicValue;

    public BestFirstMinimaxAI() {
        super();
        this.heuristicValue = (double) Finder.findBestHandLayout(this.hand).getDeadwood();
    }

    public double newEvaluation(Node nodeChecking) {
        List<MyCard> attemptHand = nodeChecking.hand;
        //get deadwood value from hand
        int scoreHand = Finder.findBestHandLayout(attemptHand).getDeadwood();

        //get possible new hand cards
        MyCard[] pick_discard = this.pick_discard(nodeChecking.hand);
        MyCard pickCard = pick_discard[0];

        //get probability of each card
        double[][] probMap = this.tree.probMap;
        double cardPickedProb = probMap[pickCard.suit.index][pickCard.rank.index];

        double finalValue = scoreHand + cardPickedProb*100;
        return finalValue;
    }

    public MyCard[] pick_discard(List<MyCard> newAttemptHand) {

        //card pick
        MyCard pickCard = null;
        for (MyCard card : newAttemptHand) {
            if (!this.hand.contains(card)) {
                pickCard = card;
            }
        }
        //card discard
        MyCard discardCard = null;
        for (MyCard card : this.hand) {
            if (!newAttemptHand.contains(card)) {
                discardCard = card;
            }
        }
        return new MyCard[] {pickCard, discardCard};
    }

    public void searching() {

    }
}
