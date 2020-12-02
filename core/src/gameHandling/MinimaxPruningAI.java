package gameHandling;

import cardlogic.Card;
import cardlogic.SetOfCards;

import java.io.*;
import java.util.*;

public class MinimaxPruningAI {
    GametreeAI tree;
    boolean AITurn;
    public SetOfCards hand;
    public SetOfCards pile;
    public SetOfCards unknownCards;
    boolean playerKnock = false;
    boolean AIknock = false;
    //public static SetOfCards deck;


    public MinimaxPruningAI(GametreeAI tree, SetOfCards pile, SetOfCards hand, SetOfCards unknown) {
        this.tree = tree;
        this.hand = hand;
        this.pile = pile;
        this.unknownCards = unknown; //include deck and opponent hand
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

    public void chooseNode(SetOfCards deck) {
        // this method doesn't get root node, should be changed if that is what you need here!!
        //get the current state of AI (game)
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
            pile.addCard(discardCard);
            hand.addCard(pickCard);
            hand.discardCard(discardCard);

        }
        else{
            // should pick from actual deck instead of cardsunknown in tree since in game you get topcard from deck
            // discard card then also should be changed instead of the node one because your new card is probably different
            /* this one changes to the rule below (I am not really sure this one makes any senses or not)
            if the search return the card from pile  -> pick pile otherwise get card from deck (we have the prob that we can pick the likely card but still not sure)
             */
            pickCard = deck.drawTopCard();
            deck.discardCard(pickCard);
            hand.addCard(pickCard);
            discardCard = GametreeAI.chooseCardToDiscard(hand.toList());
            hand.discardCard(discardCard);
            pile.addCard(discardCard);
            System.out.println("AI pick from deck: "+pickCard);
            //pickCard is now known so remove from unknowncards
            unknownCards.discardCard(pickCard);
        }

        pickedCard();
        System.out.println("Card discard from bot: "+discardCard);
        //update tree
    }

    public boolean AIknock() {
        int score = Player.scoreHand(hand.toList());
        System.out.println("hand score: "+score);
        if (score < 10) {
            System.out.println("Bot wins the game!!");
            AIknock = true;
            return true;
        }
        else
            return false;
    }

    public void playGame(SetOfCards opponentHand, SetOfCards deck){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Scanner sc = new Scanner(System.in);
        System.out.println("deck size: "+deck.size());
        System.out.println("deck top card: "+deck.getCard(deck.size()-1));
        // AI's turn
        System.out.println("bot hand before turn: "+hand);
        chooseNode(deck);
        System.out.println("bot hand after turn: "+hand);
        // opponents turn
        System.out.println("Player 2, it's your turn");
        //System.out.println("Discard pile:" + pile.getCard(pile.size()-1));
        System.out.println("Discard pile:" + pile);

        System.out.println("Current hand:" + opponentHand.toList());
        System.out.println("Pick Deck or Pile");
        // get choice of opponent from which pile it gets new card
        String choice = "pile";
        try {
            choice = reader.readLine();
        } catch (Exception e) {
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
        int cardDiscard = sc.nextInt();
        if (cardDiscard < opponentHand.size()) {
            Card aCard = opponentHand.getCard(cardDiscard);
            opponentHand.discardCard(aCard);
            this.pile.addCard(aCard);
            this.unknownCards.discardCard(aCard);

        }

        boolean knock = Player.chooseToKnock(opponentHand);

        if (knock) {
            System.out.println("yes to knock otherwise continue!!!");
            String chooseToKnock = sc.nextLine();
            if (chooseToKnock.equals("yes")) {
                playerKnock = true;
            }
            else
                playerKnock = false;
        }

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

        //boolean knocked = false;

        // start game
        while(!AI.AIknock() && !AI.playerKnock){
            AI.playGame(opponentHand, copyDeck);
            //System.out.println("bot hand: "+hand);

        }
        System.out.println("Bot hand card after game over: "+hand);

    }
}
