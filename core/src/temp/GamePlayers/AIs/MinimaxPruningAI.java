package temp.GamePlayers.AIs;

import cardlogic.Card;
import cardlogic.SetOfCards;
import gameHandling.Player;
import temp.GameLogic.GameActions.DiscardAction;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.MELDINGOMEGALUL.Finder;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GametreeAI;
import temp.GamePlayers.Node;

import java.io.*;
import java.util.*;

public class MinimaxPruningAI extends GamePlayer {
    GametreeAI tree;
    boolean AITurn;
    public SetOfCards hand;
    public SetOfCards pile;
    public SetOfCards unknownCards;
    boolean playerKnock = false;
    boolean AIknock = false;
    static int round = 0;
    //public static SetOfCards deck;


    public MinimaxPruningAI(GametreeAI tree, SetOfCards pile, SetOfCards hand, SetOfCards unknown) {
        this.tree = tree;
        this.hand = hand;
        this.pile = pile;
        this.unknownCards = unknown; //include deck and opponent hand
        AITurn = true;
    }

    public MinimaxPruningAI(GametreeAI tree) {
        this.tree = tree;
        this.hand = tree.hand;
        this.pile = tree.discardPile;
        this.unknownCards = tree.cardsUnknown;
    }

    public void pickedCard() {
        AITurn = false;
    }

