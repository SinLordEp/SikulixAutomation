package data.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.FileIOException;
import model.enums.CaseState;
import model.TestCase;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Sin
 */
public class TestCaseDAO {
    private String configPath;
    private JsonNode jsonNode = null;
    private HashMap<String, ArrayList<TestCase>> categories = new HashMap<>();
    private List<String> testResults;
    private List<TestCase> testPlan;
    private boolean dataChanged = false;

    public TestCaseDAO() {
        // No parameter needed now
    }

    public static HashMap<String, ArrayList<TestCase>> jsonToTestCaseCategory(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Paths.get(path).toFile(), new TypeReference<>() {});
    }

    public void testCaseCategoryToJson(String path, HashMap<String, ArrayList<TestCase>> category) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), category);
        dataChanged = false;
    }

    public void loadConfig(String path) throws IOException {
        categories = jsonToTestCaseCategory(path);
    }

    public void saveConfig(String path) throws IOException {
        this.configPath = path;
        testCaseCategoryToJson(path, categories);
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

    public HashMap<String, ArrayList<TestCase>> getCategories() {
        return categories;
    }

    public HashMap<String, ArrayList<TestCase>> getCategoryCopy() {
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

    public List<TestCase> getTestPlan() {
        return new ArrayList<>(testPlan);
    }

    public void initializeTestPlan(List<TestCase> testPlan){
        this.testPlan = new ArrayList<>(testPlan);
    }

    public void updateTestPlan(int index, TestCase testCase) {
        testPlan.set(index, testCase);
    }

    public void initializeTestResults() {
        testResults = new ArrayList<>();
        testResults.add("Test Case;Case State;Failed Step;Failure Description;Json Param");
    }

    public void updateTestResult(TestCase testCase, String jsonParam){
        StringBuilder sb = new StringBuilder();
        sb.append(testCase).append(";").append(testCase.getState()).append(";");
        if(testCase.getState() == CaseState.FAIL){
            sb.append(testCase.getCurrentTestStep()).append(";").append(testCase.getCurrentTestStep().getDescription()).append(";").append(jsonParam);
        }else{
            sb.append("NONE").append(";").append("NONE").append(";").append(jsonParam);
        }
        testResults.add(sb.toString());
    }

    public void generateTestResult(String path) throws IOException {
        Files.write(Paths.get(path), testResults);
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public void setDataIsChanged() {
        this.dataChanged = true;
    }

    public void setupJsonNode(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.jsonNode = mapper.readTree(Paths.get(path).toFile());
    }

    public JsonNode getJsonByCaseAndIndex(String caseName, int iterateIndex){
        if(jsonNode.has(caseName)){
            JsonNode testCaseNode = jsonNode.get(caseName);
            return testCaseNode.get(iterateIndex);
        }else{
            throw new FileIOException("No json param for case: %s".formatted(caseName));
        }
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }
}
