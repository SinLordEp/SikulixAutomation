package service;


import exception.OperationCancelException;
import exception.TestStepFailedException;
import model.*;
import model.enums.CaseState;
import model.enums.EventCommand;
import model.enums.StepState;
import interfaces.Callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.SikulixUtils;


import java.util.LinkedHashMap;

public class CaseExecuteService {
    private final TestCaseService testCaseService;
    private final StepExecuteService stepExecuteService;
    private Thread testThread = null;
    private final Callback<EventPackage> callback;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public CaseExecuteService(TestCaseService testCaseService, Callback<EventPackage> callback) {
        this.testCaseService = testCaseService;
        this.stepExecuteService = new StepExecuteService();
        this.callback = callback;
    }

    public void startTest(){
        buildThread(testCaseService.getTestPlan());
        testThread.start();
    }

    public void stopTest(){
        testThread.interrupt();
        testCaseService.updateTestResult();
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }

    private void buildThread(LinkedHashMap<TestCase, CaseState> currentTestPlan){
        testThread = new Thread(() -> {
            currentTestPlan.forEach((testCase, _) -> {
                try {
                    logger.debug("Executing TestCase: {}", testCase);
                    Thread.sleep(500);
                    SikulixUtils.setImagePath(testCase.getName());
                    testCase.getSteps().forEach(testStep ->
                    {
                        testCase.nextCurrentStep();
                        testCaseService.updateTestResult(testCase, CaseState.ONGOING);
                        StepState stepState = stepExecuteService.execute(testStep);
                        if (stepState != StepState.PASS) {
                            throw new TestStepFailedException(testStep.toString());
                        }
                    });
                    testCaseService.updateTestResult(testCase, CaseState.PASS);
                } catch (TestStepFailedException e) {
                    logger.debug("TestCase: {} failed at TestStep: {}", testCase, e.getMessage());
                    testCaseService.updateTestResult(testCase, CaseState.FAIL);
                } catch (InterruptedException e) {
                    testCaseService.updateTestResult(testCase, CaseState.INTERRUPT);
                    throw new OperationCancelException();
                }
                logger.debug("TestCase: {} executed successfully", testCase);
                testCaseService.updateTestResult();
            });
            logger.debug("Test plan is finished");
            callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
        });
    }

}
