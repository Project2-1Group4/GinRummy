package temp.GamePlayers.GameTreeAIs.MCTS;

// Does MCTS on x amount of created perfect information games
public class MCTSv1 extends MCTS{

    private final int simulations = 10; // Nb of perfect games simulated

    public MCTSv1(int seed){
        super(seed);
    }
    public MCTSv1(){
        super();
    }
    @Override
    protected void monteCarloTreeSearch(MCTSNode root, KnowledgeBase knowledge){
        if(knowledge.otherPlayer.size()+knowledge.unknown.size() <= 12){
            System.out.println("This game world is invalid and we should stop searching");
        }

        for (int i = 0; i < simulations; i++) {
            KnowledgeBase generated = generateRandomWorld(knowledge);

            // Here the children shouldn't all go to the same root
            // It's convenient for MCTS to sometimes go to one of the leaf nodes isntead of the original root
            // TODO: Fix that up later

            MCTSNode generatedRoot = getPossibleMoves(new MCTSNode(null, null), generated);
            mcts(generatedRoot, generated);
            merge(root, generatedRoot);
        }
        System.out.println("PRE");
        print(root,null);
        averageOutDeckPicks(root);
        rootBackProp(root);
        System.out.println("POST");
        print(root,null);
    }

    private void rootBackProp(MCTSNode root){
        for (MCTSNode child : root.children) {
            backPropagate(child);
        }
    }

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