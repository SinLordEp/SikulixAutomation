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
    private final JPanel rightDetailPanel = new JPanel();
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

        rightDetailPanel.setLayout(new BoxLayout(rightDetailPanel, BoxLayout.Y_AXIS));
        JScrollPane casePane = new JScrollPane(rightDetailPanel);

        // Load category and cases config
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
        // add category and case panel
        splitPane.setLeftComponent(categoryPane);
        splitPane.setRightComponent(casePane);
        splitPane.setResizeWeight(0.3);
        testCasePanel.add(splitPane, BorderLayout.CENTER);
        testCasePanel.setPreferredSize(new Dimension(750, 200));
        mainPanel.add(testCasePanel);
        // add category and case panel
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
        rightDetailPanel.removeAll();
        if (category == null) return;
        List<TestCaseItem> items = categoryDataMap.getOrDefault(category, new ArrayList<>());
        for (TestCaseItem item : items) {
            JPanel row = new JPanel(new BorderLayout());
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(item.selected);
            DefaultListModel<String> model = new DefaultListModel<>();
            model.addElement(item.name);
            JList<String> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setVisibleRowCount(1);
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setFixedCellHeight(30);
            list.setFixedCellWidth(150);
            list.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            list.addListSelectionListener(e -> editButton.setEnabled(true));

            checkBox.addActionListener(e -> item.selected = checkBox.isSelected());

            row.add(checkBox, BorderLayout.WEST);
            row.add(list, BorderLayout.CENTER);
            row.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            rightDetailPanel.add(row);
        }
        rightDetailPanel.revalidate();
        rightDetailPanel.repaint();
    }

    private static class TestCaseItem {
        String name;
        boolean selected;
        TestCaseItem(String name, boolean selected) {
            this.name = name;
            this.selected = selected;
        }
    }

    public void run(){
        setVisible(true);
    }

}
