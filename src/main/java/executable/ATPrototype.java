package executable;

import com.sun.jna.platform.win32.WinDef;
import dao.TestCaseParser;
import exceptions.TestStepFailedException;
import model.*;
import org.sikuli.script.Region;
import utils.JNAUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Sin
 */
public class ATPrototype {
    static int width = 1400;
    static int height = 1000;
    static Region region = new Region(1,1, width-1, height-1);
    private static HashMap<String, ArrayList<TestCase>> category = new HashMap<>();
    public static void main(String[] args) {
        //new ToolGUI().run();
        //new TestStepGUI(region).run();
        //categoryToJsonFile();
        jsonFileToCategory();
        runTest();
    }

    public static void demoRun(){
        captureWindow();
    }

    public static void categoryToJsonFile(){
        ArrayList<TestCase> testCases = new ArrayList<>();
        testCases.add(testCaseOne());
        category.put("Ventas", testCases);
        TestCaseParser.testCaseCategoryToJson(category);
    }

    public static void jsonFileToCategory(){
        category = TestCaseParser.jsonToTestCaseCategory("TestCases.json");
    }

    public static void captureWindow(){
        WinDef.HWND window = JNAUtils.getWindowByTitle("Servidor Tienda [Tienda PRE ] CastorTPV v@VERSION@");
        JNAUtils.setWindowSize(window, width,height);
        JNAUtils.setWindowAtLocation(window, 0, 0);
    }

    public static void runTest(){
        for(TestCase testCase : category.get("Ventas")){
            try{
                testCase.getSteps().forEach(testStep ->
                {
                    StepState state = StepExecutor.execute(testStep);
                    if (state == StepState.FAIL){
                        throw new TestStepFailedException("Defined error detected");
                    }
                    if (state == StepState.NO_MATCH) {
                        throw new TestStepFailedException("Expected result is not detected");
                    }
                });
            }catch (TestStepFailedException e){
                System.err.printf("Test case %s has failed with cause: %s%n", testCase.getName(), e.getMessage());
            }
        }
    }

    public static TestCase testCaseOne(){
        TestCase testCase = new TestCase("ventas_001");

        TestStep step1 = new TestStep();
        StepElement element = new StepElement();
        element.setRegion(region);
        element.setPath("Ventas_button.png");
        element.setAction(StepAction.CLICK);
        step1.setStepElement(StepElementType.PASS, element);
        testCase.addStep(step1);

        TestStep step2 = new TestStep();
        StepElement element2 = new StepElement();
        element2.setRegion(region);
        element2.setPath("Ventas_panel.png");
        element2.setAction(StepAction.FIND);
        step2.setStepElement(StepElementType.PASS, element2);
        testCase.addStep(step2);

        TestStep step3 = new TestStep();
        StepElement element3 = new StepElement();
        element3.setRegion(region);
        element3.setPath("Buscar_button.png");
        element3.setAction(StepAction.CLICK);
        step3.setStepElement(StepElementType.PASS, element3);
        testCase.addStep(step3);

        TestStep step4 = new TestStep();
        StepElement element4 = new StepElement();
        element4.setRegion(region);
        element4.setPath("Busqueda_panel.png");
        element4.setAction(StepAction.FIND);
        step4.setStepElement(StepElementType.PASS, element4);
        testCase.addStep(step4);

        TestStep step5 = new TestStep();
        StepElement element5 = new StepElement();
        element5.setRegion(region);
        element5.setPath("Busqueda_buscar_button.png");
        element5.setAction(StepAction.CLICK);
        step5.setStepElement(StepElementType.PASS, element5);
        testCase.addStep(step5);

        TestStep step6 = new TestStep();
        StepElement element6 = new StepElement();
        element6.setRegion(region);
        element6.setPath("Busqueda_resultado_uno.png");
        element6.setAction(StepAction.FIND);
        step6.setStepElement(StepElementType.PASS, element6);
        testCase.addStep(step6);

        TestStep step7 = new TestStep();
        StepElement element7 = new StepElement();
        element7.setRegion(region);
        element7.setPath("Busqueda_nombreContiene_input.PNG");
        element7.setAction(StepAction.TYPE);
        element7.setDataSource(DataSource.INPUT_TEXT);
        element7.setOutputText("aaaaaaa");
        element7.setEnterKey(true);
        step7.setStepElement(StepElementType.PASS, element7);
        testCase.addStep(step7);

        TestStep step8 = new TestStep();
        StepElement element8 = new StepElement();
        element8.setRegion(region);
        element8.setPath("Busqueda_resultado_uno.png");
        element8.setAction(StepAction.FIND);
        step8.setStepElement(StepElementType.PASS, element8);
        StepElement element9 = new StepElement();
        element9.setRegion(region);
        element9.setPath("Busqueda_resultado_vacio.png");
        element9.setAction(StepAction.FIND);
        step8.setStepElement(StepElementType.FAIL, element9);
        testCase.addStep(step8);

        return testCase;
    }

}
