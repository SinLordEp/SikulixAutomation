package service;

import exception.TestInterruptException;
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
    private boolean interrupted = false;

    public CaseExecuteService(TestCaseService testCaseService, Callback<EventPackage> callback) {
        this.testCaseService = testCaseService;
        this.stepExecuteService = new StepExecuteService();
        this.callback = callback;
    }

    public void startTest(boolean isIterating){
        buildThread(testCaseService.getTestPlan(), isIterating);
        interrupted = false;
        testThread.start();
    }

    public void stopTest(){
        interrupted = true;
        testCaseService.repaintTestResult();
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }

    private void buildThread(List<TestCase> currentTestPlan, boolean isIterating) {
        testThread = new Thread(() -> {
            ExecutionContext context = new ExecutionContext();
            try {
                for (TestCase testCase : currentTestPlan) {
                    if (interrupted) {
                        handleInterrupt(context, testCase);
                        break;
                    }
                    executeTestCase(context, testCase, isIterating);
                }
            } catch (Exception e) {
                logger.error("Error during test execution: {}", e.getMessage(), e);
            }

            finalizeExecution();
        });
    }

    private void executeTestCase(ExecutionContext context, TestCase testCase, boolean isIterating) {
        try {
            Thread.sleep(1500);
            logger.debug("Executing TestCase: {}", testCase);
            updateCaseParams(context, testCase, isIterating);
            SikulixUtils.setImagePath(testCase.getName());
            testCase.getSteps().forEach(testStep -> {
                if (interrupted) {
                    throw new TestInterruptException("Execution interrupted during TestStep.");
                }
                executeTestStep(context, testCase, testStep);
            });
            testCase.setState(CaseState.PASS);
            testCaseService.updateTestPlan(context.caseIndex++, testCase);
        } catch (TestStepFailedException e) {
            handleTestStepFailure(context, testCase, e);
        } catch (InterruptedException | TestInterruptException e) {
            handleInterrupt(context, testCase);
        }
    }

    private void executeTestStep(ExecutionContext context, TestCase testCase, TestStep testStep) {
        testCase.nextCurrentStep();
        testCase.setState(CaseState.ONGOING);
        testCaseService.updateTestPlan(context.caseIndex, testCase);

        StepState stepState = testStep.getJsonParams().isEmpty()
                ? stepExecuteService.execute(testStep)
                : stepExecuteService.execute(testStep, testCaseService.getJsonByCaseAndIndex(testCase.getName(), context.paramIndex));

        if (stepState != StepState.PASS) {
            throw new TestStepFailedException("Step failed: " + testStep);
        }
    }

    private void handleInterrupt(ExecutionContext context, TestCase testCase) {
        logger.debug("TestCase interrupted: {}", testCase.getName());
        testCase.setState(CaseState.INTERRUPT);
        testCaseService.updateTestPlan(context.caseIndex, testCase);
    }

    private void updateCaseParams(ExecutionContext context, TestCase testCase, boolean isIterating) {
        if (isIterating && context.isSameCaseAsPrevious(testCase)) {
            context.incrementParamIndex();
        } else {
            context.updateCaseName(testCase.getName());
        }
    }

    private void handleTestStepFailure(ExecutionContext context, TestCase testCase, TestStepFailedException e) {
        if(interrupted){
            handleInterrupt(context, testCase);
            return;
        }
        logger.debug("TestCase failed: {} at TestStep: {}", testCase.getName(), e.getMessage());
        testCase.setState(CaseState.FAIL);
        testCaseService.updateTestPlan(context.caseIndex++, testCase);
    }

    private void finalizeExecution() {
        logger.debug("Test plan is finished");
        callback.onSubmit(new EventPackage(EventCommand.TEST_FINISHED));
    }

    private static class ExecutionContext {
        private int caseIndex = 0;
        private int paramIndex = 0;
        private String currentCaseName = "";

        public void updateCaseName(String newCaseName) {
            this.currentCaseName = newCaseName;
            this.paramIndex = 0;
        }

        public boolean isSameCaseAsPrevious(TestCase testCase) {
            return this.currentCaseName.equals(testCase.getName());
        }

        public void incrementParamIndex() {
            this.paramIndex++;
        }
    }


}
