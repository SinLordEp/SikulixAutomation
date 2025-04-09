package data.model;

import model.enums.CaseState;
import model.TestCase;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class TestResultTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Test Case", "Test Step", "Status"};
    private List<TestCase> testPlan;
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

    public TestResultTableModel(List<TestCase> testPlan) {
        setData(testPlan);
    }

    public void setData(List<TestCase> testPlan) {
        this.testPlan = testPlan;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return testPlan.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return switch (col) {
            case 0 -> testPlan.get(row).getName();
            case 1 -> testPlan.get(row).getCurrentTestStep().getName();
            case 2 -> testPlan.get(row).getState().name();
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


