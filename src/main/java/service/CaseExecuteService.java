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


import java.util.List;

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

    public void startTest(boolean isIterating){
        buildThread(testCaseService.getTestPlan(), isIterating);
        testThread.start();
    }

    public void stopTest(){
        testThread.interrupt();
        testCaseService.repaintTestResult();
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }

    private void buildThread(List<TestCase> currentTestPlan, boolean isIterating){
        testThread = new Thread(() -> {
            int[] caseIndex = new int[1];
            currentTestPlan.forEach(testCase -> {
                try {
                    Thread.sleep(2000);
                    logger.debug("Executing TestCase: {}", testCase);
                    int[] paramIndex = new int[1];
                    SikulixUtils.setImagePath(testCase.getName());
                    testCase.getSteps().forEach(testStep -> {
                        testCase.nextCurrentStep();
                        testCase.setState(CaseState.ONGOING);
                        testCaseService.updateTestPlan(caseIndex[0], testCase);
                        StepState stepState = testStep.getJsonParams().isEmpty() ? stepExecuteService.execute(testStep)
                                : stepExecuteService.execute(testStep, testCaseService.getJsonByCaseAndIndex(testCase.getName(), paramIndex[0]));
                        if(isIterating){
                            paramIndex[0]++;
                        }
                        if (stepState != StepState.PASS) {
                            throw new TestStepFailedException(testStep.toString());
                        }
                    });
                    testCase.setState(CaseState.PASS);
                    testCaseService.updateTestPlan(caseIndex[0]++ ,testCase);
                } catch (TestStepFailedException e) {
                    logger.debug("TestCase: {} failed at TestStep: {}", testCase, e.getMessage());
                    testCase.setState(CaseState.FAIL);
                    testCaseService.updateTestPlan(caseIndex[0]++, testCase);
                } catch (InterruptedException e) {
                    testCase.setState(CaseState.INTERRUPT);
                    testCaseService.updateTestPlan(caseIndex[0]++, testCase);
                    throw new OperationCancelException();
                }
                logger.debug("TestCase: {} executed successfully", testCase);
                testCaseService.repaintTestResult();
                testCaseService.updateTestResult(testCase, "");
            });
            logger.debug("Test plan is finished");
            callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
        });
    }


}
