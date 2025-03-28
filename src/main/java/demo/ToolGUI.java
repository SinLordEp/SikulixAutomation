package demo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolGUI extends JFrame{
    private final Map<String, List<TestCaseItem>> categoryDataMap = new HashMap<>();
    private final DefaultListModel<String> leftListModel = new DefaultListModel<>();
    private final JList<String> leftList = new JList<>(leftListModel);
    private final DefaultListModel<TestCaseItem> caseListModel = new DefaultListModel<>();
    private final JList<TestCaseItem> caseList = new JList<>(caseListModel);
    private final JButton editButton = new JButton("Edit");

    public ToolGUI() {
        setTitle("Testing Automation");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(new JLabel("Testing category and Test Case"));

        JPanel testCasePanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane categoryPane = new JScrollPane(leftList);

        caseList.setCellRenderer(new CaseListRenderer());
        caseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && caseList.getSelectedValue() != null) {
                editButton.setEnabled(true);
            }
        });
        caseList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int index = caseList.locationToIndex(evt.getPoint());
                if (index >= 0) {
                    TestCaseItem item = caseListModel.getElementAt(index);
                    item.selected = !item.selected;
                    caseList.repaint();
                }
            }
        });

        JScrollPane casePane = new JScrollPane(caseList);

        for (int i = 1; i <= 10; i++) {
            String category = "Category " + i;
            leftListModel.addElement(category);
            List<TestCaseItem> testCases = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                testCases.add(new TestCaseItem("TestCase " + j, true));
            }
            categoryDataMap.put(category, testCases);
        }

        leftList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadCases(leftList.getSelectedValue());
            }
        });

        splitPane.setLeftComponent(categoryPane);
        splitPane.setRightComponent(casePane);
        splitPane.setResizeWeight(0.3);
        testCasePanel.add(splitPane, BorderLayout.CENTER);
        testCasePanel.setPreferredSize(new Dimension(750, 200));
        mainPanel.add(testCasePanel);

        JPanel buttonRow = new JPanel(new BorderLayout());
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(new JButton("Load config"));
        leftButtons.add(new JButton("Save config"));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editButton.setEnabled(false);
        rightButtons.add(editButton);
        rightButtons.add(new JButton("+"));
        rightButtons.add(new JButton("-"));

        buttonRow.add(leftButtons, BorderLayout.WEST);
        buttonRow.add(rightButtons, BorderLayout.EAST);
        mainPanel.add(buttonRow);

        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
        newPanel.setBorder(BorderFactory.createTitledBorder("Testing Process"));

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startPanel.add(new JButton("Start"));
        newPanel.add(startPanel);

        String[] columns = {"Test case", "Status"};
        Object[][] data = new Object[20][2];
        for (int i = 0; i < 20; i++) {
            data[i][0] = "TestCase" + (i + 1);
            data[i][1] = "FAIL";
        }
        JTable table = new JTable(data, columns);
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(700, 150));
        newPanel.add(tableScrollPane);

        JPanel singleButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        singleButtonPanel.add(new JButton("Generate result"));
        newPanel.add(singleButtonPanel);

        mainPanel.add(newPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCases(String category) {
        caseListModel.clear();
        if (category == null) return;
        List<TestCaseItem> items = categoryDataMap.getOrDefault(category, new ArrayList<>());
        for (TestCaseItem item : items) {
            caseListModel.addElement(item);
        }
        editButton.setEnabled(false);
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
