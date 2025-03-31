package gui;

import model.TestStep;
import org.sikuli.script.Region;
import utils.Callback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


/**
 * @author Sin
 */
public class TestStepGUI extends JFrame{
    private Region region = new Region(1,1,1399,999);
    private TestStep testStep;

    public TestStepGUI() {
        setTitle("TestStep Editor");
        setSize(400, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMainPanel();
    }

    private void setMainPanel(){
        add(stepInfoPanel(), BorderLayout.NORTH);
        add(stepElementPane(), BorderLayout.CENTER);
        add(bottomButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel stepInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Step Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int row = 0;

        // Step name label and input
        gbc.gridy = row++;
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Step name: "), BorderLayout.WEST);
        namePanel.add(new JTextField(20), BorderLayout.CENTER);
        panel.add(namePanel, gbc);

        // Description label
        gbc.gridy = row++;
        panel.add(new JLabel("Description:"), gbc);

        // Description input (2 rows height)
        gbc.gridy = row++;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.1;
        JTextArea descriptionText = new JTextArea(3, 20);
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionText);
        descScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(descScroll, gbc);
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        row++;

        // 4 region info labels + 4 inputs
        gbc.gridy = row++;
        JPanel regionValuesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        regionValuesPanel.add(new JLabel("X:"));
        regionValuesPanel.add(new JTextField(String.valueOf(region.getX()), 3));
        regionValuesPanel.add(new JLabel("Y:"));
        regionValuesPanel.add(new JTextField(String.valueOf(region.getY()), 3));
        regionValuesPanel.add(new JLabel("Width:"));
        regionValuesPanel.add(new JTextField(String.valueOf(region.getW()), 3));
        regionValuesPanel.add(new JLabel("Height:"));
        regionValuesPanel.add(new JTextField(String.valueOf(region.getH()), 3));
        panel.add(regionValuesPanel, gbc);

        // Toggle highlight button
        gbc.gridy = row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JButton("Toggle area highlight"));
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JScrollPane stepElementPane() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Step elements"));
        panel.setAlignmentY(TOP_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        int row = 0;

        // Title
        gbc.gridy = row++;
        panel.add(new JLabel("Pass element"), gbc);

        // Option row
        gbc.gridy = row++;
        panel.add(matchTypePanel(), gbc);

        // Input label
        gbc.gridy = row++;
        panel.add(new JLabel("Image name or Text:"), gbc);

        // Input field
        gbc.gridy = row++;
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 25));
        panel.add(inputField, gbc);

        // Screenshot button
        gbc.gridy = row++;
        panel.add(screenshotButtonPanel(), gbc);

        // Image label
        gbc.gridy = row++;
        JLabel img = new JLabel(new ImageIcon());
        img.setPreferredSize(new Dimension(300, 100));
        panel.add(img, gbc);

        // Action type selection
        gbc.gridy = row++;
        JPanel actionPanel = actionTypePanel();
        panel.add(actionPanel, gbc);

