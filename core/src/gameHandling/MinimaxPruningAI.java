package gameHandling;

import cardlogic.Card;
import java.util.*;

public class MinimaxPruningAI {
    GametreeAI tree;
    boolean AITurn;

    public MinimaxPruningAI(GametreeAI tree) {
        this.tree = tree;
        AITurn = true;
    }

    public void pickedCard() {
        AITurn = false;
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer) {
        if ((node.getChildren().size() == 0) || !node.playerStop || !!node.AIStop) {
            return node;
        }

        if (maxPlayer) {
            Node maxNode = new Node(false); //node with negative inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, false);
                maxNode = Node.getNodeMax(alpha, evalNode);
                alpha = Node.getNodeMax(alpha, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return maxNode;
        } else {
            Node minNode = new Node(true); // node with positive inf hand value
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, true);
                minNode = Node.getNodeMin(beta, evalNode);
                beta = Node.getNodeMin(beta, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            return minNode;
        }
    }

    public void chooseNode() {
        Node parent = tree.getParentNode();
        Node pickNode = alphaBetaPruning(parent, new Node(false), new Node(true), true);

        List<Card> currentHand = Player.copyList(parent.hand.toList());
        List<Card> newHand = Player.copyList(pickNode.hand.toList());

        Card pickCard = null;

        //loop through newHand to get the new card
        for (Card card : newHand) {
            if (!currentHand.contains(card)) {
                pickCard = card;
            }
        }

        Card discardCard = null;

        //loop through old hand to get the card be discarded
        for (Card card : currentHand) {
            if (!newHand.contains(card)) {
                discardCard = card;
            }
        }

        this.tree.cardsUnknown.discardCard(pickCard);
        this.tree.discardPile.addCard(discardCard);
        this.tree.hand.discardCard(discardCard);
        this.tree.hand.addCard(pickCard);
        pickedCard();
        //update tree
    }

}
