package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EventPackage {
    private final EventCommand command;
    private HashMap<String, ArrayList<TestCase>> testCases;
    private LinkedHashMap<String, CaseState> caseStates;

    public EventPackage(EventCommand command, HashMap<String, ArrayList<TestCase>> testCases) {
        this.command = command;
        this.testCases = testCases;
    }

    public EventPackage(EventCommand command, LinkedHashMap<String, CaseState> caseStates) {
        this.command = command;
        this.caseStates = caseStates;
    }

    public EventCommand getCommand() {
        return command;
    }

    public HashMap<String, ArrayList<TestCase>> getTestCases() {
        return testCases;
    }

    public LinkedHashMap<String, CaseState> getCaseStates() {
        return caseStates;
    }
}
