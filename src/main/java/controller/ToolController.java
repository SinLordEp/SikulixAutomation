package controller;

import gui.ToolGUI;
import model.*;
import service.*;
import util.EventListener;

import java.util.*;

/**
 * @author Sin
 */
public class ToolController {
    private final List<EventListener<EventPackage>> listeners = new ArrayList<>();
    private final TestCaseService testCaseService;
    private final CaseExecuteService caseExecuteService;


    public ToolController() {
        testCaseService = new TestCaseService(this::notifyEvent);
        caseExecuteService = new CaseExecuteService(testCaseService, this::notifyEvent);
    }

    public void run(){
        new ToolGUI(this).run();
    }

    public void loadConfig(){
        testCaseService.loadConfig();
    }

    public boolean saveConfig(){
       return testCaseService.saveConfig();
    }

    public void addCategory(String name){
        testCaseService.addCategory(name);
    }

    public void deleteCategory(String category){
        testCaseService.deleteCategory(category);
    }

    public void addTestCase(String category, String name){
        testCaseService.addTestCase(category, name);
    }

    public void deleteTestCase(String category, int caseIndex){
        testCaseService.deleteTestCase(category, caseIndex);
    }

    public void addTestStep(String category, int caseIndex){
        testCaseService.addTestStep(category, caseIndex);
    }

    public void deleteTestStep(String category, int caseIndex, int stepIndex){
        testCaseService.deleteTestStep(category, caseIndex, stepIndex);
    }

    public void modifyTestStep(String category, int caseIndex, int stepIndex){
        testCaseService.modifyTestStep(category, caseIndex, stepIndex);
    }

    public void startTest(LinkedHashMap<TestCase, CaseState> currentTestPlan){
        caseExecuteService.startTest(currentTestPlan);
    }

    public void stopTest(){
        caseExecuteService.stopTest();
    }

    public void generateResult(){
        testCaseService.generateResult();
    }

    public void onWindowClosing() {
        testCaseService.saveDataOnChanged();
        System.exit(0);
    }

    public void addListener(EventListener<EventPackage> listener){
        listeners.add(listener);
    }

    private void notifyEvent(EventPackage eventPackage){
        listeners.forEach(listener -> listener.onEvent(eventPackage));
    }


}
