package controller;

import exception.ExceptionHandler;
import exception.ToolExceptionHandler;
import gui.ToolGUI;
import interfaces.Callback;
import model.*;
import service.*;
import interfaces.EventListener;

import javax.swing.*;
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

    public void loadTestCases(JFrame parent){
        exceptionHandler.run(() -> testCaseService.loadConfig(parent), testCaseService.getClass().getName());
    }

    public void saveTestCases(JFrame parent){
        exceptionHandler.run(() -> testCaseService.saveConfig(parent), testCaseService.getClass().getName());
    }

    public void addCategory(JFrame parent){
        exceptionHandler.run(()-> testCaseService.addCategory(parent), testCaseService.getClass().getName());
    }

    public void deleteCategory(JFrame parent, String category){
        exceptionHandler.run(()-> testCaseService.deleteCategory(parent, category), testCaseService.getClass().getName());
    }

    public void modifyCategory(JFrame parent, String category){
        exceptionHandler.run(()-> testCaseService.modifyCategory(parent, category), testCaseService.getClass().getName());
    }

    public void addTestCase(JFrame parent, String category){
        exceptionHandler.run(()-> testCaseService.addTestCase(parent, category), testCaseService.getClass().getName());
    }

    public void deleteTestCase(JFrame parent, String category, int caseIndex){
        exceptionHandler.run(()-> testCaseService.deleteTestCase(parent, category, caseIndex), testCaseService.getClass().getName());
    }

    public void modifyTestCase(JFrame parent, String category, int caseIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestCase(parent, category, caseIndex), testCaseService.getClass().getName());
    }

    public void modifyTestCaseOrder(String category, int oldCaseIndex, int newCaseIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestCaseOrder(category, oldCaseIndex, newCaseIndex), testCaseService.getClass().getName());
    }

    public void addTestStep(JFrame parent, String category, int caseIndex){
        exceptionHandler.run(()-> testCaseService.addTestStep(parent, category, caseIndex), testCaseService.getClass().getName());
    }

    public void deleteTestStep(JFrame parent, String category, int caseIndex, int stepIndex){
        exceptionHandler.run(()-> testCaseService.deleteTestStep(parent, category, caseIndex, stepIndex), testCaseService.getClass().getName());
    }

    public void modifyTestStep(JFrame parent, String category, int caseIndex, int stepIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestStep(parent, category, caseIndex, stepIndex), testCaseService.getClass().getName());
    }

    public void modifyTestStepOrder(String category, int caseIndex, int oldStepIndex, int newStepIndex){
        exceptionHandler.run(()-> testCaseService.modifyTestStepOrder( category, caseIndex, oldStepIndex, newStepIndex), testCaseService.getClass().getName());
    }

    public void loadJson(JFrame parent){
        exceptionHandler.run(()-> testCaseService.loadJson(parent), testCaseService.getClass().getName());
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

    public void generateResult(JFrame parent){
        exceptionHandler.run(()-> testCaseService.generateResult(parent), testCaseService.getClass().getName());
    }

    public void captureWindow(String windowName, int width, int height, Callback<EventPackage> callback){
        exceptionHandler.run(() -> windowService.captureWindow(windowName, width, height, callback), windowService.getClass().getName());
    }

    public void onWindowClosing(JFrame parent) {
        exceptionHandler.run(()-> {
            testCaseService.saveDataOnChanged(parent);
            System.exit(0);
        }, testCaseService.getClass().getName());

    }

    public void addListener(EventListener<EventPackage> listener){
        listeners.add(listener);
    }

    private void notifyEvent(EventPackage eventPackage){
        listeners.forEach(listener -> exceptionHandler.run(() -> listener.onEvent(eventPackage), listener.getClass().getSimpleName()));
    }

}
