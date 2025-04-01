package data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CaseState;
import model.TestCase;
import model.TestStep;

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
    private LinkedHashMap<String, CaseState> testResults;

    public TestCaseDAO() {
    }

    public static HashMap<String, ArrayList<TestCase>> jsonToTestCaseCategory(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Paths.get("TestCases.json").toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testCaseCategoryToJson(HashMap<String, ArrayList<TestCase>> category) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("TestCases.json").toFile(), category);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig(String path){
        categories = TestCaseDAO.jsonToTestCaseCategory("TestCases.json");
        configPath = path;
    }

    public void saveConfig(String path){
        this.configPath = path;
        testCaseCategoryToJson(categories);
    }

    public void addCategory(String category){
        categories.put(category, new ArrayList<>());
    }

    public void deleteCategory(String category){
        categories.remove(category);
    }

    public void addTestCase(String category, TestCase testCase){
        categories.get(category).add(testCase);
    }

    public void deleteTestCase(String category, int caseIndex){
        categories.get(category).remove(caseIndex);
    }

    public void addTestStep(String category, int caseIndex, TestStep testStep){
        categories.get(category).get(caseIndex).addStep(testStep);
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        categories.get(category).get(caseIndex).getSteps().remove(stepIndex);
    }

    public TestStep getTestStep(String category, int caseIndex, int stepIndex){
        return categories.get(category).get(caseIndex).getSteps().get(stepIndex);
    }

    public void modifyTessStep(String category, int caseIndex, int stepIndex, TestStep testStep){
        categories.get(category).get(caseIndex).getSteps().set(stepIndex, testStep);
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

    public LinkedHashMap<String, CaseState> getTestResults() {
        return testResults;
    }

    public void initializeTestResults(LinkedHashMap<String, TestCase> testCases) {
        testResults = new LinkedHashMap<>();
        testCases.forEach((_, testCase) -> testResults.put(testCase.getName(), CaseState.QUEUED));
    }

    public void updateTestResult(String caseName, CaseState caseState){
        testResults.put(caseName, caseState);
    }

    public void generateTestResult(String path) throws IOException {
        List<String> output = new ArrayList<>();
        testResults.forEach((caseName,caseState) -> {
            String line = caseName + ";" + caseState.toString();
            output.add(line);
        });
        Files.write(Paths.get(path), output);
    }
}
