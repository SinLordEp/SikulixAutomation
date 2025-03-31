package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EventPackage {
    private final EventCommand command;
    private HashMap<String, ArrayList<TestCase>> testCases;
    private LinkedHashMap<String, CaseState> testResults;

    public EventPackage(EventCommand command, HashMap<String, ArrayList<TestCase>> testCases) {
        this.command = command;
        this.testCases = testCases;
    }

    public EventPackage(EventCommand command, LinkedHashMap<String, CaseState> testResults) {
        this.command = command;
        this.testResults = testResults;
    }

    public EventCommand getCommand() {
        return command;
    }

    public HashMap<String, ArrayList<TestCase>> getTestCases() {
        return testCases;
    }

    public LinkedHashMap<String, CaseState> getTestResults() {
        return testResults;
    }
}
