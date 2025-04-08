package model;

import model.enums.CaseState;
import model.enums.EventCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EventPackage {
    private final EventCommand command;
    private HashMap<String, ArrayList<TestCase>> testCases;
    private LinkedHashMap<TestCase, CaseState> testResults;
    private int totalSteps;

    public EventPackage(EventCommand command, HashMap<String, ArrayList<TestCase>> testCases) {
        this.command = command;
        this.testCases = testCases;
    }

    public EventPackage(EventCommand command, LinkedHashMap<TestCase, CaseState> testResults) {
        this.command = command;
        this.testResults = testResults;
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

    public LinkedHashMap<TestCase, CaseState> getTestResults() {
        return testResults;
    }

    public int getTotalSteps() {
        return totalSteps;
    }
}
