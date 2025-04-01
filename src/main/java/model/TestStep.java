package model;

import java.util.HashMap;

/**
 * @author Sin
 */
public class TestStep {
    private String name;
    private String description;
    private String jsonPath;
    private HashMap<StepElementType, StepElement> stepElements = new HashMap<>();

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

    public void setStepElement(StepElementType type, StepElement element){
        stepElements.put(type, element);
    }

    public void setStepElements(HashMap<StepElementType, StepElement> stepElements) {
        this.stepElements = stepElements;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public StepElement getPassElement(){
        return stepElements.get(StepElementType.PASS);
    }

    public StepElement getPreconditionElement(){
        return stepElements.get(StepElementType.PRECONDITION);
    }

    public StepElement getFailElement(){
        return stepElements.get(StepElementType.FAIL);
    }

    public StepElement getRetryElement(){
        return stepElements.get(StepElementType.RETRY);
    }

    public StepElement getCloseElement(){
        return stepElements.get(StepElementType.CLOSE);
    }

    @Override
    public String toString() {
        return name;
    }
}
