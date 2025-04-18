package gui;

import controller.ToolController;
import data.model.TestResultTableModel;
import data.model.JListDragActionHandler;
import exception.UndefinedException;
import model.enums.CaseState;
import model.EventPackage;
import model.TestCase;
import model.TestStep;
import util.DialogUtils;
import interfaces.EventListener;
import util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

/**
 * @author Sin
 */
public class ToolGUI extends JFrame implements EventListener<EventPackage>{
    private final ToolController controller;
    private final TestResultTableModel resultModel = new TestResultTableModel(new LinkedHashMap<>());
    private final DefaultListModel<String> categoryListModel = new DefaultListModel<>();
    private final JList<String> categoryList = new JList<>(categoryListModel);
    private final DefaultListModel<TestCase> caseListModel = new DefaultListModel<>();
    private final JList<TestCase> caseList = new JList<>(caseListModel);
    private final DefaultListModel<TestStep> stepListModel = new DefaultListModel<>();
    private final JList<TestStep> stepList = new JList<>(stepListModel);
    private HashMap<String, ArrayList<TestCase>> testCases = new HashMap<>();

    private int categoryIndex = 0;
    private int caseIndex = 0;
    private boolean caseAllSelected = false;
    private boolean windowCaptured = false;
    private boolean testPlanBuilt = false;
    private final JLabel windowNameLabel = new JLabel("No window is selected, please select target window!");
    private final JButton selectWindowButton = new JButton("Select Window");
    private final JButton captureWindowButton = new JButton("Capture Window");
    private final JButton buildPlanButton = new JButton("Build Test Plan");
    private final JButton startButton = new JButton("Start");
    private final JButton stopButton = new JButton("Stop");
    private final JProgressBar testProgressBar = new JProgressBar();

