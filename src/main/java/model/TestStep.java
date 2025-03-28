package model;

import org.sikuli.script.Region;
import utils.TestAction;

public class TestStep {
    private final Region region;
    private final String imagePath;
    private final String retryImagePath;
    private final String errorImagePath;
    private final double similarity;
    private final int timeoutSec;
    private final TestAction action;
    private final String outputText;
    private final boolean enterKey;

    private TestStep(Builder builder) {
        this.region = builder.region;
        this.imagePath = builder.imagePath;
        this.retryImagePath = builder.retryImagePath;
        this.errorImagePath = builder.errorImagePath;
        this.similarity = builder.similarity;
        this.timeoutSec = builder.timeoutSec;
        this.action = builder.action;
        this.outputText = builder.outputText;
        this.enterKey = builder.enterKey;

    }

    public static class Builder{
        private Region region;
        private String imagePath;
        private String retryImagePath = null;
        private String errorImagePath = null;
        private double similarity = 0.9;
        private int timeoutSec;
        private TestAction action = TestAction.FIND;
        private String outputText;
        private boolean enterKey = false;

        public Builder setRegion(Region region) {
            this.region = region;
            return this;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder setRetryImagePath(String retryImagePath) {
            this.retryImagePath = retryImagePath;
            return this;
        }

        public Builder setErrorImagePath(String errorImagePath) {
            this.errorImagePath = errorImagePath;
            return this;
        }

        public Builder setSimilarity(double similarity) {
            this.similarity = similarity;
            return this;
        }

        public Builder setTimeoutSec(int timeoutSec) {
            this.timeoutSec = timeoutSec;
            return this;
        }

        public Builder setAction(TestAction action) {
            this.action = action;
            return this;
        }

        public Builder setOutputText(String outputText) {
            this.outputText = outputText;
            return this;
        }

        public Builder setEnterKey(boolean enterKey) {
            this.enterKey = enterKey;
            return this;
        }

        public TestStep build(){
            return new TestStep(this);
        }
    }

    public Region getRegion() {
        return region;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getSimilarity() {
        return similarity;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public TestAction getAction() {
        return action;
    }

    public String getOutputText() {
        return outputText;
    }

    public boolean isEnterKey() {
        return enterKey;
    }

    public String getErrorImagePath() {
        return errorImagePath;
    }

    public String getRetryImagePath() {
        return retryImagePath;
    }

}
