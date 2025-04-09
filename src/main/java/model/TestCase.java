package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sin
 */
public class TestCase {
    private String name;
    private ArrayList<TestStep> steps = new ArrayList<>();
    private ArrayList<String> params = new ArrayList<>();
    @JsonIgnore
    private boolean selected = false;
    @JsonIgnore
    private int currentStep = -1;

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

    public ArrayList<String> getParams() {
        return params;
    }
    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    @JsonIgnore
    public int getStepCount() {
        return steps.size();
    }
    public void addParam(String param) {
        this.params.add(param);
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

    @Override
    public String toString() {
        return name;
    }


}
