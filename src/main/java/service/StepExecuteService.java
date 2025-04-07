package service;

import exception.UndefinedException;
import model.StepElement;
import model.enums.StepElementType;
import model.enums.StepState;
import model.TestStep;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
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
            executeElement(step.getRegion(), element);
        }
    }
    private StepState executePassElement(TestStep step){
        return executeElement(step.getRegion(), step.getStepElements().get(StepElementType.PASS));
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
            return executeElement(step.getRegion(), element);
        }
        return StepState.NO_MATCH;
    }

    private void executeCloseElement(TestStep step){
        StepElement element = step.getStepElements().get(StepElementType.CLOSE);
        if(element != null){
            executeElement(step.getRegion(), element);
        }
    }

    private StepState executeElement(Region region, StepElement element){
        return switch (element.getAction()){
            case FIND -> find(region, element);
            case CLICK -> findAndClick(region, element);
            case TYPE, PASTE -> findAndText(region, element);
        };
    }

    private StepState find(Region region, StepElement element){
        return handleFindFailed(() -> SikulixUtils.findImage(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private StepState findAndClick(Region region, StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickOnFound(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private StepState findAndText(Region region, StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickAndText(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec(), element.getOutputText(), element.getAction(), element.isEnterKey()));
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
