package service;


import exception.OperationCancelException;
import exception.TestStepFailedException;
import model.*;
import model.enums.CaseState;
import model.enums.EventCommand;
import model.enums.StepState;
import interfaces.Callback;
import util.SikulixUtils;


import java.util.LinkedHashMap;

public class CaseExecuteService {
    private final TestCaseService testCaseService;
    private final WindowService windowService;
    private final StepExecuteService stepExecuteService;
    private Thread testThread = null;
    private final Callback<EventPackage> callback;

    public CaseExecuteService(TestCaseService testCaseService, Callback<EventPackage> callback) {
        this.testCaseService = testCaseService;
        this.windowService = new WindowService();
        this.stepExecuteService = new StepExecuteService();
        this.callback = callback;
    }

    public void startTest(LinkedHashMap<TestCase, CaseState> currentTestPlan){
        testCaseService.initializeTestResults(currentTestPlan);
        buildThread(currentTestPlan);
        windowService.captureWindow(this::startThread);
    }

    private void startThread(EventPackage eventPackage){
        if(eventPackage.getCommand() == EventCommand.WINDOW_CAPTURED){
            testThread.start();
        }
    }

    public void stopTest(){
        testThread.interrupt();
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }

    private void buildThread(LinkedHashMap<TestCase, CaseState> currentTestPlan){
        testThread = new Thread(() -> {
            currentTestPlan.forEach((testCase, _) -> {
                try {
                    Thread.sleep(500);
                    SikulixUtils.setImagePath(testCase.getName());
                    testCase.getSteps().forEach(testStep ->
                    {
                        testCase.nextCurrentStep();
                        testCaseService.updateTestResult(testCase, CaseState.ONGOING);
                        StepState stepState = stepExecuteService.execute(testStep);
                        if (stepState == StepState.FAIL) {
                            throw new TestStepFailedException("Defined error detected");
                        }
                        if (stepState == StepState.NO_MATCH) {
                            throw new TestStepFailedException("Expected result is not detected");
                        }
                    });
                    testCaseService.updateTestResult(testCase, CaseState.PASS);
                } catch (TestStepFailedException e) {
                    System.err.printf("Test case %s has failed with cause: %s%n", testCase.getName(), e.getMessage());
                    testCaseService.updateTestResult(testCase, CaseState.FAIL);
                } catch (InterruptedException e) {
                    testCaseService.updateTestResult(testCase, CaseState.INTERRUPT);
                    throw new OperationCancelException();
                }
                testCaseService.updateTestResult();
            });
            finishTest();
        });
    }

    private void finishTest(){
        windowService.unsetWindowAlwaysOnTop();
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }
}
