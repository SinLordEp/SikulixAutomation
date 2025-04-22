package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.enums.CaseState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sin
 */
public class TestCase {
    private String name;
    private ArrayList<TestStep> steps = new ArrayList<>();

    @JsonIgnore
    private boolean selected = false;
    @JsonIgnore
    private int currentStep = -1;
    @JsonIgnore
    private boolean iterating = false;
    @JsonIgnore
    private CaseState state = CaseState.QUEUED;

    public TestCase() {
    }

    public TestCase(String name) {
        this.name = name;
    }

    public void addStep(TestStep step) {
        steps.add(step);
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSteps(ArrayList<TestStep> steps) {
        this.steps = steps;
    }

    @JsonIgnore
    public List<String> getParams() {
        List<String> params = new ArrayList<>();
        steps.forEach(step -> params.addAll(step.getJsonParams()));
        return params;
    }

    @JsonIgnore
    public int getStepCount() {
        return steps.size();
    }

    @JsonIgnore
    public boolean isSelected() {
        return selected;
    }
    @JsonIgnore
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @JsonIgnore
    public TestStep getCurrentTestStep() {
        return currentStep == -1  ? steps.getFirst() : steps.get(currentStep);
    }

    @JsonIgnore
    public int getCurrentStep() {
        return currentStep;
    }

    @JsonIgnore
    public void nextCurrentStep() {
        this.currentStep += 1;
    }

    @JsonIgnore
    public void resetCurrentStep() {
        this.currentStep = -1;
    }

    @JsonIgnore
    public boolean isIterating() {
        return iterating;
    }

    @JsonIgnore
    public void setIterating(boolean iterating) {
        this.iterating = iterating;
    }

    @JsonIgnore
    public CaseState getState() {
        return state;
    }

    @JsonIgnore
    public void setState(CaseState state) {
        this.state = state;
    }

    @JsonIgnore
    public TestCase deepCopy() {
        TestCase copy = new TestCase(this.name);
        ArrayList<TestStep> copiedSteps = new ArrayList<>();
        for (TestStep step : this.steps) {
            copiedSteps.add(step.deepCopy());
        }
        copy.setSteps(copiedSteps);
        copy.iterating = this.iterating;
        return copy;
    }


    @Override
    public String toString() {
        return name;
    }


}
