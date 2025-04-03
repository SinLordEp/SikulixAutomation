package data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.FileIOException;
import exception.OperationCancelException;
import model.CaseState;
import model.TestCase;
import model.TestStep;
import util.DialogUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Sin
 */
public class TestCaseDAO {
    private String configPath;
    private HashMap<String, ArrayList<TestCase>> categories = new HashMap<>();
    private LinkedHashMap<TestCase, CaseState> testResults;
    private boolean dataChanged = false;

    public TestCaseDAO() {
        // No parameter needed now
    }

    public static HashMap<String, ArrayList<TestCase>> jsonToTestCaseCategory(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Paths.get(path).toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new FileIOException("Could not read json file - Path: " + path);
        }
    }

    public boolean testCaseCategoryToJson(String path, HashMap<String, ArrayList<TestCase>> category) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), category);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void loadConfig(String path){
        categories = jsonToTestCaseCategory(path);
    }

    public boolean saveConfig(String path){
        this.configPath = path;
        return testCaseCategoryToJson(path, categories);
    }

    public void addCategory(String category){
        categories.put(category, new ArrayList<>());
        dataChanged = true;
    }

    public void deleteCategory(String category){
        categories.remove(category);
        dataChanged = true;
    }

    public void addTestCase(String category, TestCase testCase){
        categories.get(category).add(testCase);
        dataChanged = true;
    }

    public void deleteTestCase(String category, int caseIndex){
        categories.get(category).remove(caseIndex);
        dataChanged = true;
    }

    public void addTestStep(String category, int caseIndex, TestStep testStep){
        categories.get(category).get(caseIndex).addStep(testStep);
        dataChanged = true;
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        categories.get(category).get(caseIndex).getSteps().remove(stepIndex);
        dataChanged = true;
    }

    public TestStep getTestStep(String category, int caseIndex, int stepIndex){
        return categories.get(category).get(caseIndex).getSteps().get(stepIndex);
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex, TestStep testStep){
        categories.get(category).get(caseIndex).getSteps().set(stepIndex, testStep);
        dataChanged = true;
    }

    public String getPath(String extension) {
        JFileChooser fileChooser = new JFileChooser(new File("src").getAbsolutePath());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("New config" + extension));
        int result = fileChooser.showSaveDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile().getAbsolutePath() : null;
    }

    public HashMap<String, ArrayList<TestCase>> getCategories() {
        return categories;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public LinkedHashMap<TestCase, CaseState> getTestResults() {
        return testResults;
    }

    public void setTestResults(LinkedHashMap<TestCase, CaseState> testResults) {
        this.testResults = testResults;
    }

    public void updateTestResult(TestCase testCase, CaseState caseState){
        testResults.put(testCase, caseState);
    }

    public void generateTestResult(String path) throws IOException {
        List<String> output = new ArrayList<>();
        testResults.forEach((caseName,caseState) -> {
            String line = caseName + ";" + caseState.toString();
            output.add(line);
        });
        Files.write(Paths.get(path), output);
    }

    public void saveOnDataChanged(){
        if(dataChanged){
            switch(DialogUtils.showConfirmDialog(null,"TestCase has changes, do you want to save config before proceed?", "Warning")){
                case JOptionPane.YES_OPTION: saveConfig(configPath);
                    break;
                case JOptionPane.NO_OPTION: break;
                default: throw new OperationCancelException();
            }
        }
    }


}
