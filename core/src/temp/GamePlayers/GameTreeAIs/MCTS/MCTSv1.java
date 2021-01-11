package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.GameLogic.Entities.Step;
import temp.GameLogic.Entities.Turn;
import temp.GameLogic.GameActions.PickAction;
import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.RoundState;

// Does MCTS on x amount of created perfect information games
public class MCTSv1 extends MCTS{

    protected final int baseSimulations = 10; // Nb of perfect games simulated

    protected final int simulations;

    public MCTSv1(int rolloutsPerSimulation, int rolloutsPerNode,double explorationParam, int simulations , Integer seed){
        super(rolloutsPerSimulation, rolloutsPerNode, explorationParam, seed);
        this.simulations = simulations;
    }
    public MCTSv1(int rolloutsPerSimulation, int rolloutsPerNode,double explorationParam , Integer seed){
        super(rolloutsPerSimulation, rolloutsPerNode, explorationParam, seed);
        this.simulations = baseSimulations;
    }
    public MCTSv1(Integer seed){
        this(baseRolloutsPerSim, baseRolloutsPerNode, baseExplorationParam,seed);
    }
    public MCTSv1(){
        this(null);
    }

    @Override
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge){
        for (int i = 0; i < simulations; i++) {
            RoundState generated = new RoundState(completeUnknownInformation(knowledge),new Turn(step, index));
            MCTSNode generatedRoot = ExpandNode(new MCTSNode(null, null, this), generated);
            mcts(generatedRoot, generated);
            merge(root, generatedRoot);
        }
        //Can check what the best deck pick would be here.
        mergeDeckPicksIntoOne(root, step);
    }

    private void merge(MCTSNode main, MCTSNode secondary){
        for (MCTSNode secondC : secondary.children) {
            boolean found = false;
            for (MCTSNode mainC : main.children) {
                if(secondC.equals(mainC)){
                    found = true;
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
    private void mergeDeckPicksIntoOne(MCTSNode node, Step step){
        if(step!=Step.Pick) return;
        int rolloutSum=0;
        int winSum=0;
        MCTSNode deck=null;
        for (int i = node.children.size() - 1; i >= 0; i--) {
            MCTSNode c = node.children.get(i);
            if(((PickAction)c.action).deck) {
                if (((PickAction)c.action).card() == null) {
                    deck = node.children.get(i);
                    continue;
                }
                winSum+=c.wins;
                rolloutSum+=c.rollouts;
                node.children.remove(i);
            }
        }
        if(deck!=null){
            deck.wins+=winSum;
            deck.rollouts+=rolloutSum;
        }
    }
}