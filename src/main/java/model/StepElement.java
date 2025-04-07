package model;

import model.enums.DataSource;
import model.enums.StepAction;

/**
 * @author Sin
 */
public class StepElement {
    private DataSource dataSource = DataSource.IMAGE;
    private StepAction action = StepAction.FIND;
    private String path;
    private double similarity = 0.9;
    private int timeoutSec = 2;
    private DataSource textDataSource = null;
    private String outputText = null;
    private boolean enterKey = false;


    public StepElement() {
        //No parameter needed for now
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public StepAction getAction() {
        return action;
    }

    public void setAction(StepAction action) {
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public DataSource getTextDataSource() {
        return textDataSource;
    }

    public void setTextDataSource(DataSource textDataSource) {
        this.textDataSource = textDataSource;
    }
}
