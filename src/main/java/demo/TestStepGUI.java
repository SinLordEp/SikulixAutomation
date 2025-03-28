package demo;

import org.sikuli.script.Region;

import javax.swing.*;
import java.awt.*;


public class TestStepGUI extends JFrame{
    private Region region;
    public TestStepGUI(Region region) {
        this.region = region;
        setTitle("Create TestStep");
        setSize(500, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Top Fixed Panel ===
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.insets = new Insets(5, 5, 5, 5);
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.weightx = 1;
        int topRow = 0;

        JPanel regionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        regionPanel.add(new JLabel("X: " + region.getX()));
        regionPanel.add(new JLabel("Y: " + region.getY()));
        regionPanel.add(new JLabel("Width: " + region.getW()));
        regionPanel.add(new JLabel("Height: " + region.getH()));
        regionPanel.add(new JButton("Toggle area highlight"));
        topGbc.gridy = topRow++;
        topPanel.add(regionPanel, topGbc);

        topGbc.gridy = topRow++;
        topPanel.add(new JSeparator(), topGbc);

        add(topPanel, BorderLayout.NORTH);

        // central panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setAlignmentY(TOP_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        int row = 0;

        // L3 title
        gbc.gridy = row++;
        mainPanel.add(new JLabel("Pass element"), gbc);

        // L4 options
        JPanel optionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup group = new ButtonGroup();
        JRadioButton opt1 = new JRadioButton("Image", true);
        JRadioButton opt2 = new JRadioButton("Text");
        group.add(opt1);
        group.add(opt2);
        JCheckBox checkBox = new JCheckBox("Click");
        optionRow.add(opt1);
        optionRow.add(opt2);
        optionRow.add(Box.createHorizontalStrut(50));
        optionRow.add(checkBox);
        gbc.gridy = row++;
        mainPanel.add(optionRow, gbc);

        // L5 - input with label
        gbc.gridy = row++;
        mainPanel.add(new JLabel("Image Path or Text:"), gbc);
        gbc.gridy = row++;
        JTextField mainInput = new JTextField();
        mainInput.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(mainInput, gbc);

        // L6 - screenshot button
        gbc.gridy = row++;
        JPanel shotBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton shotBtn = new JButton("Screenshot");
        shotBtn.setPreferredSize(new Dimension(120, 25));
        shotBtnPanel.add(shotBtn);
        mainPanel.add(shotBtnPanel, gbc);

        // L7 - image
        gbc.gridy = row++;
        JLabel img1 = new JLabel(new ImageIcon());
        img1.setPreferredSize(new Dimension(300, 100));
        mainPanel.add(img1, gbc);

        // L8~L22 - collapsible panels
        for (int section = 1; section <= 3; section++) {
            String title = switch (section) {
                case 1 -> "Fail element";
                case 2 -> "Retry element";
                case 3 -> "Close element";
                default -> throw new IllegalStateException("Unexpected value: " + section);
            };
            JPanel collapsiblePanel = createCollapsiblePanel(title);
            gbc.gridy = row++;
            mainPanel.add(collapsiblePanel, gbc);
        }

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.add(mainPanel, BorderLayout.NORTH); // 保证内容从顶端开始

        JScrollPane scrollPane = new JScrollPane(scrollWrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 提高滚动速率
        add(scrollPane, BorderLayout.CENTER);

        // bottom buttons
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomButtons.add(new JButton("Save Step"));
        bottomButtons.add(new JButton("Cancel"));
        add(bottomButtons, BorderLayout.SOUTH);
    }

    private JPanel createCollapsiblePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JButton toggle = new JButton("▶ " + title);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentY(TOP_ALIGNMENT);

        // Label above input
        content.add(new JLabel("Image Path or Text:"));

        // Three options and checkbox
        JPanel choiceRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup choiceGroup = new ButtonGroup();
        JRadioButton opt1 = new JRadioButton("None", true);
        JRadioButton opt2 = new JRadioButton("Image");
        JRadioButton opt3 = new JRadioButton("Text");
        choiceGroup.add(opt1);
        choiceGroup.add(opt2);
        choiceGroup.add(opt3);
        choiceRow.add(opt1);
        choiceRow.add(opt2);
        choiceRow.add(opt3);
        choiceRow.add(new JCheckBox("Click"));
        content.add(choiceRow);
        // input
        JTextField input = new JTextField();
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        content.add(input);
        // button
        JButton btn = new JButton("Screenshot");
        btn.setPreferredSize(new Dimension(120, 25));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btn);
        content.add(btnPanel);
        // image
        JLabel subImg = new JLabel(new ImageIcon());
        subImg.setPreferredSize(new Dimension(300, 100));
        content.add(subImg);

        panel.add(toggle, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        content.setVisible(false);
        toggle.addActionListener(e -> {
            boolean visible = !content.isVisible();
            content.setVisible(visible);
            toggle.setText((visible ? "▼ " : "▶ ") + title);
            panel.revalidate();
        });
        return panel;
    }



    public void run(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void screenshot(){

    }


}
