package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.OperationCancelException;
import model.TestCase;
import model.TestStep;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Sin
 */
public class TestCaseDAO {
    private String configPath;
    private HashMap<String, ArrayList<TestCase>> categories = new HashMap<>();

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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("New config" + extension));
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(extension.toLowerCase())) {
                filePath += extension;
            }
            return filePath;
        }else {
            throw new OperationCancelException();
        }
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
}
