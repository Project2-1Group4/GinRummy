package temp;

import com.badlogic.gdx.Screen;
import temp.GameActors.ForceActor;
import temp.GameActors.GameActor;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.Layoff;
import temp.GameLogic.MELDINGOMEGALUL.HandLayout;
import temp.GameLogic.GameState.State;
import temp.GameLogic.MyCard;
import temp.Graphics.Graphics;

/**
 * Handles coordination between actors||validator||executor||graphics
 */
//TODO automatic meld creation needs to make no errors
//TODO automatic layoff needs to make no errors
public class Coordinator implements Screen {

    private Graphics graphics;
    private State currentGameState;

    public Coordinator() {
        graphics = new Graphics();
    }

    @Override
    public void show() {
        this.currentGameState = Executor.startNewRound(500, null);
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
        GameActor curActor = currentGameState.getActor();
        handleTurn(curActor, currentGameState.getStep(), Executor.update(currentGameState, delta));

        if (newStep) {
            oncePerStep();
        }

        graphics.render(currentGameState);
    }

    private boolean newStep = true;

    private void oncePerStep() {
        currentGameState.getActor().update(currentGameState);
        newStep = false;
    }

    private void handleTurn(GameActor curActor, State.StepInTurn step, boolean outOfTime) {
        if(GameRules.print) if(outOfTime) System.out.println("FORCE "+step.name());
        switch (step) {
            case KnockOrContinue:
                if (outOfTime) {
                    knockOrContinue(new ForceActor(curActor));
                } else {
                    knockOrContinue(curActor);
                }
                break;
            case Pick:
                if (outOfTime) {
                    pick(new ForceActor(curActor));
                } else {
                    pick(curActor);
                }
                break;
            case Discard:
                if (outOfTime) {
                    discard(new ForceActor(curActor));
                } else {
                    discard(curActor);
                }
                break;
            case MeldConfirmation:
                if (outOfTime) {
                    meldConfirmation(new ForceActor(curActor));
                } else {
                    meldConfirmation(curActor);
                }
                break;
            case LayOff:
                if (currentGameState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                    Executor.endRound(currentGameState);
                    break;
                }
                if (outOfTime) {
                    layOff(new ForceActor(curActor));
                } else {
                    layOff(curActor);
                }
                break;
            case EndOfRound:
                EndOfRound();
        }
    }

    private void knockOrContinue(GameActor curActor) {
        Boolean move = curActor.knockOrContinue();
        if (move!=null && Executor.knockOrContinue(move, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void pick(GameActor curActor) {
        Boolean move = curActor.pickDeckOrDiscard(currentGameState.isDeckEmpty(), currentGameState.peekDiscardTop());
        if (move!=null && Executor.pickDeckOrDiscard(move, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void discard(GameActor curActor) {
        MyCard cardToDiscard = curActor.discardCard();
        if (cardToDiscard!=null && Executor.discardCard(cardToDiscard, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void meldConfirmation(GameActor curActor) {
        HandLayout set = curActor.confirmMelds();
        if (set!=null && Executor.updateHandLayout(set, currentGameState)) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void layOff(GameActor curActor) {
        Layoff layOffs = curActor.layOff(currentGameState.getKnockerState().viewHandLayout().viewMelds());
        if(layOffs!=null && layOffs.meld!=null){
            Executor.layOff(layOffs, currentGameState);
        }
        if (layOffs!=null && layOffs.meld==null) {
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private void EndOfRound() {
        Executor.assignPoints(currentGameState);
        currentGameState = Executor.startNewRound(500, currentGameState);
        newStep = true;
    }

    @Override
    public void resize(int width, int height) {
        graphics.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
