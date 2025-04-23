package service;

import com.fasterxml.jackson.databind.JsonNode;
import exception.UndefinedException;
import model.StepElement;
import model.enums.DataSource;
import model.enums.StepElementType;
import model.enums.StepState;
import model.TestStep;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.SikulixUtils;

/**
 * @author Sin
 */
public class StepExecuteService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @FunctionalInterface
    interface FindFailedReturnBool {
        void run() throws FindFailed;
    }

    public StepExecuteService() {
        //No parameter needed for now
    }

    public StepState execute(TestStep step, JsonNode... params){
        logger.debug("Executing TestStep {}", step);
        StepState state;
        executePreconditionElement(step, params);
        state = (executePassElement(step, params) == StepState.MATCHED) ? StepState.PASS : StepState.NO_MATCH;
        if(state != StepState.PASS && executeRetryElement(step, params) == StepState.MATCHED){
            state = (executePassElement(step, params) == StepState.MATCHED) ? StepState.PASS : StepState.NO_MATCH;
        }
        if(state != StepState.PASS){
            state = (executeFailElement(step, params) == StepState.MATCHED) ? StepState.FAIL : StepState.NO_MATCH;
        }
        executeCloseElement(step, params);
        logger.debug("TestStep {} executed successfully with State {}", step, state);
        return state;
    }

    private void executePreconditionElement(TestStep step, JsonNode... params){
        StepElement element = step.getStepElements().get(StepElementType.PRECONDITION);
        if(element != null){
            logger.debug("Precondition element found, executing...");
            executeElement(step.getRegion(), element, params);
        }
    }
    private StepState executePassElement(TestStep step, JsonNode... params){
        logger.debug("Executing pass element...");
        return executeElement(step.getRegion(), step.getStepElements().get(StepElementType.PASS), params);
    }

    private StepState executeRetryElement(TestStep step, JsonNode... params){
        StepElement element = step.getStepElements().get(StepElementType.RETRY);
        if(element != null){
            logger.debug("Retry element found, executing...");
            if(executeElement(step.getRegion(), element, params) == StepState.MATCHED){
                return executePassElement(step, params);
            }
        }
        return StepState.NO_MATCH;
    }

    private StepState executeFailElement(TestStep step, JsonNode... params){
        StepElement element = step.getStepElements().get(StepElementType.FAIL);
        if(element != null){
            logger.debug("Fail element found, executing...");
            return executeElement(step.getRegion(), element, params);
        }
        return StepState.NO_MATCH;
    }

    private void executeCloseElement(TestStep step, JsonNode... params){
        StepElement element = step.getStepElements().get(StepElementType.CLOSE);
        if(element != null){
            logger.debug("Close element found, executing...");
            executeElement(step.getRegion(), element, params);
        }
    }

    private StepState executeElement(Region region, StepElement element, JsonNode... params){
        logger.debug("Executing operation {} in Region: x:{} y: {} width:{} height:{}", element.getAction(), region.getX(), region.getY(), region.getW(), region.getH());
        return switch (element.getAction()){
            case FIND -> find(region, element);
            case CLICK -> findAndClick(region, element);
            case TYPE, PASTE -> findAndText(region, element, params);
        };
    }

    private StepState find(Region region, StepElement element){
        return handleFindFailed(() -> SikulixUtils.findImage(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec()));
    }

    private StepState findAndClick(Region region, StepElement element){
        return handleFindFailed(() -> SikulixUtils.clickOnFound(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec(), element.getClickTime()));
    }

    private StepState findAndText(Region region, StepElement element, JsonNode... params){
        String output;
        if(element.getTextSource() == DataSource.JSON && params != null){
            output = params[0].get(element.getOutputText()).asText();
        }else{
            output = element.getOutputText();
        }
        logger.debug("JSON param : {} - Output text: {}",element.getOutputText(), output);
        return handleFindFailed(() -> SikulixUtils.clickAndText(region, element.getImageNameOrText(), element.getSimilarity(), element.getTimeoutSec(), element.getClickTime(), output, element.getAction(), element.isEnterKey()));
    }


    private StepState handleFindFailed(FindFailedReturnBool function){
        try{
            function.run();
            logger.debug("Element matched!");
            return StepState.MATCHED;
        }catch (FindFailed e){
            logger.debug("Element not found!");
            return StepState.NO_MATCH;
        }catch(Exception e){
            logger.debug("Unexpected exception is found: {}", e.getMessage());
            throw new UndefinedException("Undefined exception was threw in Sikulix matching process");
        }
    }


}
