package model;

import org.sikuli.script.Region;

import java.util.HashMap;

/**
 * @author Sin
 */
public class TestStep {
    private String name;
    private String description;
    private final HashMap<StepElementType, StepElement> stepElements = new HashMap<>();

    public TestStep(){
        stepElements.put(StepElementType.PRECONDITION, null);
        stepElements.put(StepElementType.PASS, null);
        stepElements.put(StepElementType.FAIL, null);
        stepElements.put(StepElementType.RETRY, null);
        stepElements.put(StepElementType.CLOSE, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<StepElementType, StepElement> getStepElements() {
        return stepElements;
    }
}
