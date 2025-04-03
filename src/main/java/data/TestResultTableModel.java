package data;

import model.CaseState;
import model.TestCase;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.LinkedHashMap;

public class TestResultTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Test Case", "Test Step", "Status"};
    private LinkedHashMap<TestCase, CaseState> dataMap;
    private TestCase[] testCases;
    private final DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setForeground(Color.BLACK);
            cell.setBackground(Color.WHITE);

            if (column == 2) {
                String state = (String)value;
                switch (CaseState.valueOf(state)){
                    case ONGOING -> cell.setForeground(Color.BLUE);
                    case PASS -> cell.setForeground(Color.GREEN);
                    case FAIL -> cell.setForeground(Color.RED);
                    case QUEUED -> cell.setForeground(Color.ORANGE);
                    case INTERRUPT -> cell.setForeground(Color.MAGENTA);
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return cell;
        }
    };

    public TestResultTableModel(LinkedHashMap<TestCase, CaseState> testResults) {
        setData(testResults);
    }

    public void setData(LinkedHashMap<TestCase, CaseState> testResults) {
        this.dataMap = testResults;
        this.testCases = testResults.keySet().toArray(new TestCase[0]);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return dataMap.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return switch (col) {
            case 0 -> testCases[row].getName();
            case 1 -> testCases[row].getCurrentTestStep().getName();
            case 2 -> dataMap.get(testCases[row]).toString();
            default -> null;
        };
    }

    public DefaultTableCellRenderer getColorRenderer() {
        return colorRenderer;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}


