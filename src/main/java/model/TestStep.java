package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.enums.StepElementType;
import org.sikuli.script.Region;

import java.util.EnumMap;

/**
 * @author Sin
 */
public class TestStep {
    private String name;
    private String description;
    private String jsonPath;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
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

    @JsonIgnore
    public void setRegion(Region region) {
        this.x = region.x;
        this.y = region.y;
        this.width = region.w;
        this.height = region.h;
    }

    @JsonIgnore
    public Region getRegion(){
        return new Region(x,y,width,height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    @Override
    public String toString() {
        return name;
    }
}
