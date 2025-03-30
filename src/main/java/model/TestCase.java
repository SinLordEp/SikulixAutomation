package model;

import utils.SikulixUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sin
 */
public class TestCase {
    private String name;
    private ArrayList<TestStep> steps = new ArrayList<>();

    public TestCase() {
    }

    public TestCase(String name) {
        this.name = name;
        SikulixUtils.setImagePath(name);
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
}
