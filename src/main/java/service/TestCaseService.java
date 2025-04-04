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
import util.FileUtils;

import java.io.IOException;
import java.util.LinkedHashMap;

public class TestCaseService {
    private final TestCaseDAO dao = new TestCaseDAO();
    private final Callback<EventPackage> callback;
    private static final String JSON_EXTENSION = ".json";
    private static final String CSV_EXTENSION = ".csv";

    public TestCaseService(Callback<EventPackage> callback) {
        this.callback = callback;
    }

    public void loadConfig() {
        try {
            dao.saveOnDataChanged();
            String path = dao.getPath(JSON_EXTENSION);
            if(path != null){
                if (!path.toLowerCase().endsWith(JSON_EXTENSION)) {
                    path += JSON_EXTENSION;
                }
                dao.loadConfig(path);
                dao.setConfigPath(path);
            }else{
                throw new OperationCancelException();
            }
            if(!dao.getCategories().isEmpty()){
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
            }
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }


    public boolean saveConfig() {
        try {
            String path = dao.getConfigPath();
            if(path == null || path.isEmpty()){
                path = dao.getPath(JSON_EXTENSION);
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
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }else{
            throw new OperationCancelException();
        }
    }

    public void deleteCategory(String category){
        if(category != null && !category.isEmpty()){
            dao.deleteCategory(category);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }

    public void addTestCase(String category, String name){
        if(name != null && !name.isEmpty()){
            dao.addTestCase(category, new TestCase(name));
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }

    public void deleteTestCase(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            dao.deleteTestCase(category, caseIndex);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
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

    public void addTestStep(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = new TestStep();
            new TestStepGUI(testCase.getName(), testStep, newTestStep -> {
                testCase.addStep(newTestStep);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
            });
        }
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            testCase.getSteps().remove(stepIndex);
            dao.setDataIsChanged();
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = testCase.getSteps().get(stepIndex);
            new TestStepGUI(testCase.getName(), testStep, newTestStep -> {
                testCase.getSteps().set(stepIndex, newTestStep);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
            });
        }
    }

    public void initializeTestResults(LinkedHashMap<TestCase, CaseState> testResults) {
        dao.initializeTestResults(testResults);
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public void updateTestResult(TestCase testCase, CaseState caseState){
        dao.updateTestResult(testCase, caseState);
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public void updateTestResult(){
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestResults()));
    }

    public void generateResult(){
        String path = dao.getPath(CSV_EXTENSION);
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
        try {
            dao.saveOnDataChanged();
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }
}
