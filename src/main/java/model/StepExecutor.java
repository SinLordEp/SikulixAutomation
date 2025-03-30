package model;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import utils.SikulixUtils;

/**
 * @author Sin
 */
public class StepExecutor {

    @FunctionalInterface
    interface FindFailedReturnBool {
        void run() throws FindFailed;
    }

    public static StepState executeStep(TestStep step){

        return null;
    }

    public static StepState handleTestStep(TestStep step){
        StepState state = switch ((StepAction)step.getAction()){
            case FIND -> SikulixUtils.findImage(step);
            case CLICK -> SikulixUtils.clickOnFound(step);
            case TYPE, PASTE -> SikulixUtils.clickAndText(step);
        };
        // retry when retry image is not null and has match by clicking
        if(state == StepState.NO_MATCH && step.getRetryImagePath() != null && handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getRetryImagePath()).similar(step.getSimilarity()), step.getTimeoutSec()).click()) == StepState.MATCHED){
            state = SikulixUtils.findImage(step);
        }
        //return NO_MATCH when image has no match and error image is null
        if(state == StepState.NO_MATCH && step.getErrorImagePath() == null){
            return StepState.NO_MATCH;
        }
        //return FAIL when image has no match and error image is matched
        if(handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getErrorImagePath()).similar(step.getSimilarity()), step.getTimeoutSec())) == StepState.MATCHED){
            return StepState.FAIL;
        }
        return StepState.PASS;
    }


}
