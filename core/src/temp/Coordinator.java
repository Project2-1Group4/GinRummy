package temp;

import com.badlogic.gdx.ScreenAdapter;
import temp.GameActors.ForceActor;
import temp.GameActors.GameActor;
import temp.GameLogic.GameActions.*;
import temp.GameLogic.GameState.Executor;
import temp.GameLogic.GameState.State;
import temp.GameLogic.TreeExpander;
import temp.Graphics.Graphics;

import java.util.List;

/**
 * Handles coordination between actors||validator||executor||graphics
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

    private void oncePerStep() {
        newStep = false;
    }

    private void handleTurn(GameActor curActor, State.StepInTurn step, boolean outOfTime) {
        if(GameRules.print) if(outOfTime) System.out.println("FORCE "+step.name());

        Action action=null;
        if(step == State.StepInTurn.EndOfRound){
            EndOfRound();
        }
        else if(outOfTime){
            action = act(new ForceActor(curActor),step);
        }
        else{
            action = act(curActor,step);
        }

        if(action instanceof LayoffAction){
            newStep = true;
        }
        if(Executor.execute(action,currentGameState)){
            Executor.nextStep(currentGameState);
            newStep = true;
        }
    }

    private Action act(GameActor actor, State.StepInTurn step) {
        List<? extends Action> possibleActions = TreeExpander.getPossibleActions(currentGameState);
        switch (step) {
            case KnockOrContinue:
                return actor.knockOrContinue((List<KnockAction>) possibleActions);
            case Pick:
                return actor.pickDeckOrDiscard((List<PickAction>) possibleActions);
            case Discard:
                return actor.discardCard((List<DiscardAction>) possibleActions);
            case LayoutConfirmation:
                return actor.confirmLayout((List<LayoutConfirmationAction>) possibleActions);
            case LayOff:
                if (currentGameState.getKnocker().viewHandLayout().getDeadwood() == 0) {
                    Executor.endRound(currentGameState);
                    break;
                }
                return actor.layOff((List<LayoffAction>) possibleActions);
        }
        return null;
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
}