package data;

import model.CaseState;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;

public class TestResultTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Test Case", "Status"};
    private LinkedHashMap<String, CaseState> dataMap;
    private String[] caseNames;

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

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}


