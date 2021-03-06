package Extra;

public class GameRules {
    // Score specs
    public static int minDeadwoodToKnock = 10;
    public static int ginBonus = 25;
    public static int undercutBonus = 25;
    public static int pointsToWin = 100;

    // Game rules
    public static int minCardsInDeck = 2;
    public static int baseCardsPerHand = 10;
    public static int maxTurnsInARound = 500; //To avoid bots just picking from discard for infinity

    // Game speed specs. Only applies to player in visuals!
    public static float gameSpeed = 1f;
    public static float KnockOrContinueTime = 50f;
    public static float DeckOrDiscardPileTime = 50f;
    public static float DiscardTime = 50f;
    public static float LayoutConfirmationTime = 30f;
    public static float LayOffTime = 50f;

    // Visual specs
    public static float cardMaxWidthPercentage = 0.8f;
    public static float cardMaxHeightPercentage = 0.2f;
    public static float percentageAwayFromBottom = 0.2f;

    //Bot names
    public static String names_meldGreedy = "meld meldgreedy";
    public static String names_basicGreedy = "basic greedy basicgreedy";
    public static String names_minimax = "depthminimax minimax alphabeta";
    public static String name_best_search = "best_first best bestminimax";
    public static String names_random = "random rd";
    public static String names_keyboard = "keyboard";
    public static String names_mouse = "mouse";
    public static String names_MCTS = "mcts";
}