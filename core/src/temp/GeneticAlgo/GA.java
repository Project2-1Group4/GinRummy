package temp.GeneticAlgo;

import java.util.Random;

public class GA{

    public static void main(String[] args){
        GA ga = new GA(0,10,2,0.05f,0);
        ga.init(new GAPlayer[]{
                new GAPlayer(0,new TestPlayer())
        });
        ga.train();
    }
    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////// Check Results class, GALogic class and GAPlayer are correct ///////////////
    /////////////////////////////////METHODS TO UPDATE///////////////////////////////////////
    protected GAPlayer mutate(Random rd, GAPlayer[] winners, int index) {
        TestPlayer p = new TestPlayer();
        if (winners != null) {
            GAPlayer parent = winners[rd.nextInt(winners.length)];
            p.probabilityMatrix = parent.player.probabilityMatrix;
            p.valueMatrix = parent.player.valueMatrix;
        }
        for (int i = 0; i < p.probabilityMatrix.length; i++) {
            for (int j = 0; j < p.probabilityMatrix.length; j++) {
                p.probabilityMatrix[i][j]+= getMutation(rd)*5;
            }
        }
        for (int i = 0; i < p.valueMatrix.length; i++) {
            for (int j = 0; j < p.valueMatrix.length; j++) {
                p.valueMatrix[i][j]+= getMutation(rd)*5;
            }
        }
        return new GAPlayer(index,p);
    }

    protected void updateScores(Result result){
        final int winScore = 30;
        final float turnLoss = 0.1f;
        final int tieScore = 5;
        if(result.winner!=null){
            result.winner.score+=winScore;
        }
        else {
            result.player1.score+=tieScore;
            result.player2.score+=tieScore;
        }
        result.player1.score-= result.nbOfTurns*turnLoss;
        result.player2.score-= result.nbOfTurns*turnLoss;
    }

    protected boolean stopCondition(){
        return iteration >= 20;
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    private int iteration=0;
    private final float mutationChance;
    private final float crossMutationChance;
    private final int nbOfWinners;
    private final int initSeed;

    private final GAPlayer[] competitors;


    public GA(int initSeed, int nbOfCompetitors, int nbOfWinners, float mutationChance, float crossMutationChance){
        this.initSeed = initSeed;
        this.nbOfWinners = nbOfWinners;
        this.mutationChance = mutationChance;
        this.crossMutationChance = crossMutationChance;
        competitors = new GAPlayer[nbOfCompetitors];
    }

    public void init(GAPlayer[] prototypes){
        Random rd = new Random(initSeed);
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = mutate(rd,null,i);
        }
    }

    public GAPlayer[] train(){
        assert competitors[0]!=null;
        GALogic game = new GALogic();
        int seed = 0;
        do{
            System.out.println("Iteration: "+iteration);
            GAPlayer[] winners = getWinners();
            update(winners);
            resetScores();
            for (int i = 0; i < competitors.length; i++) {
                for (int j = 0; j < competitors.length; j++) {
                    if(i!=j) {
                        Result result = game.play(competitors[i], competitors[j], seed);
                        updateScores(result);
                    }
                }
            }
            seed++;
            iteration++;
        }while(!stopCondition());
        return getWinners();
    }

    private void resetScores(){
        for (GAPlayer competitor : competitors) {
            competitor.score = 0;
        }
    }

    private void update(GAPlayer[] winners){
        Random rd = new Random();
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = mutate(rd,winners,i);
        }
    }

    /**
     * Value between -1 and 1, 0 if no mutation
     *
     * @param rd randomizer
     * @return double [-1,1]
     */
    protected double getMutation(Random rd){
        if(rd.nextDouble()<mutationChance) {
            if (rd.nextBoolean()) {
                return rd.nextDouble();
            }
            return -rd.nextDouble();
        }
        return 0;
    }

    protected GAPlayer[] getWinners(){
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
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i].score > a[i+1].score) {
                    temp = a[i];
                    a[i] = a[i+1];
                    a[i+1] = temp;
                    sorted = false;
                }
            }
        }
        return a;
    }

}
