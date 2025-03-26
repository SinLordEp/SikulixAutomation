package demo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DemoGUI {
    private JPanel mainPanel;
    private JTextField textFieldTest;
    private JTable table;
    private JButton buttonOK;
    private JButton buttonCancel;

    public DemoGUI() {
        mainPanel = new JPanel(new BorderLayout());
        textFieldTest = new JTextField();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(textFieldTest, BorderLayout.NORTH);
        String[] columnNames = {"Shape", "Color"};
        Object[][] data = {
                {"round", "red"},
                {"square", "green"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        buttonOK = new JButton("OK");
        buttonCancel = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(buttonOK);
        buttonPanel.add(buttonCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    }

    public void run(){
        JFrame frame = new JFrame("Demo GUI");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
