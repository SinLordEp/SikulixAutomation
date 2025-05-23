package controller;

import exception.ExceptionHandler;
import exception.ToolExceptionHandler;
import gui.ToolGUI;
import interfaces.Callback;
import model.*;
import service.*;
import interfaces.EventListener;
import java.util.*;

/**
 * @author Sin
 */
public class ToolController {
    private final List<EventListener<EventPackage>> listeners = new ArrayList<>();
    private final TestCaseService testCaseService;
    private final CaseExecuteService caseExecuteService;
    private final ExceptionHandler exceptionHandler;
    private final WindowService windowService;


    public ToolController() {
        testCaseService = new TestCaseService(this::notifyEvent);
        caseExecuteService = new CaseExecuteService(testCaseService, this::notifyEvent);
        exceptionHandler = new ToolExceptionHandler();
        windowService = new WindowService();
    }

    public void run(){
        new ToolGUI(this).run();
    }

    public void loadConfig(){
        exceptionHandler.run(testCaseService::loadConfig, testCaseService.getClass().getName());
    }

    public boolean saveConfig(){
        return exceptionHandler.run(testCaseService::saveConfig, testCaseService.getClass().getName());
    }

    public void addCategory(String name){
        exceptionHandler.run(()-> testCaseService.addCategory(name), testCaseService.getClass().getName());
    }

    public void deleteCategory(String category){
        exceptionHandler.run(()-> testCaseService.deleteCategory(category), testCaseService.getClass().getName());
    }

    public void addTestCase(String category, String name){
        exceptionHandler.run(()-> testCaseService.addTestCase(category, name), testCaseService.getClass().getName());
    }

    public void deleteTestCase(String category, int caseIndex){
        exceptionHandler.run(()-> testCaseService.deleteTestCase(category, caseIndex), testCaseService.getClass().getName());
    }

    public void modifyTestCase(String category, int caseIndex, String name){
        exceptionHandler.run(()-> testCaseService.modifyTestCase(category, caseIndex, name), testCaseService.getClass().getName());
    }

    public void modifyTestCaseOrder(String category, int oldCaseIndex, int newCaseIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestCaseOrder(category, oldCaseIndex, newCaseIndex), testCaseService.getClass().getName());
    }

    public void addTestStep(String category, int caseIndex){
        exceptionHandler.run(()-> testCaseService.addTestStep(category, caseIndex), testCaseService.getClass().getName());
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        exceptionHandler.run(()-> testCaseService.deleteTestStep(category, caseIndex, stepIndex), testCaseService.getClass().getName());
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestStep(category, caseIndex, stepIndex), testCaseService.getClass().getName());
    }

    public void modifyTestStepOrder(String category, int caseIndex, int oldStepIndex, int newStepIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestStepOrder( category, caseIndex, oldStepIndex, newStepIndex), testCaseService.getClass().getName());
    }

    public void buildTestPlan(HashMap<String, ArrayList<TestCase>> testCases){
        exceptionHandler.run(()-> testCaseService.buildTestPlan(testCases), testCaseService.getClass().getName());
    }

    public void startTest(){
        exceptionHandler.run(caseExecuteService::startTest, caseExecuteService.getClass().getName());
    }

    public void stopTest(){
        exceptionHandler.run(caseExecuteService::stopTest, testCaseService.getClass().getName());
    }

    public void generateResult(){
        exceptionHandler.run(testCaseService::generateResult, testCaseService.getClass().getName());
    }

    public void captureWindow(String windowName, int width, int height, Callback<EventPackage> callback){
        exceptionHandler.run(() -> windowService.captureWindow(windowName, width, height, callback), windowService.getClass().getName());
    }

    public void onWindowClosing() {
        exceptionHandler.run(testCaseService::saveDataOnChanged, testCaseService.getClass().getName());
        System.exit(0);
    }

    public void addListener(EventListener<EventPackage> listener){
        listeners.add(listener);
    }

    private void notifyEvent(EventPackage eventPackage){
        listeners.forEach(listener -> exceptionHandler.run(() -> listener.onEvent(eventPackage), listener.getClass().getSimpleName()));
    }

}
