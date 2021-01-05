package temp.Extra.GA;

import temp.Extra.Tests.EndOfRoundInfo;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.PlayerState;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GameTreeAIs.MCTS.Knowledge;
import temp.GameRules;

import java.util.ArrayList;
import java.util.Stack;

// TODO implement Coordinator with this
public class GameLogic {

    private final boolean onlyBot;
    private final boolean fullGame;


    private boolean roundEnd;
    private boolean printEndOfRound = false;

    public GameLogic(){
        onlyBot = true;
        fullGame = true;
    }

    public GameLogic(boolean onlyBot, boolean fullGame){
        this.onlyBot = onlyBot;
        this.fullGame = fullGame;
    }

    public Result play(GamePlayer player1, GamePlayer player2, int seed) {
        State curState = new StateBuilder()
                .setSeed(seed)
                .addPlayer(player1)
                .addPlayer(player2)
                .build();
        curState = startGame(curState);

        while (!curState.endOfGame()) {
            curState = update(curState);
        }
        GamePlayer winner = null;
        if (curState.getWinnerID() != null) {
            winner = curState.getWinnerID().equals(player1.index)? player1 : player2;
        }
        return new Result(player1, player2, winner,
                curState.getPlayerStates().get(0).viewHandLayout(),
                curState.getPlayerStates().get(1).viewHandLayout(),
                curState.getTurn());
    }

    public State startGame(State startState){
        roundEnd = false;
        return Executor.startGame(500, startState);
    }

    public State newRound(State curState){
        return Executor.startNewRound(500, curState);
    }

    public State update(State curState){
        if (curState.getDeckSize() <= GameRules.minCardsInDeck) {
            if (GameRules.print) System.out.println("FORCE END OF ROUND. 2 CARDS LEFT IN DECK");
            roundEnd = true;
        }
        if(!roundEnd) {
            GamePlayer player = curState.getPlayer();
            Action action = getAction(player, curState);
            boolean executed = Executor.execute(action, curState);
            if (onlyBot && !roundEnd) {
                if (action == null) {
                    System.out.println("ERROR ERROR ERROR BOT RETURNS NO MOVE");
                }
                if (action!=null && !executed) {
                    System.out.println("ERROR ERROR ERROR BOT RETURNS NON-EXECUTABLE MOVE: "+action);
                }
            }
        }

        if (roundEnd) {
            Executor.assignPoints(curState);
            if(printEndOfRound) {
                System.out.println(new EndOfRoundInfo(curState, false));
            }
            if(fullGame) {
                curState = endOfRound(curState);
            }
        }

        if (curState.endOfGame() || (!fullGame && (curState.getTurn() >= GameRules.maxTurnsInARound || roundEnd))) {
            curState.endGame = true;
        }
        return curState;
    }

    private State endOfRound(State curState) {
        roundEnd = false;
        if(curState.endOfGame()){
            return curState;
        }
        return newRound(curState);
    }

    private Action getAction(GamePlayer curPlayer, State curState) {

        Action action = null;
        switch (curState.getStep()) {
            case KnockOrContinue:
                action = knockOrContinue(curPlayer, curState);
                break;
            case Pick:
                action = pick(curPlayer, curState);
                break;
            case Discard:
                action = discard(curPlayer, curState);
                break;
            case LayoutConfirmation:
                action = layoutConfirmation(curPlayer, curState);
                break;
            case LayOff:
                if (curState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                    Executor.endRound(curState);
                } else {
                    action = layOff(curPlayer, curState);
                    break;
                }
            case EndOfRound:
                roundEnd = true;
        }
        return action;
    }

    private KnockAction knockOrContinue(GamePlayer curPlayer, State curState) {
        Boolean move = curPlayer.knockOrContinue();
        if (move == null) {
            return null;
        } else if (move) {
            return new KnockAction(curState.getPlayerNumber(), true, curPlayer.viewHandLayout());
        } else {
            return new KnockAction(curState.getPlayerNumber(), false, curPlayer.viewHandLayout());
        }
    }

    private PickAction pick(GamePlayer curPlayer, State curState) {
        Boolean move = curPlayer.pickDeckOrDiscard(curState.getDeckSize(), curState.peekDiscardTop());
        if (move == null) {
            return null;
        } else if (move) {
            return new PickAction(curState.getPlayerNumber(), true, null);
        } else {
            return new PickAction(curState.getPlayerNumber(), false, curState.peekDiscardTop());
        }
    }

    private DiscardAction discard(GamePlayer curPlayer, State curState) {
        MyCard cardToDiscard = curPlayer.discardCard();
        if (cardToDiscard != null) {
            return new DiscardAction(curState.getPlayerNumber(), cardToDiscard);
        }
        return null;
    }

    private LayoutConfirmationAction layoutConfirmation(GamePlayer curPlayer, State curState) {
        HandLayout set = curPlayer.confirmLayout();
        if (set != null) {
            return new LayoutConfirmationAction(curState.getPlayerNumber(), set);
        }
        return null;
    }

    private LayoffAction layOff(GamePlayer curPlayer, State curState) {
        Layoff layOffs = curPlayer.layOff(curState.getKnockerState().viewMelds());
        if (layOffs != null) {
            return new LayoffAction(curState.getPlayerNumber(), layOffs);
        }
        return null;
    }
}
