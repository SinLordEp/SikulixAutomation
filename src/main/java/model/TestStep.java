package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.enums.StepElementType;

import java.util.EnumMap;

/**
 * @author Sin
 */
public class TestStep {
    private String name;
    private String description;
    private String jsonPath;
    private EnumMap<StepElementType, StepElement> stepElements = new EnumMap<>(StepElementType.class);

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

    public EnumMap<StepElementType, StepElement> getStepElements() {
        return stepElements;
    }

    public void setStepElements(EnumMap<StepElementType, StepElement> stepElements) {
        this.stepElements = stepElements;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @JsonIgnore
    public StepElement getPassElement(){
        return stepElements.get(StepElementType.PASS);
    }
    @JsonIgnore
    public StepElement getPreconditionElement(){
        return stepElements.get(StepElementType.PRECONDITION);
    }
    @JsonIgnore
    public StepElement getFailElement(){
        return stepElements.get(StepElementType.FAIL);
    }
    @JsonIgnore
    public StepElement getRetryElement(){
        return stepElements.get(StepElementType.RETRY);
    }
    @JsonIgnore
    public StepElement getCloseElement(){
        return stepElements.get(StepElementType.CLOSE);
    }
    @JsonIgnore
    public void setPassElement(StepElement passElement){
        stepElements.put(StepElementType.PASS, passElement);
    }
    @JsonIgnore
    public void setPreconditionElement(StepElement preconditionElement){
        stepElements.put(StepElementType.PRECONDITION, preconditionElement);
    }
    @JsonIgnore
    public void setFailElement(StepElement failElement){
        stepElements.put(StepElementType.FAIL, failElement);
    }
    @JsonIgnore
    public void setRetryElement(StepElement retryElement){
        stepElements.put(StepElementType.RETRY, retryElement);
    }
    @JsonIgnore
    public void setCloseElement(StepElement closeElement){
        stepElements.put(StepElementType.CLOSE, closeElement);
    }

    @Override
    public String toString() {
        return name;
    }
}
