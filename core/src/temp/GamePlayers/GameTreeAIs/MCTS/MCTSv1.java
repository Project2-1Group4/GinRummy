package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.Entities.Turn;
import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.RoundState;

// Does MCTS for time given on simulations
public class MCTSv1 extends MCTS{

    public int simulations = 10;

    public MCTSv1(Integer seed){
        super(seed);
    }
    public MCTSv1(){
        this(null);
    }

    @Override
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge){
        for (int i = 0; i < simulations; i++) {
            if(print){
                System.out.println("Simulation "+i);
            }
            RoundState generated = completeUnknownInformation(knowledge, new Turn(step, index));
            MCTSNode generatedRoot = ExpandNode(new MCTSNode(null, null, this), generated);
            mcts(generatedRoot, generated);
            merge(root, generatedRoot);
        }
        //Can check what the best deck pick would be here.
    }

    private void merge(MCTSNode main, MCTSNode secondary){
        for (MCTSNode secondC : secondary.children) {
            boolean found = false;
            for (MCTSNode mainC : main.children) {
                if(secondC.equals(mainC)){
                    found = true;
                    mainC.children.addAll(secondC.children);
                    mainC.wins+=secondC.wins;
                    mainC.rollouts+=secondC.rollouts;
                    main.wins+=secondC.wins;
                    main.rollouts+=secondC.rollouts;
                    break;
                }
            }
            if(!found){
                main.children.add(secondC);
                main.wins+=secondC.wins;
                main.rollouts+=secondC.rollouts;
            }
        }
    }

}