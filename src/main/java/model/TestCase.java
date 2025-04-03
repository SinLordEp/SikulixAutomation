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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public TestStep getCurrentTestStep() {
        return currentStep == -1  ? steps.getFirst() : steps.get(currentStep);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void nextCurrentStep() {
        this.currentStep += 1;
    }

    public void resetCurrentStep() {
        this.currentStep = -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
