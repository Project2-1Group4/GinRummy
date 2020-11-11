package temp;

import com.badlogic.gdx.ScreenAdapter;
import temp.GameLogic.GameState.StateBuilder;
import temp.GamePlayers.ForcePlayer;
import temp.GamePlayers.GamePlayer;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.Graphics.Graphics;

/**
 * Handles coordination between players||validator||executor||graphics
 */
public class Coordinator extends ScreenAdapter {

    private Graphics graphics;
    private State currentGameState;

    private boolean newStep = true;

    public Coordinator() {
        graphics = new Graphics();
    }

    @Override
    public void show() {
        currentGameState = new StateBuilder().setNumberOfPlayers(2).build();
        this.currentGameState = Executor.startNewRound(500, currentGameState);
    }

    /**
     * Main logic loop. Everything goes from here.
     *
     * @param delta time difference between previous render loop and now
     */
    @Override
    public void render(float delta) {

        if (newStep) {
            // only called upon new round
            oncePerStep();
        }
        GamePlayer curPlayer = currentGameState.getPlayer();
        handleTurn(curPlayer, currentGameState.getStep(), Executor.update(currentGameState, delta));

        if (newStep) {
            oncePerStep();
        }

        graphics.render(currentGameState);
    }

    @Override
    public void resize(int width, int height) {
        graphics.resize(width, height);
    }

    /**
     * Gets called every time an player makes a valid move
     */
    private void oncePerStep() {
        newStep = false;
    }

    /* GAME TURNS */
    /**
     *  Handles the main turn logic
     *
     * @param curPlayer player that needs to make the move
     * @param step turn in the players turn round
     * @param outOfTime true if you need to force a move
     */
    private void handleTurn(GamePlayer curPlayer, State.StepInTurn step, boolean outOfTime) {
        if(GameRules.print) if(outOfTime) System.out.println("FORCE "+step.name());

        if(currentGameState.getDeckSize()==2){
            if(GameRules.print) System.out.println("FORCE END OF ROUND. 2 CARDS LEFT IN DECK");
            currentGameState = Executor.startNewRound(500,currentGameState);
        }

        switch (step) {
            case KnockOrContinue:
                if (outOfTime) {
                    knockOrContinue(new ForcePlayer(curPlayer));
                } else {
                    knockOrContinue(curPlayer);
                }
                break;
            case Pick:
                if (outOfTime) {
                    pick(new ForcePlayer(curPlayer));
                } else {
                    pick(curPlayer);
                }
                break;
            case Discard:
                if (outOfTime) {
                    discard(new ForcePlayer(curPlayer));
                } else {
                    discard(curPlayer);
                }
                break;
            case LayoutConfirmation:
                if (outOfTime) {
                    layoutConfirmation(new ForcePlayer(curPlayer));
                } else {
                    layoutConfirmation(curPlayer);
                }
                break;
            case LayOff:
                if (currentGameState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                    Executor.endRound(currentGameState);
                    break;
                }
                if (outOfTime) {
                    layOff(new ForcePlayer(curPlayer));
                } else {
                    layOff(curPlayer);
                }
                break;
            case EndOfRound:
                EndOfRound();
        }
    }

    private void knockOrContinue(GamePlayer curPlayer) {
        Boolean move = curPlayer.knockOrContinue();
        if (move!=null && Executor.knockOrContinue(move, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void pick(GamePlayer curPlayer) {
        Boolean move = curPlayer.pickDeckOrDiscard(currentGameState.getDeckSize(), currentGameState.peekDiscardTop());
        if (move!=null && Executor.pickDeckOrDiscard(move, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void discard(GamePlayer curPlayer) {
        MyCard cardToDiscard = curPlayer.discardCard();
        if (cardToDiscard!=null && Executor.discardCard(cardToDiscard, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void layoutConfirmation(GamePlayer curPlayer) {
        HandLayout set = curPlayer.confirmLayout();
        if (set!=null && Executor.updateHandLayout(set, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void layOff(GamePlayer curPlayer) {
        Layoff layOffs = curPlayer.layOff(currentGameState.getKnockerState().viewMelds());
        if (layOffs!=null && Executor.layOff(layOffs,currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    /**
     * Only called once everything is done. The melds and the laying off.
     * Assigns points and starts a new round
     */
    private void EndOfRound() {
        Executor.assignPoints(currentGameState);
        currentGameState = Executor.startNewRound(500, currentGameState);
        newStep = true;
    }
}
