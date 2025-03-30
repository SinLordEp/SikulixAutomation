package model;

import org.sikuli.script.Region;
import utils.SikulixUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sin
 */
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

    }

    public void addClickStep(String imagePath, int timeoutSec, String errorImagePath){

    }

    public void addTypeStep(String imagePath, int timeoutSec, String text, boolean enterKey, String errorImagePath){
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public String getName() {
        return name;
    }
}
