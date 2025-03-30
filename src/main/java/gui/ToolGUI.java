package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sin
 */
public class ToolGUI extends JFrame{
    private final Map<String, List<TestCaseItem>> categoryDataMap = new HashMap<>();
    private final Map<String, List<TestStep>> stepDataMap = new HashMap<>();
    private final DefaultListModel<String> categoryListModel = new DefaultListModel<>();
    private final JList<String> categoryList = new JList<>(categoryListModel);
    private final DefaultListModel<TestCaseItem> caseListModel = new DefaultListModel<>();
    private final JList<TestCaseItem> caseList = new JList<>(caseListModel);
    private final DefaultListModel<TestStep> stepListModel = new DefaultListModel<>();
    private final JList<TestStep> stepList = new JList<>(stepListModel);
    private final JButton editButton = new JButton("Edit");

    public ToolGUI() {
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
                editButton.setEnabled(true);
                loadTestSteps(caseList.getSelectedValue().name);
            }
        });
        caseList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int index = caseList.locationToIndex(evt.getPoint());
                if (index >= 0) {
                    Rectangle rect = caseList.getCellBounds(index, index);
                    if (evt.getX() - rect.x < 20) {
                        TestCaseItem item = caseListModel.getElementAt(index);
                        item.selected = !item.selected;
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

        for (int i = 1; i <= 10; i++) {
            String category = "Category " + i;
            categoryListModel.addElement(category);
            List<TestCaseItem> testCases = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                String testCaseName = "TestCase " + j;
                testCases.add(new TestCaseItem(testCaseName, true));
                List<TestStep> steps = new ArrayList<>();
                for (int k = 1; k <= 3; k++) {
                    steps.add(new TestStep(testCaseName + "-Step" + k));
                }
                stepDataMap.put(testCaseName, steps);
            }
            categoryDataMap.put(category, testCases);
        }

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
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1000, 800));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel configButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JButton("Load config"));
        panel.add(new JButton("Save config"));
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
        panel.add(addAndDeleteButtonsPanel(), BorderLayout.SOUTH);
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
        panel.add(addAndDeleteButtonsPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel stepPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(stepList), BorderLayout.CENTER);
        panel.add(addAndDeleteButtonsPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel addAndDeleteButtonsPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JButton("+"));
        panel.add(new JButton("-"));
        return panel;
    }

    private JPanel testProgressPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(runTestButtonPanel());
        panel.setBorder(BorderFactory.createTitledBorder("Testing Process"));
        panel.add(new JScrollPane(testProgressTable()));
        panel.add(progressButtonPanel());
        return panel;
    }

    private JPanel runTestButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JButton("Start"));
        panel.add(new JButton("Stop"));
        return panel;
    }

    private JTable testProgressTable(){
        String[] columns = {"Test case", "Status"};
        Object[][] data = new Object[20][2];
        for (int i = 0; i < 20; i++) {
            data[i][0] = "TestCase" + (i + 1);
            data[i][1] = "FAIL";
        }
        JTable table = new JTable(data, columns);
        table.setPreferredScrollableViewportSize(new Dimension(100, 150));
        return table;
    }

    private JPanel progressButtonPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JButton("Generate result"));
        return panel;
    }

    private void loadCases(String category) {
        caseListModel.clear();
        if (category == null) {
            return;
        }
        List<TestCaseItem> items = categoryDataMap.getOrDefault(category, new ArrayList<>());
        for (TestCaseItem item : items) {
            caseListModel.addElement(item);
        }
        editButton.setEnabled(false);
        stepListModel.clear();
    }

    private void loadTestSteps(String testCaseName) {
        stepListModel.clear();
        List<TestStep> steps = stepDataMap.getOrDefault(testCaseName, new ArrayList<>());
        for (TestStep step : steps) {
            stepListModel.addElement(step);
        }
    }

    private static class TestCaseItem {
        String name;
        boolean selected;
        TestCaseItem(String name, boolean selected) {
            this.name = name;
            this.selected = selected;
        }
        public String toString() {
            return name;
        }
    }

    private static class TestStep {
        String description;
        TestStep(String description) {
            this.description = description;
        }
        public String toString() {
            return description;
        }
    }

    private static class CaseListRenderer extends JCheckBox implements ListCellRenderer<TestCaseItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends TestCaseItem> list, TestCaseItem value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.name);
            setSelected(value.selected);
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

}
