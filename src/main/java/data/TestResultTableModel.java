package data;

import model.CaseState;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.LinkedHashMap;

public class TestResultTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Test Case", "Status"};
    private LinkedHashMap<String, CaseState> dataMap;
    private String[] caseNames;
    private DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setForeground(Color.BLACK);
            cell.setBackground(Color.WHITE);

            if (column == 1) {
                String state = (String)value;
                switch (CaseState.valueOf(state)){
                    case ONGOING -> cell.setForeground(Color.BLUE);
                    case PASS -> cell.setForeground(Color.GREEN);
                    case FAIL -> cell.setForeground(Color.RED);
                    case QUEUED -> cell.setForeground(Color.ORANGE);
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);

            return cell;
        }
    };

    public TestResultTableModel(LinkedHashMap<String, CaseState> testResults) {
        setData(testResults);
    }

    public void setData(LinkedHashMap<String, CaseState> testResults) {
        this.dataMap = testResults;
        this.caseNames = testResults.keySet().toArray(new String[0]);
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
        String key = caseNames[row];
        return switch (col) {
            case 0 -> key;
            case 1 -> dataMap.get(key).toString();
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


