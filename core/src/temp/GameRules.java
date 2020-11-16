package temp;

public class GameRules {
    // Score specs
    public static int minDeadwoodToKnock = 10;
    public static int ginBonus = 25;
    public static int undercutBonus = 25;
    public static int pointsToWin = 100;
    public static int baseCardsPerHand = 10;

    // Game speed specs
    public static float gameSpeed = 1000f;
    public static float KnockOrContinueTime = 50f;
    public static float DeckOrDiscardPileTime = 50f;
    public static float DiscardTime = 50f;
    public static float LayoutConfirmationTime = 30f;
    public static float LayOffTime = 50f;

    // Command printing specs
    public static boolean print = false;
    public static boolean printEndOfRound = true;

    // Visual specs
    public static float cardMaxWidthPercentage = 0.8f;
    public static float cardMaxHeightPercentage = 0.2f;
    public static float percentageAwayFromBottom = 0.2f;
}