package temp.Extra.GA;

import temp.GamePlayers.AIs.basicGreedyTest;
import temp.GamePlayers.AIs.meldBuildingGreedy;
import temp.GamePlayers.GamePlayer;

import java.util.Random;

public class GA {

    public static void main(String[] args) {
        // Create GA with wanted params
        //GA ga = new GA(0,20,1,0.05f,0);
        // Initialize with players you want
        /**ga.init(new TestPlayer());*/
        // Start GA

        GameLogic logic = new GameLogic();
        logic.play(new basicGreedyTest(true), new meldBuildingGreedy(), 0);

        //GAPlayer[] winners = ga.train();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////// Check Results class, GALogic class and GAPlayer are correct ///////////////
    /////////////////////////////////METHODS TO UPDATE///////////////////////////////////////
    protected GAPlayer mutate(Random rd, GAPlayer[] winners, int index) {
        /**GamePlayer p = new TestPlayer();*/
        // TODO Do modifications
        /**return new GAPlayer(index,p);*/
        return null;
    }

    protected void updateScores(Result result, int player1Index, int player2Index) {
        float player1 = 0;
        float player2 = 0;
        // TODO Update scores
        competitors[player1Index].score += player1;
        competitors[player2Index].score += player2;
    }

    protected boolean stopCondition() {
        // TODO Set up stop condition
        return iteration >= 500;
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    private int iteration = 0;
    private final float mutationChance;
    private final float crossMutationChance;
    private final int nbOfWinners;
    private final int initSeed;

    private final GAPlayer[] competitors;


    public GA(int initSeed, int nbOfCompetitors, int nbOfWinners, float mutationChance, float crossMutationChance) {
        this.initSeed = initSeed;
        this.nbOfWinners = nbOfWinners;
        this.mutationChance = mutationChance;
        this.crossMutationChance = crossMutationChance;
        competitors = new GAPlayer[nbOfCompetitors];
    }

    public void init(GamePlayer[] prototypes) {
        Random rd = new Random(initSeed);
        GAPlayer[] gaPrototype = new GAPlayer[prototypes.length];
        int j = 0;
        for (GamePlayer prototype : prototypes) {
            gaPrototype[j] = new GAPlayer(j,prototype);
            j++;
        }
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = mutate(rd, gaPrototype, i);
        }
    }

    public void init(GamePlayer prototype){
        Random rd = new Random(initSeed);
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = mutate(rd, new GAPlayer[]{new GAPlayer(0,prototype)}, i);
        }
    }

    public GAPlayer[] train() {
        assert competitors[0] != null;
        GameLogic game = new GameLogic();
        int seed = 0;
        do {
            GAPlayer[] winners = getWinners();

            System.out.println("Iteration: " + iteration);
            System.out.println(winners[0].score + "\n");

            update(winners);
            resetScores();
            for (int i = 0; i < competitors.length; i++) {
                for (int j = 0; j < competitors.length; j++) {
                    if (i != j) {
                        Result result = game.play(competitors[i].player, competitors[j].player, seed);
                        updateScores(result, competitors[i].index, competitors[j].index);
                    }
                }
            }
            seed++;
            iteration++;
        } while (!stopCondition());
        return getWinners();
    }

    private void resetScores() {
        for (GAPlayer competitor : competitors) {
            competitor.score = 0;
        }
    }

    private void update(GAPlayer[] winners) {
        Random rd = new Random();
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = mutate(rd, winners, i);
        }
    }

    /**
     * Value between -1 and 1, 0 if no mutation
     *
     * @param rd randomizer
     * @return double [-1,1]
     */
    protected double getMutation(Random rd) {
        if (rd.nextDouble() < mutationChance) {
            if (rd.nextBoolean()) {
                return rd.nextDouble();
            }
            return -rd.nextDouble();
        }
        return 0;
    }

    protected GAPlayer[] getWinners() {
        GAPlayer[] sorted = bubbleSort(competitors);
        GAPlayer[] winners = new GAPlayer[nbOfWinners];
        for (int i = 0; i < winners.length; i++) {
            winners[i] = competitors[sorted[i].index];
        }
        return winners;
    }

    // https://stackabuse.com/sorting-algorithms-in-java/#bubblesort
    private GAPlayer[] bubbleSort(GAPlayer[] array) {
        GAPlayer[] a = new GAPlayer[array.length];
        System.arraycopy(array, 0, a, 0, a.length);

        boolean sorted = false;
        GAPlayer temp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i].score > a[i + 1].score) {
                    temp = a[i];
                    a[i] = a[i + 1];
                    a[i + 1] = temp;
                    sorted = false;
                }
            }
        }
        return a;
    }

}
