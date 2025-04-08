package data.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.GlobalPaths;
import model.enums.CaseState;
import model.TestCase;


import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public static HashMap<String, ArrayList<TestCase>> jsonToTestCaseCategory(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Paths.get(path).toFile(), new TypeReference<>() {});
    }

    public boolean testCaseCategoryToJson(String path, HashMap<String, ArrayList<TestCase>> category) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), category);
        dataChanged = false;
        return true;
    }

    public void loadConfig(String path) throws IOException {
        categories = jsonToTestCaseCategory(path);
    }

    public boolean saveConfig(String path) throws IOException {
        this.configPath = path;
        return testCaseCategoryToJson(path, categories);
    }

    public void addCategory(String category){
        categories.put(category, new ArrayList<>());
        setDataIsChanged();
    }

    public void deleteCategory(String category){
        categories.remove(category);
        setDataIsChanged();
    }

    public void addTestCase(String category, TestCase testCase){
        categories.get(category).add(testCase);
        setDataIsChanged();
    }

    public void deleteTestCase(String category, int caseIndex){
        categories.get(category).remove(caseIndex);
        setDataIsChanged();
    }

    public String getPath(String extension) {
        JFileChooser fileChooser = new JFileChooser(GlobalPaths.BASE_ROOT.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("New config" + extension));
        int result = fileChooser.showSaveDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile().getAbsolutePath() : null;
    }

    public HashMap<String, ArrayList<TestCase>> getCategories() {
        HashMap<String, ArrayList<TestCase>> result = new HashMap<>();
        categories.forEach((k, v) -> result.put(k, new ArrayList<>(v)));
        return result;
    }

    public TestCase getTestCase(String category, int caseIndex){
        return categories.get(category).get(caseIndex);
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public LinkedHashMap<TestCase, CaseState> getTestResults() {
        return new LinkedHashMap<>(testResults);
    }

    public void initializeTestResults(LinkedHashMap<TestCase, CaseState> testResults) {
        this.testResults = new LinkedHashMap<>(testResults);
    }

    public void updateTestResult(TestCase testCase, CaseState caseState){
        testResults.put(testCase, caseState);
    }

    public void generateTestResult(String path) throws IOException {
        List<String> output = new ArrayList<>();
        output.add("Test Case;Case State;Test Step;Step Description");
        testResults.forEach((testCase,caseState) -> {
            String line;
            if(caseState == CaseState.PASS){
                line = testCase + ";" + caseState + ";None;None";
            }else{
                line = testCase + ";" + caseState + ";" + testCase.getCurrentTestStep().getName() + ";" + testCase.getCurrentTestStep().getDescription();
            }
            output.add(line);
        });
        Files.write(Paths.get(path), output);
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public void setDataIsChanged() {
        this.dataChanged = true;
    }

}
