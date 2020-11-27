package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MinimaxPruningAI {
    GametreeAI tree;
    boolean AITurn;
    public SetOfCards hand;
    public SetOfCards pile;
    public SetOfCards unknownCards;


    public MinimaxPruningAI(GametreeAI tree, SetOfCards pile, SetOfCards hand, SetOfCards unknown) {
        this.tree = tree;
        this.hand = hand;
        this.pile = pile;
        this.unknownCards = unknown;
        AITurn = true;
    }

    public void pickedCard() {
        AITurn = false;
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer) {
        if ((node.getChildren().size() == 0) || !node.playerStop || !node.AIStop) {
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
        // this method doesn't get root node, should be changed if that is what you need here!!
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

        if(pickCard == pile.getCard(pile.size()-1)){
            pile.discardCard(pickCard);
        }
        else{
            // should pick from actual deck instead of cardsunknown in tree since in game you get topcard from deck
            // discard card then also should be changed instead of the node one because your new card is probably different
            unknownCards.discardCard(pickCard);
        }
        pile.addCard(discardCard);
        hand.discardCard(discardCard);
        hand.addCard(pickCard);
        pickedCard();
        //update tree
    }

    public void playGame(SetOfCards opponentHand, SetOfCards deck){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // AI's turn
        chooseNode();
        // opponents turn
        System.out.println("Player 2, it's your turn");
        System.out.println("Discard pile:" + pile.getCard(pile.size()-1));
        System.out.println("Current hand:" + opponentHand.toList());
        System.out.println("Pick Deck or Pile");
        // get choice of opponent from which pile it gets new card
        String choice = "pile";
        try {
            choice = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // update probs
        Node current = new Node(pile, hand, unknownCards, new SetOfCards(false, false), 0);
        if(choice.equals("pile")){
            this.tree.simulationPickPile(current);
            unknownCards = this.tree.getCardsUnknown();
            opponentHand.addCard(pile.drawTopCard());
        }
        else{
            this.tree.simulationPickDeck(current);
            unknownCards = this.tree.getCardsUnknown();
            opponentHand.addCard(deck.drawTopCard());
        }
        System.out.println("Pick a card to discard");

    }


    public static void main (String [] args){
        // create cards for game
        SetOfCards deck = new SetOfCards(true, false);
        SetOfCards hand = new SetOfCards(false, false);
        // create hand AI
        for(int i = 0; i < 10; i++){
            Card aCard = deck.drawTopCard();
            hand.addCard(aCard);
        }
        // create pile
        SetOfCards pile = new SetOfCards(false, false);
        Card discardCard = deck.drawTopCard();
        pile.addCard(discardCard);
        // create tree
        GametreeAI gameTree = new GametreeAI(pile, hand,deck, 10);
        gameTree.createTree();
        // create opponenthand
        SetOfCards copyDeck  = new SetOfCards(deck.toList());
        SetOfCards opponentHand = new SetOfCards(false, false);
        for(int i = 0; i < 10; i++){
            Card aCard = copyDeck.drawTopCard();
            opponentHand.addCard(aCard);
        }
        // create pruning
        MinimaxPruningAI AI = new MinimaxPruningAI(gameTree, pile, hand, deck);

        boolean knocked = false;

        // start game
        while(!knocked){
            AI.playGame(opponentHand, copyDeck);

        }

    }


}