    // look at the more likely hand to pick. Here we save the scoreHand of each possible handCards
    public Node alphaBetaPruning(Node node, Node alpha, Node beta, boolean maxPlayer) {
        if ((node.getChildren().size() == 0) || node.playerStop || node.AIStop) {
            return node;
        }

        if (maxPlayer) {
            //System.out.println("run alpha beta with max player");
            Node maxNode = new Node(false); //node with negative inf hand value
            //Node maxNode = null;
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, false);
                //System.out.println("evalNode: "+evalNode);
                maxNode = Node.getNodeMax(alpha, evalNode);
                //System.out.println("max node: "+maxNode);
                alpha = Node.getNodeMax(alpha, evalNode);
                //System.out.println("alpha: "+alpha);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            //System.out.println("max node: "+maxNode);
            return maxNode;
        } else {
            //System.out.println("run alpha beta with min player");
            Node minNode = new Node(true); // node with positive inf hand value
            //Node minNode = null;
            for (Node child : node.getChildren()) {
                Node evalNode = alphaBetaPruning(child, alpha, beta, true);
                minNode = Node.getNodeMin(beta, evalNode);
                //System.out.println("min node: "+minNode);
                beta = Node.getNodeMin(beta, evalNode);
                if (beta.getHandValue() <= alpha.getHandValue())
                    break;
            }
            //System.out.println("min node: "+minNode);
            return minNode;
        }
    }

    public void chooseNode(SetOfCards deck) {
        // this method doesn't get root node, should be changed if that is what you need here!!
        //get the current state of AI (game)
        Node parent = tree.getRootNode();
        List<Card> currentHand = Player.copyList(parent.hand.toList());
        System.out.println("current bot hand: "+currentHand);
        Node pickNode = alphaBetaPruning(parent, new Node(false), new Node(true), true);
        System.out.println("pick node (hoping hand): "+pickNode);
        unknownCards = pickNode.unknownCards;

        List<Card> newHand = Player.copyList(pickNode.hand.toList());
        Card pickCard = null;

        //loop through newHand to get the new card
        for (Card card : newHand) {
            if (!currentHand.contains(card)) {
                pickCard = card;
            }
        }
        System.out.println("pick card: "+pickCard);
        Card discardCard = null;

        //if pickCard and discardCard are both null. It means that after simulating the bot does not want to change the hand
        //at current state
        //loop through old hand to get the card be discarded
        for (Card card : currentHand) {
            if (!newHand.contains(card)) {
                discardCard = card;
            }
        }

        System.out.println("discard card: "+discardCard);

        if(pickCard == pile.getCard(pile.size()-1)){
            System.out.println("AI pick from pile: "+pickCard);
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

        System.out.println("bot hand after play: "+hand);
        System.out.println("Card discard from bot: "+discardCard);
        //update tree
    }

    public boolean AIknock() {
        int score = Player.scoreHand(this.hand.toList());
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
        //System.out.println("bot hand before turn: "+hand);
        System.out.println("Discard pile before bot discard: " + pile);
        chooseNode(deck);
        //System.out.println("bot hand after turn: "+hand);
        // opponents turn
        System.out.println("Player 2, it's your turn");
        //System.out.println("Discard pile:" + pile.getCard(pile.size()-1));
        System.out.println("Discard pile:" + pile);

        System.out.println("Current player hand:" + opponentHand.toList());
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
        //current.unknownCards = unknownCards;
        System.out.println("New player hand: " + opponentHand.toList());
        System.out.println("Pick a card to discard");
        int cardDiscard = sc.nextInt();
        if (cardDiscard < opponentHand.size()) {
            Card aCard = opponentHand.getCard(cardDiscard);
            this.tree.simulationDiscard(current, aCard);
            opponentHand.discardCard(aCard);
            this.pile.addCard(aCard);
            unknownCards.discardCard(aCard);
            //unknownCards = this.tree.getCardsUnknown();

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
        //GametreeAI gameTree = new GametreeAI(pile, hand,unknownCards, 4);
        //this.tree = gameTree;
        //Node newRoot = new Node(pile, hand, unknownCards, gameTree.opponentHand, 0);
        //gameTree.createNodesOpponent(newRoot, false);
        //tree = new GametreeAI(pile, hand,unknownCards, 4);
        //Node newRoot = new Node(pile, hand, unknownCards, tree.opponentHand, 0);
        //tree.createNodesOpponent(newRoot, false);
        tree.createTree(false);


    }


    public static void main (String [] args){
        // create cards for game
        SetOfCards deck = new SetOfCards(true, false);
        // create hand AI
        SetOfCards hand = SetOfCards.handOutCard(10, deck);
        System.out.println("hand: "+hand);
        System.out.println("score: "+Player.scoreHand(hand.toList()));
        // create pile
        SetOfCards pile = new SetOfCards(false, false);
        Card discardCard = deck.drawTopCard();
        pile.addCard(discardCard);
        // create tree
        GametreeAI gameTree = new GametreeAI(pile, hand,deck, 4);
        gameTree.createTree(true);

        // create opponenthand
        SetOfCards copyDeck  = new SetOfCards(deck.toList());
        SetOfCards opponentHand = SetOfCards.handOutCard(10, copyDeck);
        // create pruning
        MinimaxPruningAI AI = new MinimaxPruningAI(gameTree, pile, hand, deck);

        // start game
        while(!AI.AIknock() && !AI.playerKnock){
            //Print out some cards to see the probabilities
            /*
            for(int i = 0; i<3; i++) {
                System.out.println("test prob: "+gameTree.cardsUnknown.getCard(i+3)+" "+gameTree.cardsUnknown.getCard(i+3).getProb());
            }

             */
            AI.playGame(opponentHand, copyDeck);
            round++;
        }
        System.out.println("Bot hand card after game over: "+hand);
        System.out.println("Win after: "+round+" rounds");



    }

    /*
    If true then the player knocks and the round ends
    If false then the player doesn't knock
     */
    @Override
    public Boolean knockOrContinue() {
        return null;
    }

    /*
    Retruns true if the deck is picked
    False if the discard pile is picked
     */
    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {

        Card aCard = new Card(topOfDiscard);
        MyCard myCard = new MyCard(aCard);
        return null;
    }

    /*
    Returns the card that wants to be reomved from the current hand
     */
    @Override
    public MyCard discardCard() {

        return null;
    }

    /*
    It's called everytime the player's hand is changed
    So this means it's called when:
        Before the player chooses from the discard pile or from the deck
        The player chooses what to discard
        After the player has discarded a card (with the new 10 card hand)
     */
    @Override
    public void update(HandLayout realLayout) {
        allCards = realLayout.viewAllCards();
        handLayout = Finder.findBestHandLayout(allCards);

        SetOfCards cardList = new SetOfCards(realLayout);
    }

    /*
    Complete round reset, so it's a new hand and a new deck and a new everything
     */
    @Override
    public void newRound(MyCard topOfDiscard) {

    }

    /*
    Gives information on the other player's actions
        Just says what card the other player discarded
     */
    @Override
    public void playerDiscarded(DiscardAction discardAction) {
        MyCard DisCard = discardAction.card;
    }

    /*
    Gives info on the other player's actions
        If pickAction.deck is not true, then the other player picked from the discard pile
        So we can know what he picked up
     */
    @Override
    public void playerPicked(PickAction pickAction) {
        if(!pickAction.deck){
            MyCard aCard = pickAction.card;
            //this.memoryMatrix[aCard.suit.index][aCard.rank.index] = 2;
        }

    }

}