    public ToolGUI(ToolController controller) {
        this.controller = controller;
        controller.addListener(this);
        setTitle("Testing Automation");
        setMinimumSize(new Dimension(400, 800));
        setPreferredSize(new Dimension(400, 800));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                controller.onWindowClosing();
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);
        setMainPanel();
        initializeCategoryList();
        initializeCaseList();
        initializeStepList();
    }

    private void setMainPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        // Row 1 Load config, Save config buttons
        panel.add(configButtonsPanel(), BorderLayout.NORTH);
        // Row 2 Test config panel with category panel, case and step splitPane
        panel.add(testConfigPanel(), BorderLayout.CENTER);
        // Row 3 Testing process panel
        panel.add(testProgressPanel(), BorderLayout.SOUTH);
        JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1000, 800));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel configButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load Test Case");
        load.addActionListener(_ -> {
            controller.loadConfig();
            categoryIndex = 0;
            caseIndex = 0;
        });
        panel.add(load);

        JButton save = new JButton("Save Test Case");
        save.addActionListener(_ -> {
            if(controller.saveConfig()){
                DialogUtils.showInfoDialog(this,"Save test case", "Successfully saved config");
            }else {
                DialogUtils.showErrorDialog(this, "Save test case", "Error saving test case");
            }
        });
        panel.add(save);

        return panel;
    }

    private JPanel testConfigPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panel.setBorder(BorderFactory.createTitledBorder("Test case configuration"));
        splitPane.setLeftComponent(categoryPanel());
        splitPane.setRightComponent(caseAndStepSplitPane());
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(150, 200));
        return panel;
    }

    private JPanel categoryPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("+");
        addButton.addActionListener(_ -> controller.addCategory(DialogUtils.showInputDialog(this, "Add category", "Input the name of the new category:")));
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(_ -> {
            if(categoryList.getSelectedIndex() != -1){
                controller.deleteCategory(categoryList.getSelectedValue());
            }
        });
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JSplitPane caseAndStepSplitPane(){
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, casePanel(), stepPanel());
        pane.setResizeWeight(0.5);
        return pane;
    }

    private JPanel casePanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(caseList), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton allButton = new JButton("All");
        allButton.addActionListener(_ -> {
            caseAllSelected = !caseAllSelected;
            caseListModel.elements().asIterator().forEachRemaining(testCase -> testCase.setSelected(caseAllSelected));
            caseList.repaint();
        });
        buttonPanel.add(allButton);
        JButton addButton = new JButton("+");
        addButton.addActionListener(_ -> controller.addTestCase(categoryList.getSelectedValue(), DialogUtils.showInputDialog(this, "Add test case", "Input the name of the new case:")));
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(_ -> {
            if(categoryList.getSelectedIndex() != -1 && caseList.getSelectedIndex() != -1){
                controller.deleteTestCase(categoryList.getSelectedValue(), caseList.getSelectedIndex());
            }
        });
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel stepPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(stepList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("+");
        addButton.addActionListener(_ -> controller.addTestStep(categoryList.getSelectedValue(), caseList.getSelectedIndex()));
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(_ -> {
            if(categoryList.getSelectedIndex() != -1 && caseList.getSelectedIndex() != -1 && stepList.getSelectedIndex() != -1){
                controller.deleteTestStep(categoryList.getSelectedValue(), caseList.getSelectedIndex(), stepList.getSelectedIndex());
            }
        });
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel testProgressPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(captureWindowPanel());
        panel.add(runTestButtonPanel());
        panel.setBorder(BorderFactory.createTitledBorder("Testing Process"));
        panel.add(new JScrollPane(testResultTable()));
        panel.add(resultButtonPanel());
        return panel;
    }

    private JPanel captureWindowPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectWindowButton.addActionListener(_ -> new WindowSelectorDialog(this, this::captureWindowName));
        buttonPanel.add(selectWindowButton);

        JPanel windowNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        windowNamePanel.setBorder(BorderFactory.createTitledBorder("Target window name"));
        windowNamePanel.add(windowNameLabel);

        JPanel modifyWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel widthLabel = new JLabel("Width: ");
        JTextField widthField = new JTextField("1400",3);
        SwingUtils.makeTextFieldIntegerWithMax(widthField, 1920);
        modifyWindowPanel.add(widthLabel);
        modifyWindowPanel.add(widthField);

        JLabel heightLabel = new JLabel("Height: ");
        JTextField heightField = new JTextField("1000",3);
        SwingUtils.makeTextFieldIntegerWithMax(heightField, 1080);
        modifyWindowPanel.add(heightLabel);
        modifyWindowPanel.add(heightField);

        captureWindowButton.setEnabled(false);
        captureWindowButton.addActionListener(_ -> controller.captureWindow(windowNameLabel.getText(), Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()), this::onEvent));
        modifyWindowPanel.add(captureWindowButton);

        panel.add(buttonPanel);
        panel.add(windowNamePanel);
        panel.add(modifyWindowPanel);
        return panel;
    }
    private JPanel runTestButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buildPlanButton.addActionListener(_ -> controller.buildTestPlan(testCases));
        panel.add(buildPlanButton);

        startButton.addActionListener(_ -> {
            controller.startTest();
            toggleTestRelatedComponents(false);
        });
        startButton.setEnabled(false);
        panel.add(startButton);

        stopButton.setEnabled(false);
        stopButton.addActionListener(_ -> {
            stopButton.setEnabled(false);
            controller.stopTest();
        });
        panel.add(stopButton);

        return panel;
    }

    private JTable testResultTable(){
        JTable table = new JTable(resultModel);
        table.getColumnModel().getColumns().asIterator().forEachRemaining(column -> column.setCellRenderer(resultModel.getColorRenderer()));
        table.setPreferredScrollableViewportSize(new Dimension(100, 150));
        return table;
    }

    private JPanel resultButtonPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        testProgressBar.setStringPainted(true);
        testProgressBar.setMinimum(0);
        panel.add(testProgressBar, BorderLayout.WEST);
        JButton generateButton = new JButton("Generate result");
        generateButton.addActionListener(_ -> controller.generateResult());
        panel.add(generateButton, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
        return panel;
    }

    private void loadCases(String category) {
        caseListModel.clear();
        if (category == null) {
            return;
        }
        caseListModel.addAll(testCases.getOrDefault(category, new ArrayList<>()));
        caseList.setModel(caseListModel);
        stepListModel.clear();
    }

    private void loadTestSteps(TestCase testCase) {
        stepListModel.clear();
        if (testCase != null) {
            stepListModel.addAll(testCase.getSteps());
        }
    }

    private void initializeCategoryList(){
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if(categoryList.getSelectedIndex() != -1){
                    categoryIndex = categoryList.getSelectedIndex();
                }else{
                    categoryList.setSelectedIndex(categoryIndex);
                }
                loadCases(categoryList.getSelectedValue());
            }
        });
    }

    private void initializeCaseList(){
        caseList.setCellRenderer(new CaseListRenderer());
        caseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if(caseList.getSelectedIndex() != -1){
                    caseIndex = caseList.getSelectedIndex();
                }else{
                    caseList.setSelectedIndex(caseIndex);
                }
                loadTestSteps(caseList.getSelectedValue());
            }
        });
        caseList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = caseList.locationToIndex(e.getPoint());
                if(index < 0){
                    return;
                }
                Rectangle rect = caseList.getCellBounds(index, index);
                if (e.getX() - rect.x < 20) {
                    TestCase testCase = caseListModel.getElementAt(index);
                    testCase.setSelected(!testCase.isSelected());
                    caseList.repaint();
                }else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    controller.modifyTestCase(categoryList.getSelectedValue(), caseList.getSelectedIndex(), DialogUtils.showInputDialog(null, "Modify Test case", "Input new name: "));
                }else{
                    loadTestSteps(caseList.getSelectedValue());
                }
            }
        });
        caseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseList.setDragEnabled(true);
        caseList.setDropMode(DropMode.INSERT);
        JListDragActionHandler dragHandler = new JListDragActionHandler();
        dragHandler.setOrderChangeListener(((oldIndex, newIndex) -> controller.modifyTestCaseOrder(categoryList.getSelectedValue(), oldIndex, newIndex)));
        caseList.setTransferHandler(dragHandler);
    }

    private void initializeStepList(){
        stepList.setCellRenderer((list, value, _, isSelected, _) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });
        stepList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // if double click
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int index = stepList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        controller.modifyTestStep(categoryList.getSelectedValue(), caseList.getSelectedIndex(), index);
                    }
                }
            }
        });
        stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stepList.setDragEnabled(true);
        stepList.setDropMode(DropMode.INSERT);
        JListDragActionHandler dragHandler = new JListDragActionHandler();
        dragHandler.setOrderChangeListener((oldIndex, newIndex) -> controller.modifyTestStepOrder(categoryList.getSelectedValue(), caseList.getSelectedIndex(), oldIndex, newIndex));
        stepList.setTransferHandler(dragHandler);
    }

    private static class CaseListRenderer extends JCheckBox implements ListCellRenderer<TestCase> {
        @Override
        public Component getListCellRendererComponent(JList<? extends TestCase> list, TestCase testCase, int index, boolean isSelected, boolean cellHasFocus) {
            setText(testCase.getName());
            setSelected(testCase.isSelected());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
    }

    public void run(){
        setVisible(true);
    }

    private void updateUITestCases(HashMap<String, ArrayList<TestCase>> testCases){
        this.testCases = testCases;
        categoryListModel.clear();
        categoryListModel.addAll(testCases.keySet());
        if (!categoryListModel.isEmpty()) {
            categoryList.setSelectedIndex(categoryIndex);
            loadCases(categoryList.getSelectedValue());
        }
        if(!caseListModel.isEmpty()){
            caseList.setSelectedIndex(caseIndex);
            loadTestSteps(caseList.getSelectedValue());
        }
    }

    private void updateUITestResults(LinkedHashMap<TestCase, CaseState> testResults){
        resultModel.setData(testResults);
        int[] currentProgress = new int[1];
        testResults.forEach((testCase, state) -> {
            if(state == CaseState.PASS || state == CaseState.FAIL){
                currentProgress[0] += testCase.getSteps().size();
                return;}
            if(state == CaseState.ONGOING || state == CaseState.INTERRUPT){
                currentProgress[0] += testCase.getCurrentStep();
            }
        });
        testProgressBar.setValue(currentProgress[0]);
    }

    private void testPlanBuilt(int maxValue){
        if(maxValue < 1){
            return;
        }
        testProgressBar.setMaximum(maxValue);
        testProgressBar.setValue(0);
        testPlanBuilt = true;
        if(windowCaptured){
            startButton.setEnabled(true);
        }
    }

    private void toggleTestRelatedComponents(boolean toggle){
        selectWindowButton.setEnabled(toggle);
        captureWindowButton.setEnabled(toggle);
        buildPlanButton.setEnabled(toggle);
        startButton.setEnabled(false);
        stopButton.setEnabled(!toggle);
        testPlanBuilt = false;
    }

    private void captureWindowName(String windowName){
        windowNameLabel.setText(windowName);
        captureWindowButton.setEnabled(true);
    }

    private void windowCaptured(){
        windowCaptured = true;
        if(testPlanBuilt){
            startButton.setEnabled(true);
        }
    }

    @Override
    public void onEvent(EventPackage eventPackage) {
        switch (eventPackage.getCommand()){
            case TESTCASE_CHANGED -> updateUITestCases(eventPackage.getTestCases());
            case RESULT_CHANGED -> updateUITestResults(eventPackage.getTestResults());
            case TEST_FINISHED -> toggleTestRelatedComponents(true);
            case WINDOW_CAPTURED -> windowCaptured();
            case PLAN_BUILT -> testPlanBuilt(eventPackage.getTotalSteps());
            default -> throw new UndefinedException("Unknown command (To be perfected)");
        }
    }

}
