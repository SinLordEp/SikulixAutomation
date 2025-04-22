package service;

import com.fasterxml.jackson.databind.JsonNode;
import config.GlobalPaths;
import data.dao.TestCaseDAO;
import exception.ConfigMissingException;
import exception.FileIOException;
import exception.OperationCancelException;
import exception.UndefinedException;
import gui.TestStepGUI;
import model.*;
import model.enums.EventCommand;
import interfaces.Callback;
import model.enums.FileOperation;
import util.CollectionUtils;
import util.DialogUtils;
import util.FileUtils;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.enums.FileExtension;

public class TestCaseService {
    private final TestCaseDAO dao = new TestCaseDAO();
    private final Callback<EventPackage> callback;

    public TestCaseService(Callback<EventPackage> callback) {
        this.callback = callback;
    }

    public void loadConfig(JFrame parent) {
        try {
            saveDataOnChanged(parent);
            String path = FileUtils.getPath(parent, FileOperation.OPEN, FileExtension.JSON);
            if (!path.toLowerCase().endsWith(FileExtension.JSON.getExtension())) {
                path += FileExtension.JSON.getExtension();
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

    public void saveConfig(JFrame parent) {
        try {
            String path = dao.getConfigPath();
            if(path == null || path.isEmpty()){
                path = FileUtils.getPath(parent, FileOperation.SAVE, FileExtension.JSON);
                if (!path.toLowerCase().endsWith(FileExtension.JSON.getExtension())) {
                    path += FileExtension.JSON.getExtension();
                }
            }
            dao.saveConfig(path);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_SAVED));
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    public void addCategory(JFrame parent){
        String input = DialogUtils.showInputDialog(parent, "Add category", "Input the name of the new category:");
        if(!input.isEmpty()){
            dao.addCategory(input);
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }else{
            throw new OperationCancelException();
        }
    }

    public void deleteCategory(JFrame parent, String category){
        if(category != null && !category.isEmpty()){
            if(DialogUtils.showConfirmDialog(parent, "Deleting category", "Delete Category: \"%s\"?".formatted(category)) == JOptionPane.YES_OPTION){
                dao.deleteCategory(category);
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            }else {
                throw new OperationCancelException();
            }
        }
    }

    public void addTestCase(JFrame parent, String category){
        if(category == null || category.isEmpty()){
            return;
        }
        String input = DialogUtils.showInputDialog(parent, "Add test case", "Input the name of the new case:");
        if(!input.isEmpty()){
            dao.addTestCase(category, new TestCase(input));
            callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
        }
    }

    public void deleteTestCase(JFrame parent, String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            if(DialogUtils.showConfirmDialog(parent, "Deleting test case", "Delete TestCase: \"%s\"?".formatted(dao.getTestCase(category, caseIndex).getName()) ) == JOptionPane.YES_OPTION){
                dao.deleteTestCase(category, caseIndex);
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            }else{
                throw new OperationCancelException();
            }
        }
    }

    public void modifyTestCase(JFrame parent, String category, int caseIndex){
        TestCase testCase = dao.getTestCase(category, caseIndex);
        String input = DialogUtils.showInputDialog(parent, "Modify Test case", "Input new name: ");
        if(input.isEmpty()){
            throw new OperationCancelException();
        }
        if(!testCase.getSteps().isEmpty()){
            try {
                FileUtils.renameFolder(GlobalPaths.IMAGE_ROOT.resolve(testCase.getName()), GlobalPaths.IMAGE_ROOT.resolve(input));
            } catch (IOException e) {
                throw new FileIOException("Failed to rename folder with cause: " + e.getMessage());
            }
        }
        testCase.setName(input);
        dao.setDataIsChanged();
    }

    public void modifyTestCaseOrder(String category, int oldIndex, int newIndex){
        ArrayList<TestCase> testCases = dao.getCategories().get(category);
        CollectionUtils.moveElementInList(testCases, oldIndex, newIndex);
        dao.setDataIsChanged();
        callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
    }

    public void addTestStep(JFrame parent, String category, int caseIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = new TestStep("Step " + (testCase.getSteps().size()+1));
            new TestStepGUI(parent, testCase, testStep, newTestStep -> {
                testCase.addStep(newTestStep);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            });
        }
    }

    public void deleteTestStep(JFrame parent, String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            if(DialogUtils.showConfirmDialog(parent, "Deleting test step", "Delete TestStep: \"%s\"?".formatted(testCase.getSteps().get(stepIndex).getName()) ) == JOptionPane.YES_OPTION){
                testCase.getSteps().remove(stepIndex);
                dao.setDataIsChanged();
                callback.onSubmit(new EventPackage(EventCommand.TESTCASE_CHANGED, dao.getCategoryCopy()));
            }else{
                throw new OperationCancelException();
            }
        }
    }

