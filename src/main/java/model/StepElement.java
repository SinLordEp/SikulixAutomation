package model;

import model.enums.DataSource;
import model.enums.StepAction;

/**
 * @author Sin
 */
public class StepElement {
    private DataSource matchType = DataSource.IMAGE;
    private StepAction action = StepAction.FIND;
    private String imageNameOrText;
    private double similarity = 0.9;
    private int timeoutSec = 2;
    private DataSource textSource = null;
    private String outputText = null;
    private boolean enterKey = false;


    public StepElement() {
        //No parameter needed for now
    }

    public DataSource getMatchType() {
        return matchType;
    }

    public void setMatchType(DataSource matchType) {
        this.matchType = matchType;
    }

    public StepAction getAction() {
        return action;
    }

    public void setAction(StepAction action) {
        this.action = action;
    }

    public String getImageNameOrText() {
        return imageNameOrText;
    }

    public void setImageNameOrText(String imageNameOrText) {
        this.imageNameOrText = imageNameOrText;
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

    public DataSource getTextSource() {
        return textSource;
    }

    public void setTextSource(DataSource textSource) {
        this.textSource = textSource;
    }
}
