package service;

import exception.UndefinedException;
import model.StepElement;
import model.StepElementType;
import model.StepState;
import model.TestStep;
import org.sikuli.script.FindFailed;
import util.SikulixUtils;

/**
 * @author Sin
 */
public class StepExecuteService {

    @FunctionalInterface
    interface FindFailedReturnBool {
        void run() throws FindFailed;
    }

    public StepExecuteService() {
        //No parameter needed for now
    }

    public StepState execute(TestStep step){
        StepState state = StepState.NO_MATCH;
        executePreconditionElement(step);
        state = executePassElement(step) == StepState.MATCHED ? StepState.PASS : state;
        if(state != StepState.PASS){
            state = executeRetryElement(step) == StepState.MATCHED ? StepState.PASS : state;
        }
        if(state != StepState.PASS){
            state = executeFailElement(step) == StepState.MATCHED ? StepState.FAIL : StepState.NO_MATCH;
        }
        executeCloseElement(step);
        return state;
    }

    private void executePreconditionElement(TestStep step){
        StepElement element = step.getStepElements().get(StepElementType.PRECONDITION);
        if(element != null){
            executeElement(element);
        }
    }
    private StepState executePassElement(TestStep step){
        return executeElement(step.getStepElements().get(StepElementType.PASS));
    }

    private StepState executeRetryElement(TestStep step){
        StepElement element = step.getStepElements().get(StepElementType.RETRY);
        if(element != null){
            return executePassElement(step);
        }
        return StepState.NO_MATCH;
    }

    private StepState executeFailElement(TestStep step){
        StepElement element = step.getStepElements().get(StepElementType.FAIL);
        if(element != null){
            return executeElement(element);
        }
        return StepState.NO_MATCH;
    }

    private void executeCloseElement(TestStep step){
        StepElement element = step.getStepElements().get(StepElementType.CLOSE);
        if(element != null){
            executeElement(element);
        }
    }

    private StepState executeElement(StepElement element){
        return switch (element.getAction()){
            case FIND -> find(element);
            case CLICK -> findAndClick(element);
            case TYPE, PASTE -> findAndText(element);
        };
    }

    private StepState find(StepElement element){
        return handleFindFailed(() -> SikulixUtils.findImage(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private StepState findAndClick(StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickOnFound(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private StepState findAndText(StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickAndText(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec(), element.getOutputText(), element.getAction(), element.isEnterKey()));
    }


    private StepState handleFindFailed(FindFailedReturnBool function){
        try{
            function.run();
            return StepState.MATCHED;
        }catch (FindFailed e){
            return StepState.NO_MATCH;
        }catch(Exception e){
            throw new UndefinedException("Undefined exception was threw in Sikulix matching process");
        }
    }


}
