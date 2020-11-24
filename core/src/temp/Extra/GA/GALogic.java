package temp.Extra.GA;

import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.GameRules;

// Modify based on the experiment you're running
public class GALogic {

    private State currentGameState;
    private boolean roundEnd;
    public Result play(GAPlayer player1, GAPlayer player2, int seed){
        currentGameState = new StateBuilder()
                .setSeed(seed)
                .addPlayer(player1.player)
                .addPlayer(player2.player)
                .build();
        roundEnd = false;
        currentGameState = Executor.startNewRound(500,currentGameState);
        while(currentGameState.getWinner()==null){
            update();
            if(roundEnd){
                Executor.assignPoints(currentGameState);
            }
            if(currentGameState.getRoundTurn()>= 200 || roundEnd){
                break;
            }
        }
        GAPlayer winner = null;
        if(currentGameState.getWinner()!=null){
            winner = currentGameState.getWinner()==0? player1:player2;
        }
        return new Result(player1,player2,winner,
                currentGameState.getPlayerStates().get(0).viewHandLayout(),
                currentGameState.getPlayerStates().get(1).viewHandLayout(),
                currentGameState.getRoundTurn());
    }

    private void update(){
        GamePlayer player = currentGameState.getPlayer();
        Action action = getAction(player,currentGameState);
        boolean executed = Executor.execute(action,currentGameState);
        if(action==null){
            System.out.println("ERROR ERROR ERROR BOT RETURNS NO MOVE");
        }
        if(!executed){
            System.out.println("ERROR ERROR ERRROR BOT RETURNS NON-EXECUTABLE MOVE");
        }
    }

    private Action getAction(GamePlayer curPlayer,State curState) {
        // TODO maybe move elsewhere
        if (curState.getDeckSize() <= GameRules.minCardsInDeck) {
            if (GameRules.print) System.out.println("FORCE END OF ROUND. 2 CARDS LEFT IN DECK");
            curState = Executor.startNewRound(500, curState);
        }

        Action action = null;
        switch (curState.getStep()) {
            case KnockOrContinue:
                action = knockOrContinue(curPlayer);
                break;
            case Pick:
                action = pick(curPlayer);
                break;
            case Discard:
                action = discard(curPlayer);
                break;
            case LayoutConfirmation:
                action = layoutConfirmation(curPlayer);
                break;
            case LayOff:
                if (currentGameState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                    Executor.endRound(currentGameState);
                } else {
                    action = layOff(curPlayer);
                    break;
                }
            case EndOfRound:
                roundEnd = true;
        }
        return action;
    }

    private KnockAction knockOrContinue(GamePlayer curPlayer) {
        Boolean move = curPlayer.knockOrContinue();
        if(move==null){
            return null;
        }else if(move){
            return new KnockAction(currentGameState.getPlayerNumber(),true,curPlayer.viewHandLayout());
        }else{
            return new KnockAction(currentGameState.getPlayerNumber(),false,curPlayer.viewHandLayout());
        }
    }

    private PickAction pick(GamePlayer curPlayer) {
        Boolean move = curPlayer.pickDeckOrDiscard(currentGameState.getDeckSize(), currentGameState.peekDiscardTop());
        if(move==null){
            return null;
        }else if(move){
            return new PickAction(currentGameState.getPlayerNumber(),true,null);
        }else{
            return new PickAction(currentGameState.getPlayerNumber(), false, currentGameState.peekDiscardTop());
        }
    }

    private DiscardAction discard(GamePlayer curPlayer) {
        MyCard cardToDiscard = curPlayer.discardCard();
        if(cardToDiscard!=null){
            return new DiscardAction(currentGameState.getPlayerNumber(),cardToDiscard);
        }
        return null;
    }

    private LayoutConfirmationAction layoutConfirmation(GamePlayer curPlayer) {
        HandLayout set = curPlayer.confirmLayout();
        if(set!=null){
            return new LayoutConfirmationAction(currentGameState.getPlayerNumber(), set);
        }
        return null;
    }

    private LayoffAction layOff(GamePlayer curPlayer) {
        Layoff layOffs = curPlayer.layOff(currentGameState.getKnockerState().viewMelds());
        if(layOffs!=null){
            return new LayoffAction(currentGameState.getPlayerNumber(),layOffs);
        }
        return null;
    }
}
