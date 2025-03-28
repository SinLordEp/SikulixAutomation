package demo;

import com.sun.jna.platform.win32.WinDef;
import exceptions.TestStepFailedException;
import model.TestCase;
import org.sikuli.script.*;
import utils.JNAUtils;
import utils.SikulixUtils;
import utils.TestState;

import java.util.ArrayList;

public class ATPrototype {
    static int width = 1400;
    static int height = 1000;
    static Region region = new Region(1,1, width-1, height-1);
    private static final ArrayList<TestCase> testCases = new ArrayList<>();
    public static void main(String[] args) {
        new TestStepGUI(region).run();
        //captureWindow();
        //testCases.add(testCaseOne());
        //runTest();
    }

    public static void captureWindow(){
        WinDef.HWND window = JNAUtils.getWindowByTitle("Servidor Tienda [Tienda PRE ] CastorTPV v@VERSION@");
        JNAUtils.setWindowSize(window, width,height);
        JNAUtils.setWindowAtLocation(window, 0, 0);
    }

    public static void runTest(){
        for(TestCase testCase : testCases){
            try{
                testCase.getSteps().forEach(testStep ->
                {
                    TestState state = SikulixUtils.handleTestStep(testStep);
                    if (state == TestState.FAIL){
                        throw new TestStepFailedException("Defined error detected");
                    }
                    if (state == TestState.NO_MATCH) {
                        throw new TestStepFailedException("Expected result is not detected");
                    }
                });
            }catch (TestStepFailedException e){
                System.err.printf("Test case %s has failed with cause: %s%n", testCase.getName(), e.getMessage());
            }
        }
    }

    public static TestCase testCaseOne(){
        TestCase testCase = new TestCase("ventas_001", region);
        testCase.addClickStep("Ventas_button.png", 2, null);
        testCase.addMatchStep("Ventas_panel.png", 2, null);
        testCase.addClickStep("Buscar_button.png", 2, null);
        testCase.addMatchStep("Busqueda_panel.png", 2, null);
        testCase.addClickStep("Busqueda_buscar_button.png", 2, null);
        testCase.addMatchStep("Busqueda_resultado_uno", 2, null);
        testCase.addTypeStep("Busqueda_nombreContiene_input.PNG", 2, "aaaaaaa", true, null);
        testCase.addMatchStep("Busqueda_resultado_uno.png", 2, "Busqueda_resultado_vacio.png");
        return testCase;
    }

    public static TestCase testCaseTwo(){
        TestCase testCase = new TestCase("", region);

        return testCase;
    }

}
