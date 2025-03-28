package model;

import org.sikuli.script.Region;
import utils.SikulixUtils;
import utils.TestAction;

import java.util.ArrayList;

public class TestCase {
    private String name;
    private final ArrayList<TestStep> steps = new ArrayList<>();
    private final Region region;

    public TestCase(String name, Region region) {
        this.name = name;
        SikulixUtils.setImagePath(name + "/");
        this.region = region;
    }

    public void addMatchStep(String imagePath, int timeoutSec, String errorImagePath){
        steps.add(new TestStep(region, imagePath, timeoutSec, errorImagePath));
    }

    public void addClickStep(String imagePath, int timeoutSec, String errorImagePath){
        steps.add(new TestStep(region, imagePath, timeoutSec, TestAction.CLICK, errorImagePath));
    }

    public void addTypeStep(String imagePath, int timeoutSec, String text, boolean enterKey, String errorImagePath){
        steps.add(new TestStep(region, imagePath, timeoutSec, TestAction.TYPE, text, enterKey, errorImagePath));
    }

    public ArrayList<TestStep> getSteps() {
        return steps;
    }
}
