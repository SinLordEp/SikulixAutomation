package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.sikuli.script.Region;

/**
 * @author Sin
 */
public class StepElement {
    private DataSource dataSource = DataSource.IMAGE;
    private StepAction action = StepAction.FIND;
    private String path;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private double similarity = 0.9;
    private int timeoutSec = 2;
    private DataSource textDataSource = null;
    private String outputText = null;
    private boolean enterKey = false;


    public StepElement() {
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

    @JsonIgnore
    public void setRegion(Region region) {
        this.x = region.x;
        this.y = region.y;
        this.width = region.w;
        this.height = region.h;
    }

    @JsonIgnore
    public Region getRegion(){
        return new Region(x,y,width,height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public DataSource getTextDataSource() {
        return textDataSource;
    }

    public void setTextDataSource(DataSource textDataSource) {
        this.textDataSource = textDataSource;
    }
}