        // Collapsible element panels
        String[] elementTitles = {"Precondition element","Fail element", "Retry element", "Close element"};
        for (int i = 0; i <= 3; i++) {
            gbc.gridy = row++;
            panel.add(elementCollapsiblePanel(elementTitles[i]), gbc);
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private static JPanel actionTypePanel() {
        JPanel wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createTitledBorder("Action type"));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton matchBtn = new JRadioButton("Match", true);
        JRadioButton clickBtn = new JRadioButton("Click");
        JRadioButton typeBtn = new JRadioButton("Type");
        JRadioButton pasteBtn = new JRadioButton("Paste");
        buttonGroup.add(matchBtn);
        buttonGroup.add(clickBtn);
        buttonGroup.add(typeBtn);
        buttonGroup.add(pasteBtn);
        panel.add(matchBtn);
        panel.add(clickBtn);
        panel.add(typeBtn);
        panel.add(pasteBtn);
        wrapper.add(panel);

        // Text source panel
        JPanel sourcePanel = new JPanel();
        sourcePanel.setBorder(BorderFactory.createTitledBorder("Text Data Source"));
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.Y_AXIS));
        JPanel formatRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup formatGroup = new ButtonGroup();
        JRadioButton textOption = new JRadioButton("Text", true);
        JRadioButton jsonOption = new JRadioButton("JSON");
        formatGroup.add(textOption);
        formatGroup.add(jsonOption);
        formatRow.add(textOption);
        formatRow.add(jsonOption);
        sourcePanel.add(formatRow);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sourceLabel = new JLabel("Output Text:");
        labelPanel.add(sourceLabel);
        sourcePanel.add(labelPanel);

        JTextField outputField = new JTextField();
        outputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        sourcePanel.add(outputField);

        sourcePanel.setVisible(false);
        wrapper.add(sourcePanel);

        // Toggle label and input field
        ActionListener formatSwitch = _ -> sourceLabel.setText(textOption.isSelected() ? "Output Text:" : "Json file path:");
        textOption.addActionListener(formatSwitch);
        jsonOption.addActionListener(formatSwitch);

        // Toggle text source panel
        ActionListener typeSwitch = _ -> {
            boolean visible = typeBtn.isSelected() || pasteBtn.isSelected();
            sourcePanel.setVisible(visible);
            wrapper.revalidate();
            wrapper.repaint();
        };
        typeBtn.addActionListener(typeSwitch);
        pasteBtn.addActionListener(typeSwitch);
        matchBtn.addActionListener(typeSwitch);
        clickBtn.addActionListener(typeSwitch);

        return wrapper;
    }

    private JPanel matchTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup group = new ButtonGroup();
        JRadioButton opt1 = new JRadioButton("Image", true);
        JRadioButton opt2 = new JRadioButton("Text");
        group.add(opt1);
        group.add(opt2);
        panel.add(opt1);
        panel.add(opt2);
        return panel;
    }

    private JPanel screenshotButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton shotBtn = new JButton("Screenshot");
        shotBtn.setPreferredSize(new Dimension(120, 25));
        panel.add(shotBtn);
        return panel;
    }

    private JPanel bottomButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.add(new JButton("Save Step"));
        panel.add(new JButton("Cancel"));
        return panel;
    }

    private JPanel elementCollapsiblePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JButton toggle = new JButton("▶ " + title);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentY(TOP_ALIGNMENT);

        // Match type label
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Image Path or Text:"));
        contentPanel.add(labelPanel);

        // Match type selection
        JPanel matchTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton none = new JRadioButton("None", true);
        JRadioButton img = new JRadioButton("Image");
        JRadioButton txt = new JRadioButton("Text");
        typeGroup.add(none);
        typeGroup.add(img);
        typeGroup.add(txt);
        matchTypePanel.add(none);
        matchTypePanel.add(img);
        matchTypePanel.add(txt);
        contentPanel.add(matchTypePanel);

        // Match type input
        JTextField input = new JTextField();
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        contentPanel.add(input);

        // Screenshot button
        JPanel screenshotButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton screenshot = new JButton("Screenshot");
        screenshot.setPreferredSize(new Dimension(120, 25));
        screenshotButton.add(screenshot);
        contentPanel.add(screenshotButton);

        JLabel imageLabel = new JLabel(new ImageIcon());
        imageLabel.setPreferredSize(new Dimension(300, 100));
        contentPanel.add(imageLabel);

        JPanel actionPanel = actionTypePanel();
        contentPanel.add(actionPanel);

        // Visibility control
        ActionListener visibilityUpdater = _ -> {
            boolean show = !none.isSelected();
            input.setVisible(show);
            screenshotButton.setVisible(show);
            imageLabel.setVisible(show && img.isSelected());
            actionPanel.setVisible(show);
            panel.revalidate();
            panel.repaint();
        };
        none.addActionListener(visibilityUpdater);
        img.addActionListener(visibilityUpdater);
        txt.addActionListener(visibilityUpdater);
        input.setVisible(false);
        screenshotButton.setVisible(false);
        imageLabel.setVisible(false);
        actionPanel.setVisible(false);

        panel.add(toggle, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        contentPanel.setVisible(false);
        toggle.addActionListener(_ -> {
            boolean visible = !contentPanel.isVisible();
            contentPanel.setVisible(visible);
            toggle.setText((visible ? "▼ " : "▶ ") + title);
            panel.revalidate();
        });

        return panel;
    }




    public void run(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onScreenshot(BufferedImage image, JLabel label) {
        label.setIcon(new ImageIcon(image));
    }


}