    public void modifyTestStep(JFrame parent, String category, int caseIndex, int stepIndex){
        if(category != null && !category.isEmpty() && caseIndex >= 0 && stepIndex >= 0){
            TestCase testCase = dao.getTestCase(category, caseIndex);
            TestStep testStep = testCase.getSteps().get(stepIndex);
            new TestStepGUI(parent, testCase, testStep, newTestStep -> {
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

    public void loadJson(JFrame parent){
        try {
            String path = FileUtils.getPath(parent, FileOperation.OPEN, FileExtension.JSON);
            if (!path.toLowerCase().endsWith(FileExtension.JSON.getExtension())) {
                path += FileExtension.JSON.getExtension();
            }
            dao.setupJsonNode(path);
            callback.onSubmit(new EventPackage(EventCommand.JSON_LOADED));
        } catch (IOException e) {
            throw new FileIOException("Cannot read json file: " + e.getMessage());
        }
    }

    public JsonNode getJsonByCaseAndIndex(String caseName, int iterateIndex){
        return dao.getJsonByCaseAndIndex(caseName, iterateIndex);
    }

    public void buildTestPlan(HashMap<String, ArrayList<TestCase>> testCases){
        List<TestCase> testPlan = new ArrayList<>();
        int[] totalStep = new int[1];
        testCases.forEach((_, cases) -> cases.forEach(testCase -> {
            if(testCase.isSelected()){
                if(!testCase.getParams().isEmpty() && dao.getJsonNode() == null){
                    throw new ConfigMissingException("TestCase: " + testCase.getName() + " has parameters but no json file is loaded, please load json file first before building test plan");
                }
                totalStep[0] += testCase.getSteps().size();
                testCase.resetCurrentStep();
                testPlan.add(testCase.deepCopy());
            }
        }));
        dao.initializeTestPlan(testPlan);
        dao.initializeTestResults();
        callback.onSubmit(new EventPackage(EventCommand.PLAN_BUILT, totalStep[0]));
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestPlan()));
    }

    public List<TestCase> getTestPlan(){
        return dao.getTestPlan();
    }

    public void updateTestPlan(int index, TestCase testCase){
        dao.updateTestPlan(index, testCase);
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestPlan()));
    }

    public void updateTestResult(TestCase testCase, String jsonParam){
        dao.updateTestResult(testCase, jsonParam);
    }

    public void repaintTestResult(){
        callback.onSubmit(new EventPackage(EventCommand.RESULT_CHANGED, dao.getTestPlan()));
    }

    public void generateResult(JFrame parent){
        String path = FileUtils.getPath(parent, FileOperation.SAVE, FileExtension.CSV);
        if (!path.toLowerCase().endsWith(FileExtension.CSV.getExtension())) {
            path += FileExtension.CSV.getExtension();
        }
        try {
            dao.generateTestResult(path);
        } catch (IOException e) {
            throw new FileIOException("Could not generate test result to target file with cause: " + e.getMessage());
        }
    }

    public void saveDataOnChanged(JFrame parent) {
        if(dao.isDataChanged()){
            switch(DialogUtils.showCancelableConfirmDialog(parent, "Warning", "TestCase has changed, do you want to save config before proceed?")){
                case JOptionPane.YES_OPTION: saveConfig(parent);
                    break;
                case JOptionPane.NO_OPTION: break;
                case JOptionPane.CANCEL_OPTION: throw new OperationCancelException();
                default: throw new UndefinedException("Unexpected choice from user");
            }
        }
    }
}
