package temp.GeneticAlgo;

import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.MemoryPlayer;

import java.util.Arrays;

// Modify based on what you wanna train
public class TestPlayer extends MemoryPlayer{

    protected double[][] probabilityMatrix; // probability of having card[i] given you have certain cards
    protected double[][] valueMatrix; // value of card[i] given you have certain cards
    private double[] enemyCardOwnership;
    private double[] cardValues;
    // Memory to find probabilities
    // Hand + top of discard to find deck or discard pile
    // Probabilities (somewhat has memory) + CardValues (somewhat stores melds) to find what to discard
    // Hand + probabilities to find knock or continue
    public TestPlayer(){
        super();
        probabilityMatrix = new double[memory.length][memory.length];
        valueMatrix = new double[memory.length][memory.length];
        enemyCardOwnership = new double[memory.length];
        cardValues = new double[memory.length];
    }
    @Override
    public Boolean knockOrContinue() {
        return handLayout.getDeadwood() <= 10;
    }

    @Override
    public Boolean pickDeckOrDiscard(int remainingCardsInDeck, MyCard topOfDiscard) {
        double[] reformatted = valueReformat(memory);
        reformatted[topOfDiscard.getIndex()] = 1;
        double[] values = matrixMultiplication(valueMatrix,reformatted);
        double lowestValue = Integer.MIN_VALUE;

        int index = 0;
        int newVal = 0;
        int curVal = 0;
        for (int i = 0; i < values.length; i++) {
            if(values[i]<lowestValue){
                index = i;
                lowestValue = values[i];
            }
            newVal+= values[i];
            curVal+= cardValues[i];
        }
        // Some % increase in value
        return topOfDiscard.getIndex() != index && Math.abs(newVal - curVal) < curVal * 0.10f;
    }

    @Override
    public MyCard discardCard() {
        int discardIndex=0;
        double highestValue=Integer.MIN_VALUE;
        for (int i = 0; i < cardValues.length; i++) {
            if(cardValues[i]!=0) {
                double save = enemyCardOwnership[i];
                enemyCardOwnership[i] = 1;
                double[] values = matrixMultiplication(valueMatrix, enemyCardOwnership);
                double val=0;
                for (double value : values) {
                    val+=value;
                }
                if(val>highestValue){
                    highestValue = val;
                    discardIndex = i;
                }
                enemyCardOwnership[i] = save;
            }
        }
        for (int i = 0; i < allCards.size(); i++) {
            
        }
        return MyCard.getCard(discardIndex);
    }

    @Override
    public void update(HandLayout realLayout) {
        super.update(realLayout);
        enemyCardOwnership = matrixMultiplication(probabilityMatrix,reformat(memory));
        cardValues = matrixMultiplication(valueMatrix,valueReformat(memory));
        //basicPrinting();
    }

    private double[] valueReformat(int[] a){
        double[] reformatted = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            if(a[i]==index+1){
                reformatted[i]=1;
            }
        }
        return reformatted;
    }

    private double[] reformat(int[] a){
        double[] reformatted = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            // Player after me
            if(a[i]==index+2){
                a[i]=1;
            }
            else{
                a[i]=0;
            }
        }
        return reformatted;
    }

    private double[] matrixMultiplication(double[][] A, double[] b){
        assert b.length==A[0].length;
        double[] result = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                result[i]+= b[i]*A[i][j];
            }
        }
        return result;
    }

    private void basicPrinting(){
        System.out.println("Memory");
        print52Matrix(memory);
        System.out.println("Enemy");
        print52Matrix(enemyCardOwnership);
    }
    private void print52Matrix(int[] m){
        System.out.print("[");
        for (int i = 0; i < m.length; i++) {
            System.out.print(" "+m[i]);
            if((i+1)%13==0){
                System.out.println("]");
                if(i!=m.length-1){
                    System.out.print("[");
                }
            }
        }
        System.out.println();
    }
    private void print52Matrix(double[] m){
        System.out.print("[");
        for (int i = 0; i < m.length; i++) {
            System.out.print(" "+m[i]);
            if((i+1)%13==0){
                System.out.println("]");
                if(i!=m.length-1){
                    System.out.print("[");
                }
            }
        }
        System.out.println();
    }

    public void printMatrices(){
        System.out.println("PROB MATRIX");
        System.out.println(Arrays.toString(probabilityMatrix));
        System.out.println("VALUE MATRIX");
        System.out.println(Arrays.toString(valueMatrix));
    }
}
