package temp.Extra.GA;

import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {

    public static void main(String[] args) {
        // Create GA with wanted params
        Integer seed = 0;
        int nbOfCompetitors = 100;
        int nbOfGamesPerPair = 10;
        int nbOfWinners = 2;
        float mutationChance = 0.05f;
        GA ga = new GA(seed,nbOfCompetitors,nbOfGamesPerPair,nbOfWinners,mutationChance);
        ga.init(new basicGreedyTest()); //TODO create prototype(s)
        GamePlayer[] winners = ga.train();
        //TODO use winners for something
    }

    /*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Check Results class if it gives what you want (can also substitute Results with EndOfRoundInfo, msg me)
                                            Update these method to suite your needs
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    /**
     *
     * @param rd random seed to be able to replicate results
     * @param winners array of GamePlayer(s) that got awarded the most points this iteration
     * @return new GamePlayer with updated variables
     */
    protected GamePlayer mutate(Random rd, GamePlayer[] winners) {
        /*

        Create new GamePlayer and do modifications based on winners[]



        return null;*/
        return FkThisUpAsMuchAsYoudLike.mutate(rd,winners);
    }

    /**
     * @param results = list of results of every end of game
     * @return float[] of scores GamePlayer1 (indexed at 0) and GamePlayer2 (indexed at 1)
     */
    protected float[] updateScores(List<Result> results) {
        /*float player1 = 0;
        float player2 = 0;


        Update player1 (index 0), player2 (index 1) based on performance in results


        return new float[]{
                player1,
                player2
        };*/
        return FkThisUpAsMuchAsYoudLike.updateScores(results);
    }

    /**
     * @return true you want to stop, false if you want do another iteration
     */
    protected boolean stopCondition() {
        /*

        Set stopping condition


        return iteration>=500;
         */
        return FkThisUpAsMuchAsYoudLike.stopCondition(iteration);
    }

    /**
     * Value between -1 and 1, 0 if no mutation. Could be useful.
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

    /*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            IGNORE EVERYTHING AFTER THIS POINT
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */

    private int iteration = 0;
    private final float mutationChance;
    private final int nbOfWinners;
    private final Random rd;
    private final int nbOfGamesPerPair;
    private final GAPlayer[] competitors;

    public GA(Integer initSeed, int nbOfCompetitors, int nbOfGamesPerPair, int nbOfWinners, float mutationChance) {
        if(initSeed!=null){
            rd = new Random(initSeed);
        }else{
            rd = new Random();
        }
        this.nbOfWinners = nbOfWinners;
        this.nbOfGamesPerPair = nbOfGamesPerPair;
        this.mutationChance = mutationChance;
        competitors = new GAPlayer[nbOfCompetitors];
    }

    public void init(GamePlayer[] prototypes) {
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = new GAPlayer(i,mutate(rd, prototypes));
        }
    }

    public void init(GamePlayer prototype){
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = new GAPlayer(i,mutate(rd, new GamePlayer[]{prototype}));
        }
    }

    public GamePlayer[] train() {
        assert competitors[0] != null;
        GameLogic game = new GameLogic();
        int seed = 0;
        do {
            for (int i = 0; i < competitors.length; i++) {
                for (int j = 0; j < competitors.length; j++) {
                    if (i != j) {
                        List<Result> results = new ArrayList<>();
                        for (int k = 0; k < nbOfGamesPerPair; k++) {
                            results.add(game.play(competitors[i].player, competitors[j].player, seed));
                        }
                        float[] scores = updateScores(results);
                        competitors[i].score+= scores[i];
                        competitors[j].score+= scores[j];
                    }
                }
            }

            GAPlayer[] winners = getWinners();
            System.out.println("Iteration: " + iteration);
            /*

            If you want to print something every iteration

             */
            update(winners);
            resetScores();

            seed++;
            iteration++;
        } while (!stopCondition());
        GAPlayer[] winners = getWinners();
        GamePlayer[] w = new GamePlayer[winners.length];
        for (int i = 0; i < winners.length; i++) {
            w[i] = winners[i].player;
        }
        return w;
    }

    private void resetScores() {
        for (GAPlayer competitor : competitors) {
            competitor.score = 0;
        }
    }

    private void update(GAPlayer[] winners) {
        Random rd = new Random();
        GamePlayer[] w = new GamePlayer[winners.length];
        for (int i = 0; i < winners.length; i++) {
            w[i] = winners[i].player;
        }
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = new GAPlayer(i, mutate(rd, w));
        }
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
