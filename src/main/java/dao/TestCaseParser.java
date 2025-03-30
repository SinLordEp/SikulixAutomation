package dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.TestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Sin
 */
public class TestCaseParser {

    private TestCaseParser() {
    }

    public static HashMap<String, ArrayList<TestCase>> jsonToTestCaseCategory(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(Paths.get("TestCases.json").toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testCaseCategoryToJson(HashMap<String, ArrayList<TestCase>> category) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("TestCases.json").toFile(), category);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
