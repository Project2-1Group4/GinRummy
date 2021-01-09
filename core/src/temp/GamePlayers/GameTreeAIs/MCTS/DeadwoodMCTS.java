package temp.GamePlayers.GameTreeAIs.MCTS;

import temp.Extra.PostGameInformation.Result;

public abstract class DeadwoodMCTS extends MCTS{

    public DeadwoodMCTS(int seed){
        super(seed);
    }
    public DeadwoodMCTS(){
        super();
    }

    protected double getRoundValue(Result result){
        if(result.winner==null){
            return 0;
        }
        int deadwood = result.r.layouts()[index].deadwoodValue();
        int deadwoodDiff = 0;
        for (int i = 0; i < result.r.layouts().length; i++) {
            deadwoodDiff+= result.r.layouts()[i].deadwoodValue() - deadwood;
        }
        return deadwoodDiff;
    }
}
