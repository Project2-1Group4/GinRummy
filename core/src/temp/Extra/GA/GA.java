package temp.Extra.GA;

import temp.Extra.Tests.EndOfRoundInfo;
import temp.Extra.Tests.GameInfo;
import temp.Extra.Tests.Tests;
import temp.GamePlayers.AIs.meldBuildingGreedy;

import java.util.List;
import java.util.Random;

public class GA {

    public static void main(String[] args) {

        int numOfGames = 5;

        GA ga = new GA(50,0.02,2,15);

        ga.runForGenerations(3);

        meldBuildingGreedy[] theBest = ga.findNFittestPlayers(10);
        for(meldBuildingGreedy ai: theBest){
            System.out.println(theBest.toString());
        }

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
        /*competitors[player1Index].score += player1;
        competitors[player2Index].score += player2;*/
    }

    protected boolean stopCondition() {
        // TODO Set up stop condition
        return iteration >= 500;
    }
    ////////////////////////////////////////////////////////////////////////////////////////

    private int iteration = 0;
    private double mutationChance;
    int numberOfPlayers;
    int generationLimit;
    int numberOfGames;
    int gamesToWin;
    //private final int nbOfWinners;

    meldBuildingGreedy[] players;

    /*
    Idea of this is that a val will be modified by at most val*mutMult
    In positive or negative direction
    Under the idea that it'll help set some kind of limit on how big the mutation should be
     */
    double mutationMultiplier = 0.1;

    /*
    These two go hand in hand, as the second one says how many wins the first one had
    Which is helpful later on, trust me
     */
    meldBuildingGreedy[] fittestPlayers;
    int[] numOfWinsForFit;

    GameLogic internalLogic = new GameLogic(true,true);


    // I assume we always take half the competitors as winners, just cause it makes it easier to calculate stuff

    /*
    I butchered some of the code here to make it work specifically with meldBuildingGreedy
    Not good for generalization, but no one else will use a GA for what's missing
    So yeah, good enough
     */
    public GA(int nbOfCompetitors, double mutationChance, int generationLimit, int numberOfGames) {
        this.mutationChance = mutationChance;
        this.players = new meldBuildingGreedy[nbOfCompetitors];

        // So I'll just take half of the players as "fittest"
        this.fittestPlayers = new meldBuildingGreedy[nbOfCompetitors/2];
        this.numOfWinsForFit = new int[nbOfCompetitors/2];

        this.numberOfPlayers = nbOfCompetitors;
        this.generationLimit = generationLimit;

        this.numberOfGames = numberOfGames;

        this.gamesToWin = (numberOfGames/2)+1;

        this.init();
    }

    public void runForGenerations(int numOfGenerations){
        int i = 0;

        while(i<numOfGenerations){
            this.findFittestIndividuals();
            this.newGeneration();

        }


    }

    public void init() {
        for(int i=0; i<players.length;i++){
            double cardInMeld = randomNumberGenerator(0,1);
            double cardCloseToMeld = randomNumberGenerator(0,2);
            double cardFree = randomNumberGenerator(0,3);
            double cardNever = randomNumberGenerator(0,4);

            this.players[i] = new meldBuildingGreedy(cardInMeld,cardCloseToMeld,cardFree,cardNever);
        }

    }

    public meldBuildingGreedy[] findFittestIndividuals() {
        // By shuffling the order at the start, I can just pair a player with its immediate neighbor
        // And they'll be paired with a random opponent for sure
        shuffleArray(this.players);

        for(int i = 0; i<this.players.length/2;i+=2){
            meldBuildingGreedy[] matchedPlayers = {this.players[i], this.players[i+1]};

            List<GameInfo> results = Tests.runGames(this.internalLogic, matchedPlayers, this.numberOfGames,null);

            int victoriesForP0 = 0;
            int victoriesForP1 = 0;

            for(GameInfo info: results){
                EndOfRoundInfo lastGame = info.roundInfos.get(info.roundInfos.size()-1);
                int win = lastGame.winner;
                if(win == 0){
                    victoriesForP0++;
                } else {
                    victoriesForP1++;
                }

            }

            if(victoriesForP0>=this.gamesToWin){
                this.fittestPlayers[i/2] = matchedPlayers[0];
                this.numOfWinsForFit[i/2] = victoriesForP0;

            } else {
                this.fittestPlayers[i/2] = matchedPlayers[1];
                this.numOfWinsForFit[i/2] = victoriesForP1;

            }


        }

        return this.fittestPlayers;
    }

