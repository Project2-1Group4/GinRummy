package temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.mygdx.game.GinRummy;
import temp.Extra.Tests.EndOfRoundInfo;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.GameState.StateBuilder;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.MyCard;
import temp.GamePlayers.CombinePlayer;
import temp.GamePlayers.GameTreeAIs.MinimaxPruningAI;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GamePlayers.GreedyAIs.basicGreedyTest;
import temp.GamePlayers.GreedyAIs.meldBuildingGreedy;
import temp.GamePlayers.KeyboardPlayer;
import temp.GamePlayers.MousePlayer.MousePlayer;
import temp.Graphics.Graphics;

// Handles coordination between players||validator||executor||graphics
public class Coordinator extends ScreenAdapter {

    private final Graphics graphics;
    private State currentGameState;
    private final GinRummy master;

    private boolean newStep = true;
    private boolean roundEnd = false;

    public Coordinator(GinRummy master) {
        this.master = master;

        currentGameState = new StateBuilder()
                .addPlayer(getPlayer(master.name1))
                .addPlayer(getPlayer(master.name2))
                .build();
        graphics = new Graphics();
    }

    private GamePlayer getPlayer(String s){
        switch (s.toLowerCase()) {
            case "minimax":
            case "minimaxpruningai":
                return new MinimaxPruningAI();
            case "basic greedy":
            case "basicgreedy":
                return new basicGreedyTest();
            case "meld greedy":
            case "meldgreedy":
            case "greedy":
                return new meldBuildingGreedy();
            case "keyboard":
                return new KeyboardPlayer();
            case "mouse":
                return new MousePlayer();
            default:
                return CombinePlayer.getBaseCombinePlayer();
        }
    }
    @Override
    public void show() {
        this.currentGameState = Executor.startGame(500, currentGameState);
        if (currentGameState.endOfGame()) {
            gameEnded();
        }
    }

    public void gameEnded() {
        System.out.println(new EndOfRoundInfo(currentGameState, true));
        if(currentGameState.getWinnerIndex()==0){
            master.winner = "Player 1 "+master.name1;
        }
        else if(currentGameState.getWinnerIndex()==1){
            master.winner = "Player 2 "+master.name2;
        }
        master.changeScreen(GinRummy.END);
    }

    /**
     * Main logic loop. Everything goes from here.
     *
     * @param delta time difference between previous render loop and now
     */
    @Override
    public void render(float delta) {
        if (currentGameState != null) {

            if (newStep) {
                // only called upon new round
                oncePerStep();
            }

            GamePlayer curPlayer = currentGameState.getPlayer();
            boolean outOfTime = Executor.update(currentGameState, delta);

            if (GameRules.print) if (outOfTime) System.out.println("FORCE " + currentGameState.getStep());

            Action action = handleTurn(outOfTime ? new ForcePlayer(curPlayer) : curPlayer, currentGameState.getStep());

            if (Executor.execute(action, currentGameState)) {
                oncePerStep();
            }

            graphics.render(currentGameState);
        }
        if (roundEnd) {
            endOfRound();
        }
    }

    @Override
    public void resize(int width, int height) {
        graphics.resize(width, height);
    }

    /**
     * Gets called every time an player makes a valid move
     */
    private void oncePerStep() {
        Gdx.input.setInputProcessor(currentGameState.getPlayer().getProcessor());
        newStep = false;
    }

    // GAME TURNS

    /**
     * Handles the main turn logic
     *
     * @param curPlayer player that needs to make the move
     * @param step      turn in the players turn round
     */
    private Action handleTurn(GamePlayer curPlayer, State.StepInTurn step) {
        // TODO maybe move elsewhere
        if (currentGameState.getDeckSize() <= GameRules.minCardsInDeck) {
            if (GameRules.print) System.out.println("FORCE END OF ROUND. 2 CARDS LEFT IN DECK");
            currentGameState = Executor.startNewRound(500, currentGameState);
            return null;
        }
        if (currentGameState.getTurn() >= GameRules.maxTurnsInARound) {
            roundEnd = true;
            return null;
        }

        Action action = null;
        switch (step) {
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
        if (move == null) {
            return null;
        } else if (move) {
            return new KnockAction(currentGameState.getPlayerNumber(), true, curPlayer.viewHandLayout());
        } else {
            return new KnockAction(currentGameState.getPlayerNumber(), false, curPlayer.viewHandLayout());
        }
    }

    private PickAction pick(GamePlayer curPlayer) {
        Boolean move = curPlayer.pickDeckOrDiscard(currentGameState.getDeckSize(), currentGameState.peekDiscardTop());
        if (move == null) {
            return null;
        } else if (move) {
            return new PickAction(currentGameState.getPlayerNumber(), true, null);
        } else {
            return new PickAction(currentGameState.getPlayerNumber(), false, currentGameState.peekDiscardTop());
        }
    }

    private DiscardAction discard(GamePlayer curPlayer) {
        MyCard cardToDiscard = curPlayer.discardCard();
        if (cardToDiscard != null) {
            return new DiscardAction(currentGameState.getPlayerNumber(), cardToDiscard);
        }
        return null;
    }

    private LayoutConfirmationAction layoutConfirmation(GamePlayer curPlayer) {
        HandLayout set = curPlayer.confirmLayout();
        if (set != null) {
            return new LayoutConfirmationAction(currentGameState.getPlayerNumber(), set);
        }
        return null;
    }

    private LayoffAction layOff(GamePlayer curPlayer) {
        Layoff layOffs = curPlayer.layOff(currentGameState.getKnockerState().viewMelds());
        if (layOffs != null) {
            return new LayoffAction(currentGameState.getPlayerNumber(), layOffs);
        }
        return null;
    }

    /**
     * Only called once everything is done. The melds and the laying off.
     * Assigns points and starts a new round
     */
    private void endOfRound() {
        Executor.assignPoints(currentGameState);
        currentGameState = Executor.startNewRound(500, currentGameState);
        newStep = true;
        if (currentGameState.endOfGame()) {
            gameEnded();
        }
        roundEnd = false;
    }
}