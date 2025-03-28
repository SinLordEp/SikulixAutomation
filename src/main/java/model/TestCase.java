package model;

import org.sikuli.script.Region;
import utils.SikulixUtils;
import utils.TestAction;

import java.util.ArrayList;
import java.util.List;

public class TestCase {
    private final String name;
    private final ArrayList<TestStep> steps = new ArrayList<>();
    private final Region region;

    public TestCase(String name, Region region) {
        this.name = name;
        SikulixUtils.setImagePath(name);
        this.region = region;
    }

    public void addMatchStep(String imagePath, int timeoutSec, String errorImagePath){
        steps.add(new TestStep.Builder()
                .setRegion(region)
                .setImagePath(imagePath)
                .setTimeoutSec(timeoutSec)
                .setErrorImagePath(errorImagePath)
                .build());
    }

    public void addClickStep(String imagePath, int timeoutSec, String errorImagePath){
        steps.add(new TestStep.Builder()
                .setRegion(region)
                .setImagePath(imagePath)
                .setTimeoutSec(timeoutSec)
                .setAction(TestAction.CLICK)
                .setErrorImagePath(errorImagePath)
                .build());

    }

    public void addTypeStep(String imagePath, int timeoutSec, String text, boolean enterKey, String errorImagePath){
        steps.add(new TestStep.Builder()
                .setRegion(region)
                .setImagePath(imagePath)
                .setTimeoutSec(timeoutSec)
                .setAction(TestAction.TYPE)
                .setOutputText(text)
                .setEnterKey(enterKey)
                .setErrorImagePath(errorImagePath)
                .build());

    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public String getName() {
        return name;
    }
}
