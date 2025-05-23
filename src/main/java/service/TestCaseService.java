package service;

import config.GlobalPaths;
import data.dao.TestCaseDAO;
import exception.FileIOException;
import exception.OperationCancelException;
import gui.TestStepGUI;
import model.*;
import model.enums.CaseState;
import model.enums.EventCommand;
import interfaces.Callback;
import util.CollectionUtils;
import util.DialogUtils;
import util.FileUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static config.GlobalExtensions.CSV_EXTENSION;
import static config.GlobalExtensions.JSON_EXTENSION;

public class TestCaseService {
    private final TestCaseDAO dao = new TestCaseDAO();
    private final Callback<EventPackage> callback;

    public TestCaseService(Callback<EventPackage> callback) {
        this.callback = callback;
    }

    public void loadConfig() {
        try {
            saveDataOnChanged();
            String path = FileUtils.getPath(JSON_EXTENSION);
            if (!path.toLowerCase().endsWith(JSON_EXTENSION)) {
                path += JSON_EXTENSION;
            }
            dao.loadConfig(path);
            dao.setConfigPath(path);
            if(!dao.getCategoryCopy().isEmpty()){
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            }
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    public boolean saveConfig() {
        try {
            String path = dao.getConfigPath();
            if(path == null || path.isEmpty()){
                path = FileUtils.getPath(JSON_EXTENSION);
                if (!path.toLowerCase().endsWith(JSON_EXTENSION)) {
                    path += JSON_EXTENSION;
                }
            }
            return dao.saveConfig(path);
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    public void addCategory(String name){
        if(name != null && !name.isEmpty()){
            dao.addCategory(name);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }else{
            throw new OperationCancelException();
        }
    }

    public void deleteCategory(String category){
        if(category != null && !category.isEmpty()){
            dao.deleteCategory(category);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }
    }

    public void addTestCase(String category, String name){
        if(name != null && !name.isEmpty()){
            dao.addTestCase(category, new TestCase(name));
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }
    }

    public void deleteTestCase(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            dao.deleteTestCase(category, caseIndex);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }
    }

    public void modifyTestCase(String category, int caseIndex, String name){
        TestCase testCase = dao.getTestCase(category, caseIndex);
        try {
            FileUtils.renameFolder(GlobalPaths.IMAGE_ROOT.resolve(testCase.getName()), GlobalPaths.IMAGE_ROOT.resolve(name));
        } catch (IOException e) {
            throw new FileIOException("Failed to rename folder with cause: " + e.getMessage());
        }
        testCase.setName(name);
    }

    public void modifyTestCaseOrder(String category, int oldIndex, int newIndex){
        ArrayList<TestCase> testCases = dao.getCategories().get(category);
        CollectionUtils.moveElementInList(testCases, oldIndex, newIndex);
        dao.setDataIsChanged();
        callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
    }

    public void addTestStep(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = new TestStep("Step " + (testCase.getSteps().size()+1));
            new TestStepGUI(testCase.getName(), testStep, newTestStep -> {
                testCase.addStep(newTestStep);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            });
        }
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            testCase.getSteps().remove(stepIndex);
            dao.setDataIsChanged();
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = testCase.getSteps().get(stepIndex);
            new TestStepGUI(testCase.getName(), testStep, newTestStep -> {
                testCase.getSteps().set(stepIndex, newTestStep);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            });
        }
    }

    public void modifyTestStepOrder(String category, int caseIndex, int oldStepIndex, int newStepIndex){
        List<TestStep> testSteps = dao.getTestCase(category,caseIndex).getSteps();
        CollectionUtils.moveElementInList(testSteps, oldStepIndex, newStepIndex);
        dao.setDataIsChanged();
        callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
    }

    public void buildTestPlan(HashMap<String, ArrayList<TestCase>> testCases){
        LinkedHashMap<TestCase, CaseState> testPlan = new LinkedHashMap<>();
        int[] totalStep = new int[1];
        testCases.forEach((_, cases) -> cases.forEach(testCase -> {
            if(testCase.isSelected()){
                totalStep[0] += testCase.getSteps().size();
                testCase.resetCurrentStep();
                testPlan.put(testCase, CaseState.QUEUED);
            }
        }));
        dao.initializeTestResults(testPlan);
        callback.onSubmit(new EventPackage(EventCommand.PLAN_BUILT, totalStep[0]));
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public LinkedHashMap<TestCase, CaseState> getTestPlan(){
        return dao.getTestResults();
    }

    public void updateTestResult(TestCase testCase, CaseState caseState){
        dao.updateTestResult(testCase, caseState);
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public void updateTestResult(){
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public void generateResult(){
        String path = FileUtils.getPath(CSV_EXTENSION);
        if (!path.toLowerCase().endsWith(CSV_EXTENSION)) {
            path += CSV_EXTENSION;
        }
        try {
            dao.generateTestResult(path);
        } catch (IOException e) {
            throw new FileIOException("Could not generate test result to target file with cause: " + e.getMessage());
        }
    }

    public void saveDataOnChanged() {
        if(dao.isDataChanged()){
            switch(DialogUtils.showConfirmDialog(null, "Warning", "TestCase has changed, do you want to save config before proceed?")){
                case JOptionPane.YES_OPTION: saveConfig();
                    break;
                case JOptionPane.NO_OPTION: break;
                default: throw new OperationCancelException();
            }
        }
    }
}