    /*private void resetScores() {
        for (GAPlayer competitor : competitors) {
            competitor.score = 0;
        }
    }*/

    private void newGeneration() {
        /*
        First half will be the new generation
        Second half will be the fittest values of the old generation
         */
        for(int i=0; i< fittestPlayers.length;i++){

            // Choose the location of p1 and of p2
            int p1 = pickRandomNumber(this.numOfWinsForFit);
            int p2 = pickRandomNumber(this.numOfWinsForFit);

            // Just to make sure they're different
            while(p2==p1){
                p1 = pickRandomNumber(this.numOfWinsForFit);
            }

            this.players[i] = crossover(fittestPlayers[p1],fittestPlayers[p2]);

        }

        for(int i=0 ;i<fittestPlayers.length;i++){
            this.players[i+ fittestPlayers.length] = fittestPlayers[i];
        }

    }

    meldBuildingGreedy crossover(meldBuildingGreedy p1, meldBuildingGreedy p2){
        double[] valsp1 = p1.heuristicValues();
        double[] valsp2 = p2.heuristicValues();
        double[] futureVals = new double[valsp1.length];

        for(int i=0; i< valsp1.length;i++){
            double avg = (valsp1[i]+valsp2[i])/2.0;

            if(this.mutationChance()){
                avg = mutate(avg);
            }

            futureVals[i] = avg;

        }

        return new meldBuildingGreedy(futureVals);
    }

    boolean mutationChance(){
        return randomNumberGenerator(0,1) <= this.mutationChance;
    }

    double mutate(double valToMutate){
        double bounds = valToMutate*mutationMultiplier;
        return valToMutate + randomNumberGenerator(-bounds,bounds);
    }
    /*
    Gist of this method is that an int with a higher value should have a higher chance of being picked
    So the individual wins are summed up, and based off the total number a random number is generated

    It's the exact same thing I did for picking the random cards with probability, it works just go with it
     */
    public static int pickRandomNumber(int[] wins){
        int maxValue = calculateSum(wins);
        int desiredNum = (int)randomNumberGenerator(0,maxValue);

        // It's increased by one due to how the numbers are generated
        // Done so that all of the values have an equal chance
        // I thought it over, makes sense, so just please trust myself
        desiredNum++;

        int count = 0;

        for(int i=0; i< wins.length; i++){
            count+= wins[i];

            if(count>=desiredNum){
                return i;
            }

        }

        // Shouldn't happen, but just a sad return 0 in the end
        System.out.println("Error while calculating probability");
        return 0;

    }

    public static int calculateSum(int[] toSum){
        int val = 0;
        for(int num: toSum){
            val+=num;
        }
        return val;
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

    // Implementation of Fisher-Yates shuffle found on stackOverflow
    // https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
    static void shuffleArray(meldBuildingGreedy[] array){
        Random random = new Random();
        for(int i= array.length-1;i>0;i-- ){
            int randomIndex = random.nextInt(i+1);
            meldBuildingGreedy temp = array[randomIndex];
            array[randomIndex] = array[i];
            array[i] = temp;
        }


    }

    public static double randomNumberGenerator(double min, double max){
        return min+(Math.random()*(max-min));
    }

    public meldBuildingGreedy[] findNFittestPlayers(int nPlayersToSearch){
        // I'll save the indexes of the NFittest players first

        int[] indexes = new int[nPlayersToSearch];

        for(int i=0; i<fittestPlayers.length;i++){
            int numOfWins = numOfWinsForFit[i];

            for(int j=0; j<indexes.length;j++){
                if(numOfWins > indexes[j]){

                    for(int k = j;k<indexes.length-1;k++){
                        indexes[k+1] = indexes[k];
                    }

                    indexes[j] = i;

                    break;
                }
            }

        }

        meldBuildingGreedy[] nFittestPlayers = new meldBuildingGreedy[nPlayersToSearch];

        for(int i=0;i< nFittestPlayers.length;i++){
            nFittestPlayers[i] = fittestPlayers[indexes[i]];
        }

        return nFittestPlayers;
    }

}
