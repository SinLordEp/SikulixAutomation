package gui;

import controller.ToolController;
import data.TestResultTableModel;
import model.CaseState;
import model.EventPackage;
import model.TestCase;
import model.TestStep;
import utils.EventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * @author Sin
 */
public class ToolGUI extends JFrame implements EventListener<EventPackage>{
    private final ToolController controller;
    private TestResultTableModel resultModel = new TestResultTableModel(new LinkedHashMap<>());
    private final DefaultListModel<String> categoryListModel = new DefaultListModel<>();
    private final JList<String> categoryList = new JList<>(categoryListModel);
    private final DefaultListModel<TestCase> caseListModel = new DefaultListModel<>();
    private final JList<TestCase> caseList = new JList<>(caseListModel);
    private final DefaultListModel<TestStep> stepListModel = new DefaultListModel<>();
    private final JList<TestStep> stepList = new JList<>(stepListModel);
    private HashMap<String, ArrayList<TestCase>> testCases = new HashMap<>();


    public ToolGUI(ToolController controller) {
        this.controller = controller;
        setTitle("Testing Automation");
        setMinimumSize(new Dimension(400, 800));
        setPreferredSize(new Dimension(400, 800));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);
        setMainPanel();
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseList.setCellRenderer(new CaseListRenderer());
        caseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && caseList.getSelectedValue() != null) {
                loadTestSteps(caseList.getSelectedValue());
            }
        });
        caseList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int index = caseList.locationToIndex(evt.getPoint());
                if (index >= 0) {
                    Rectangle rect = caseList.getCellBounds(index, index);
                    if (evt.getX() - rect.x < 20) {
                        TestCase testCase = caseListModel.getElementAt(index);
                        testCase.setSelected(!testCase.isSelected());
                        caseList.repaint();
                    }
                }
            }
        });
        stepList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return label;
        });

        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = categoryList.getSelectedValue();
                loadCases(selected);
            }
        });

        if (!categoryListModel.isEmpty()) {
            categoryList.setSelectedIndex(0);
            loadCases(categoryList.getSelectedValue());
        }
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
        JButton load = new JButton("Load Testcase");
        load.addActionListener(_ -> controller.loadConfig());
        panel.add(load);

        JButton save = new JButton("Save Testcases");
        save.addActionListener(_ -> controller.saveConfig());
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
        addButton.addActionListener(_ -> controller.addCategory());
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(_ -> controller.deleteCategory(categoryList.getSelectedValue()));
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
        JButton addButton = new JButton("+");
        addButton.addActionListener(_ -> controller.addTestCase(categoryList.getSelectedValue()));
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(_ -> controller.deleteTestCase(categoryList.getSelectedValue(), caseList.getSelectedIndex()));
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
        deleteButton.addActionListener(_ -> controller.deleteTestStep(categoryList.getSelectedValue(), caseList.getSelectedIndex(), stepList.getSelectedIndex()));
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel testProgressPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(runTestButtonPanel());
        panel.setBorder(BorderFactory.createTitledBorder("Testing Process"));
        panel.add(new JScrollPane(testResultTable()));
        panel.add(resultButtonPanel());
        return panel;
    }

    private JPanel runTestButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton startButton = new JButton("Start");
        startButton.addActionListener(_ -> controller.startTest(createTestPlan()));
        panel.add(startButton);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(_ -> controller.stopTest());
        panel.add(stopButton);

        return panel;
    }

    private JTable testResultTable(){
        JTable table = new JTable(resultModel);
        table.setPreferredScrollableViewportSize(new Dimension(100, 150));
        return table;
    }

    private JPanel resultButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate result");
        generateButton.addActionListener(_ -> controller.generateResult());
        panel.add(generateButton);
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
        stepListModel.addAll(testCase.getSteps());

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

    public void updateTestCases(HashMap<String, ArrayList<TestCase>> testCases){
        this.testCases = testCases;
        categoryListModel.clear();
        categoryListModel.addAll(testCases.keySet());
    }

    public void updateTestResults(LinkedHashMap<String, CaseState> testResults){
        resultModel.setData(testResults);
    }

    private LinkedHashMap<String, TestCase> createTestPlan(){
        LinkedHashMap<String, TestCase> testPlan = new LinkedHashMap<>();
        testCases.forEach((_, testCases) -> testCases.forEach(testCase -> {
            if(testCase.isSelected()){
                testPlan.put(testCase.getName(), testCase);
            }
        }));
        return testPlan;
    }

    @Override
    public void onEvent(EventPackage eventPackage) {
        switch (eventPackage.getCommand()){
            case TESTCASE_CHANGED -> updateTestCases(eventPackage.getTestCases());
            case RESULT_CHANGED -> updateTestResults(eventPackage.getTestResults());
            default -> throw new RuntimeException("Unknown command (To be perfected)");
        }
    }

}
