package model;

import org.sikuli.script.Region;
import utils.TestAction;

public class TestStep {
    private Region region;
    private String imagePath;
    private String errorImagePath;
    private double similarity = 0.9;
    private int timeoutSec;
    private TestAction action = TestAction.FIND;
    private String outputText;
    private boolean enterKey = false;

    public TestStep(Region region, String imagePath, int timeoutSec, String errorImagePath) {
        this.region = region;
        this.imagePath = imagePath;
        this.timeoutSec = timeoutSec;
        this.errorImagePath = errorImagePath;
    }

    public TestStep(Region region, String imagePath, int timeoutSec, TestAction action, String errorImagePath) {
        this.region = region;
        this.imagePath = imagePath;
        this.action = action;
        this.timeoutSec = timeoutSec;
        this.errorImagePath = errorImagePath;
    }

    public TestStep(String imagePath, Region region, double similarity, int timeoutSec, TestAction action, String errorImagePath) {
        this.imagePath = imagePath;
        this.region = region;
        this.similarity = similarity;
        this.timeoutSec = timeoutSec;
        this.action = action;
        this.errorImagePath = errorImagePath;
    }

    public TestStep(Region region, String imagePath, int timeoutSec, TestAction action, String outputText, boolean enterKey, String errorImagePath) {
        this.region = region;
        this.imagePath = imagePath;
        this.timeoutSec = timeoutSec;
        this.action = action;
        this.outputText = outputText;
        this.enterKey = enterKey;
        this.errorImagePath = errorImagePath;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(int timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    public TestAction getAction() {
        return action;
    }

    public void setAction(TestAction action) {
        this.action = action;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public boolean isEnterKey() {
        return enterKey;
    }

    public void setEnterKey(boolean enterKey) {
        this.enterKey = enterKey;
    }

    public String getErrorImagePath() {
        return errorImagePath;
    }

    public void setErrorImagePath(String errorImagePath) {
        this.errorImagePath = errorImagePath;
    }
}
