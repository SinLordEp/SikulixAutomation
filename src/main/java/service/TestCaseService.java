package service;

import data.TestCaseDAO;
import exception.FileIOException;
import exception.OperationCancelException;
import gui.TestStepGUI;
import model.*;
import util.Callback;

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

    public void loadConfig(){
        dao.saveOnDataChanged();
        String path = dao.getPath(JSON_EXTENSION);
        if(path != null){
            if (!path.toLowerCase().endsWith(JSON_EXTENSION)) {
                path += JSON_EXTENSION;
            }
            dao.loadConfig(path);
        }else{
            throw new OperationCancelException();
        }
        if(!dao.getCategories().isEmpty()){
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }


    public boolean saveConfig(){
        String path = dao.getConfigPath();
        if(path == null || path.isEmpty()){
            path = dao.getPath(JSON_EXTENSION);
            if (!path.toLowerCase().endsWith(JSON_EXTENSION)) {
                path += JSON_EXTENSION;
            }
        }
        return dao.saveConfig(path);
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

    public void addTestStep(String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            TestStep testStep = new TestStep();
            new TestStepGUI(dao.getCategories().get(category).get(caseIndex).getName(), testStep, newTestStep -> {
                dao.addTestStep(category, caseIndex, newTestStep);
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
            });
        }
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            dao.deleteTestStep(category, caseIndex, stepIndex);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            new TestStepGUI(dao.getCategories().get(category).get(caseIndex).getName(), dao.getTestStep(category, caseIndex, stepIndex), newTestStep -> dao.modifyTestStep(category, caseIndex, stepIndex, newTestStep));
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategories()));
        }
    }

    public void setTestResults(LinkedHashMap<TestCase, CaseState> testResults) {
        dao.setTestResults(testResults);
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

    public void saveDataOnChanged(){
        dao.saveOnDataChanged();
    }
}
