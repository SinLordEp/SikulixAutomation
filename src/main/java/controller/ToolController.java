package controller;

import dao.TestCaseDAO;
import exceptions.OperationCancelException;
import exceptions.TestStepFailedException;
import gui.TestStepGUI;
import gui.ToolGUI;
import model.*;
import utils.EventListener;
import utils.SikulixUtils;
import utils.StepExecutor;

import javax.swing.*;
import java.util.*;

/**
 * @author Sin
 */
public class ToolController {
    private TestCaseDAO dao;
    private final List<EventListener<EventPackage>> listeners = new ArrayList<>();
    public ToolController() {
        initialize();
    }

    private void initialize(){
        dao = new TestCaseDAO();

    }

    public void run(){
        ToolGUI toolGUI = new ToolGUI(this);
        addListener(toolGUI);
        toolGUI.run();
    }

    public void loadConfig(){
        String path = dao.getPath(".json");
        if(path != null){
            dao.loadConfig(path);
        }else{
            throw new OperationCancelException();
        }
        if(!dao.getCategories().isEmpty()){
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void saveConfig(){
        String path = dao.getConfigPath();
        if(path == null || path.isEmpty()){
            path = dao.getPath(".json");
        }
        dao.saveConfig(path);
    }

    public void addCategory(){
        String name = JOptionPane.showInputDialog("Input name of the new category name:");
        if(name != null && !name.isEmpty()){
            dao.addCategory(name);
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }else{
            throw new OperationCancelException();
        }
    }

    public void deleteCategory(String category){
        if(category != null && !category.isEmpty()){
            dao.deleteCategory(category);
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void addTestCase(String category){
        String name = JOptionPane.showInputDialog("Input name of the new test case:");
        if(name != null && !name.isEmpty()){
            dao.addTestCase(category, new TestCase(name));
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void deleteTestCase(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            dao.deleteTestCase(category, caseIndex);
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void addTestStep(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            TestStep testStep = new TestStep();
            new TestStepGUI(testStep, newTestStep -> dao.addTestStep(category, caseIndex, newTestStep));
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            dao.deleteTestStep(category, caseIndex, stepIndex);
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            new TestStepGUI(dao.getTestStep(category, caseIndex, stepIndex), newTestStep -> dao.modifyTessStep(category, caseIndex, stepIndex, newTestStep));
            notifyEvent(new EventPackage(EventCommand.CONFIG_CHANGED, dao.getCategories()));
        }
    }

    public void startTest(LinkedHashMap<String, TestCase> testCases){
        LinkedHashMap<String, CaseState> testResults = new LinkedHashMap<>();
        testCases.forEach((_, testCase) -> testResults.put(testCase.getName(), CaseState.QUEUED));
        notifyEvent(new EventPackage(EventCommand.PROCESS_CHANGED, testResults));
        testCases.forEach((caseName, testCase) -> {
            CaseState state = CaseState.QUEUED;
            try{
                SikulixUtils.setImagePath(testCase.getName());
                testCase.getSteps().forEach(testStep ->
                {
                    StepState stepState = StepExecutor.execute(testStep);
                    if (stepState == StepState.FAIL){
                        throw new TestStepFailedException("Defined error detected");
                    }
                    if (stepState == StepState.NO_MATCH) {
                        throw new TestStepFailedException("Expected result is not detected");
                    }
                });
            }catch (TestStepFailedException e){
                System.err.printf("Test case %s has failed with cause: %s%n", testCase.getName(), e.getMessage());
            }
        });
    }

    public String pathChooser(){
        String path = dao.getPath(".json");
        if(path != null){
            return path;
        }else{
            throw new OperationCancelException();
        }
    }

    public void addListener(EventListener<EventPackage> listener){
        listeners.add(listener);
    }

    private void notifyEvent(EventPackage eventPackage){
        listeners.forEach(listener -> listener.onEvent(eventPackage));
    }


}
