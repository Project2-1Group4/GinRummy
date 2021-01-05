package temp;

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

    // Game speed specs
    public static float gameSpeed = 1f;
    public static float KnockOrContinueTime = 50f;
    public static float DeckOrDiscardPileTime = 50f;
    public static float DiscardTime = 50f;
    public static float LayoutConfirmationTime = 30f;
    public static float LayOffTime = 50f;

    // Command printing specs
    public static boolean print = false;
    public static boolean printEndOfRound = false;
    public static boolean minPrint = false;

    // Visual specs
    public static float cardMaxWidthPercentage = 0.8f;
    public static float cardMaxHeightPercentage = 0.2f;
    public static float percentageAwayFromBottom = 0.2f;

    //Bot names
    public static String names_meldGreedy = "greedy meld greedy meldgreedy";
    public static String names_basicGreedy = "basic greedy basicgreedy";
    public static String names_minimax = "minimax alphabeta";
    public static String names_random = "random rd";
    public static String names_keyboard = "keyboard";
    public static String names_mouse = "mouse";
    public static String names_MCTS = "mcts";
}