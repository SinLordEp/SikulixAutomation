package gui;

import interfaces.Callback;
import util.JNAUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class WindowSelectorDialog extends JDialog {
    private final DefaultListModel<String> windowListModel = new DefaultListModel<>();
    private final JList<String> windowList = new JList<>(windowListModel);

    public WindowSelectorDialog(JFrame owner, Callback<String> callback) {
        super(owner, "Searching window by name", true);
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(owner);
        JTextField searchField = new JTextField();
        add(searchField, BorderLayout.NORTH);

        windowList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(windowList);
        add(scrollPane, BorderLayout.CENTER);


        windowList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = windowList.getSelectedValue();
                    if (selected != null) {
                        callback.onSubmit(selected);
                        dispose();
                    }
                }
            }
        });


        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateList() {
                String keyword = searchField.getText().trim();
                List<String> titles = JNAUtils.findWindowTitlesContaining(keyword);
                windowListModel.clear();
                titles.forEach(windowListModel::addElement);
            }

            public void insertUpdate(DocumentEvent e) { updateList(); }
            public void removeUpdate(DocumentEvent e) { updateList(); }
            public void changedUpdate(DocumentEvent e) { updateList(); }
        });
        setVisible(true);
    }
}

