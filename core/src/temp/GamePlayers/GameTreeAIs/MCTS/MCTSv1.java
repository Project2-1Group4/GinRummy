package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.Extra.PostGameInformation.Result;
import temp.GameLogic.Entities.Turn;
import temp.GameLogic.Game;
import temp.GameLogic.GameActions.Action;
import temp.GameLogic.States.CardsInfo;
import temp.GameLogic.States.GameState;
import temp.GameLogic.States.RoundState;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;

import java.util.List;

// Does MCTS on x amount of created perfect information games
public class MCTSv1 extends MCTS{

    public static void main(String[] args) {
        GamePlayer[] players = new GamePlayer[]{
                new basicGreedyTest(),
                new basicGreedyTest()
        };
        Game g = new Game(players, 0);
        GameState r = g.playOutGame();
        System.out.println(Result.getScores(r.toResult()));
        //Result r = g.playOutRound();
        //System.out.println(r);
    }

    private final int simulations = 10; // Nb of perfect games simulated

    public MCTSv1(int seed){
        super(seed);
    }
    public MCTSv1(){
        super();
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, CardsInfo knowledge){
        for (int i = 0; i < simulations; i++) {
            //System.out.println("Simulation "+i);
            RoundState generated = new RoundState(completeUnknownInformation(knowledge),new Turn(step, 0));
            MCTSNode generatedRoot = ExpandNode(new MCTSNode(null, null), generated);
            mcts(generatedRoot, generated);
            //print(generatedRoot, null);
            merge(root, generatedRoot);
        }
        //System.out.println("PRE");
        //print(root,null);
        averageOutDeckPicks(root);
        rootBackProp(root);
        //System.out.println("POST");
        //print(root,null);
    }

    private void rootBackProp(MCTSNode root){
        for (MCTSNode child : root.children) {
            backPropagate(child);
        }
    }
    /**
     * Merges the children of main and second together into main
     * @param main at the same depth as second
     * @param second at the same depth as main
     */
    private void merge(MCTSNode main, MCTSNode second){
        for (int scnd = 0; scnd < second.children.size(); scnd++) {
            boolean found = false;
            for (int mn = 0; mn < main.children.size(); mn++) {
                if(main.children.get(mn).same(second.children.get(scnd))){
                    main.children.get(mn).rollouts+= second.children.get(scnd).rollouts;
                    main.children.get(mn).wins+= second.children.get(scnd).wins;
                    found = true;
                    break;
                }
            }
            if(!found){
                main.children.add(second.children.get(scnd));
            }
        }
    }
}