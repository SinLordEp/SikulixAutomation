package model;

import model.enums.EventCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EventPackage {
    private final EventCommand command;
    private HashMap<String, ArrayList<TestCase>> testCases;
    private List<TestCase> testPlan;
    private int totalSteps;

    public EventPackage(EventCommand command, HashMap<String, ArrayList<TestCase>> testCases) {
        this.command = command;
        this.testCases = testCases;
    }

    public EventPackage(EventCommand command, List<TestCase> testPlan) {
        this.command = command;
        this.testPlan = testPlan;
    }

    public EventPackage(EventCommand command, int totalSteps) {
        this.command = command;
        this.totalSteps = totalSteps;
    }

    public EventPackage(EventCommand command) {
        this.command = command;
    }

    public EventCommand getCommand() {
        return command;
    }

    public HashMap<String, ArrayList<TestCase>> getTestCases() {
        return testCases;
    }

    public List<TestCase> getTestPlan() {
        return testPlan;
    }

    public int getTotalSteps() {
        return totalSteps;
    }
}
