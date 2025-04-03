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

    public static StepState execute(TestStep step){
        StepState state = StepState.NO_MATCH;
        // Precondition check
        if(step.getStepElements().get(StepElementType.PRECONDITION) != null){
            executeElement(step.getStepElements().get(StepElementType.PRECONDITION));
        }
        // Pass element check then Retry element check if the first attempt failed (Could add retry times)
        if(executeElement(step.getStepElements().get(StepElementType.PASS)) == StepState.MATCHED
                || ((step.getStepElements().get(StepElementType.RETRY) != null && executeElement(step.getStepElements().get(StepElementType.RETRY)) == StepState.MATCHED)
                && executeElement(step.getStepElements().get(StepElementType.PASS)) == StepState.MATCHED)){
            state = StepState.PASS;
        }

        // Fail element check
        if(state != StepState.PASS
                && step.getStepElements().get(StepElementType.FAIL) != null
                && executeElement(step.getStepElements().get(StepElementType.FAIL)) == StepState.MATCHED){
            state = StepState.FAIL;
        }

        // Close element check
        if(step.getStepElements().get(StepElementType.CLOSE)  != null){
            executeElement(step.getStepElements().get(StepElementType.CLOSE));
        }
        return state;
    }

    private static StepState executeElement(StepElement element){
        return switch (element.getAction()){
            case FIND -> find(element);
            case CLICK -> findAndClick(element);
            case TYPE, PASTE -> findAndText(element);
        };
    }

    private static StepState find(StepElement element){
        return handleFindFailed(() -> SikulixUtils.findImage(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private static StepState findAndClick(StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickOnFound(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private static StepState findAndText(StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickAndText(element.getRegion(), element.getPath(), element.getSimilarity(), element.getTimeoutSec(), element.getOutputText(), element.getAction(), element.isEnterKey()));
    }


    private static StepState handleFindFailed(FindFailedReturnBool function){
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
