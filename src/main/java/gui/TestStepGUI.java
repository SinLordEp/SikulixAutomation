package gui;

import model.DataSource;
import model.StepAction;
import model.StepElementType;
import model.TestStep;
import org.sikuli.script.Region;
import utils.Callback;
import utils.Screenshot;
import utils.SikulixUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author Sin
 */
public class TestStepGUI extends JFrame {
    private final Region region;
    private final TestStep testStep;
    private Callback<TestStep> callback;
    private final boolean isNewStep;
    private final JTextField stepNameTextField = new JTextField(20);
    private final JTextArea descriptionTextField = new JTextArea(3, 20);
    private final JTextField xTextField = new JTextField();
    private final JTextField yTextField = new JTextField();
    private final JTextField widthTextField = new JTextField("", 3);
    private final JTextField heightTextField = new JTextField("", 3);

    private final ButtonGroup passMatchTypeGroup = new ButtonGroup();
    private final JTextField passImageOrTextField = new JTextField();
    private BufferedImage passImage;
    private final ButtonGroup passActionGroup = new ButtonGroup();
    private final ButtonGroup passTextSourceGroup = new ButtonGroup();
    private final JTextField passTextOrJsonTextField = new JTextField();

    private final ButtonGroup preconditionMatchTypeGroup = new ButtonGroup();
    private final JTextField preconditionImageOrTextField = new JTextField();
    private BufferedImage preconditionImage;
    private final ButtonGroup preconditionActionGroup = new ButtonGroup();
    private final ButtonGroup preconditionTextSourceGroup = new ButtonGroup();
    private final JTextField preconditionTextOrJsonTextField = new JTextField();

    private final ButtonGroup failMatchTypeGroup = new ButtonGroup();
    private final JTextField failImageOrTextField = new JTextField();
    private BufferedImage failImage;
    private final ButtonGroup failActionGroup = new ButtonGroup();
    private final ButtonGroup failTextSourceGroup = new ButtonGroup();
    private final JTextField failTextOrJsonTextField = new JTextField();

    private final ButtonGroup retryMatchTypeGroup = new ButtonGroup();
    private final JTextField retryImageOrTextField = new JTextField();
    private BufferedImage retryImage;
    private final ButtonGroup retryActionGroup = new ButtonGroup();
    private final ButtonGroup retryTextSourceGroup = new ButtonGroup();
    private final JTextField retryTextOrJsonTextField = new JTextField();

    private final ButtonGroup closeMatchTypeGroup = new ButtonGroup();
    private final JTextField closeImageOrTextField = new JTextField();
    private BufferedImage closeImage;
    private final ButtonGroup closeActionGroup = new ButtonGroup();
    private final ButtonGroup closeTextSourceGroup = new ButtonGroup();
    private final JTextField closeTextOrJsonTextField = new JTextField();

    public TestStepGUI(TestStep testStep, Callback<TestStep> callback) {
        this.testStep = testStep;
        this.callback = callback;
        this.region = testStep.getStepElements().get(StepElementType.PASS) == null ? new Region(1, 1, 1399, 999) : testStep.getStepElements().get(StepElementType.PASS).getRegion();
        isNewStep = testStep.getStepElements().get(StepElementType.PASS) == null;
        setTitle("TestStep Editor");
        setSize(500, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMainPanel();
        setVisible(true);
    }

    private void setMainPanel() {
        add(stepInfoPanel(), BorderLayout.NORTH);
        add(stepElementPane(), BorderLayout.CENTER);
        add(bottomButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel stepInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Step Info"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Step name label and input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Step name: "), BorderLayout.WEST);
        namePanel.add(stepNameTextField, BorderLayout.CENTER);
        panel.add(namePanel);

        // Description label
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        panel.add(descriptionPanel);

        // Description input (2 rows height)

        descriptionTextField.setLineWrap(true);
        descriptionTextField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionTextField);
        descScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(descScroll);

        // 4 region info labels + 4 inputs
        JPanel regionValuesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        regionValuesPanel.add(new JLabel("X: "));
        xTextField.setText(String.valueOf(region.getX()));
        regionValuesPanel.add(xTextField);
        regionValuesPanel.add(new JLabel("Y: "));
        yTextField.setText(String.valueOf(region.getY()));
        regionValuesPanel.add(yTextField);
        regionValuesPanel.add(new JLabel("Width: "));
        widthTextField.setText(String.valueOf(region.getW()));
        regionValuesPanel.add(widthTextField);
        regionValuesPanel.add(new JLabel("Height: "));
        heightTextField.setText(String.valueOf(region.getH()));
        regionValuesPanel.add(heightTextField);
        panel.add(regionValuesPanel);

        // Toggle highlight button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton highlightButton = new JButton("Toggle area highlight");
        highlightButton.addActionListener(_ -> SikulixUtils.highlightRegion(region));
        buttonPanel.add(highlightButton);
        panel.add(buttonPanel);

        return panel;
    }

    private JScrollPane stepElementPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Step elements"));
        panel.setAlignmentY(TOP_ALIGNMENT);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        // Collapsible element panels
        Arrays.stream(StepElementType.values()).forEach(element -> panel.add(createElementPanel(element)));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private static JPanel actionTypePanel(StepAction stepAction, ButtonGroup actionGroup, DataSource textSource, ButtonGroup textSourceGroup, JTextField textOrJsonTextField) {
        JPanel wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createTitledBorder("Action type"));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        JPanel sourcePanel = new JPanel();

        // Toggle text source panel
        ActionListener typeSwitch = _ -> {
            boolean visible = Objects.equals(actionGroup.getSelection().getActionCommand(), StepAction.TYPE.name()) || Objects.equals(actionGroup.getSelection().getActionCommand(), StepAction.PASTE.name());
            sourcePanel.setVisible(visible);
            wrapper.revalidate();
            wrapper.repaint();
        };
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (StepAction action : StepAction.values()) {
            JRadioButton actionButton = new JRadioButton(action.name());
            actionButton.setActionCommand(action.name());
            actionButton.setSelected(action == stepAction);
            actionButton.addActionListener(typeSwitch);
            actionGroup.add(actionButton);
            panel.add(actionButton);
        }
        wrapper.add(panel);

        // Text source panel
        sourcePanel.setBorder(BorderFactory.createTitledBorder("Text Data Source"));
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.Y_AXIS));
        JPanel sourceOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton textOption = new JRadioButton("Text");
        textOption.setActionCommand(String.valueOf(DataSource.TEXT));
        JRadioButton jsonOption = new JRadioButton("JSON");
        jsonOption.setActionCommand(String.valueOf(DataSource.JSON));
        if (textSource == DataSource.TEXT) {
            textOption.setSelected(true);
        } else {
            jsonOption.setSelected(true);
        }
        textSourceGroup.add(textOption);
        textSourceGroup.add(jsonOption);
        sourceOptionPanel.add(textOption);
        sourceOptionPanel.add(jsonOption);
        sourcePanel.add(sourceOptionPanel);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sourceLabel = new JLabel("Output Text:");
        labelPanel.add(sourceLabel);
        sourcePanel.add(labelPanel);

        textOrJsonTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        sourcePanel.add(textOrJsonTextField);

        sourcePanel.setVisible(false);
        wrapper.add(sourcePanel);

        // Toggle label and input field
        ActionListener formatSwitch = _ -> sourceLabel.setText(textOption.isSelected() ? "Output Text:" : "Json file path:");
        textOption.addActionListener(formatSwitch);
        jsonOption.addActionListener(formatSwitch);


        return wrapper;
    }

    private JPanel createElementPanel(StepElementType element) {
        DataSource matchSelected = isNewStep ? DataSource.NONE : switch (element) {
            case PRECONDITION -> testStep.getPreconditionElement().getDataSource();
            case PASS -> testStep.getPassElement().getDataSource();
            case FAIL -> testStep.getFailElement().getDataSource();
            case RETRY -> testStep.getRetryElement().getDataSource();
            case CLOSE -> testStep.getCloseElement().getDataSource();
        };
        String title = switch (element) {
            case PRECONDITION -> "Precondition element";
            case PASS -> "Pass element";
            case FAIL -> "Fail element";
            case RETRY -> "Retry element";
            case CLOSE -> "Close element";
        };

        // Initialize three content panel
        JPanel elementPanel = new JPanel();
        elementPanel.setLayout(new BoxLayout(elementPanel, BoxLayout.Y_AXIS));
        elementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel hideOnTogglePanel = new JPanel();
        hideOnTogglePanel.setLayout(new BoxLayout(hideOnTogglePanel, BoxLayout.Y_AXIS));
        hideOnTogglePanel.setAlignmentY(TOP_ALIGNMENT);
        hideOnTogglePanel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel hideOnNonePanel = new JPanel();
        hideOnNonePanel.setLayout(new BoxLayout(hideOnNonePanel, BoxLayout.Y_AXIS));

        JButton toggleButton = new JButton("▶ " + title);
        toggleButton.setAlignmentX(LEFT_ALIGNMENT);
        toggleButton.addActionListener(_ -> {
            boolean visible = !hideOnTogglePanel.isVisible();
            hideOnTogglePanel.setVisible(visible);
            toggleButton.setText((visible ? "▼ " : "▶ ") + title);
            elementPanel.revalidate();
        });

        // Visibility control
        ActionListener visibilityUpdater = _ -> {
            boolean show = !Objects.equals(switch (element){
                case PASS -> passMatchTypeGroup.getSelection().getActionCommand();
                case PRECONDITION -> preconditionMatchTypeGroup.getSelection().getActionCommand();
                case FAIL -> failMatchTypeGroup.getSelection().getActionCommand();
                case RETRY -> retryMatchTypeGroup.getSelection().getActionCommand();
                case CLOSE -> closeMatchTypeGroup.getSelection().getActionCommand();
            }, DataSource.NONE.name());
            hideOnNonePanel.setVisible(show);
            elementPanel.revalidate();
            elementPanel.repaint();
        };

        // Match type selection
        JPanel matchTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (DataSource dataSource : DataSource.values()) {
            if (dataSource == DataSource.JSON || (dataSource == DataSource.NONE && element == StepElementType.PASS)) {
                continue;
            }
            JRadioButton button = new JRadioButton(dataSource.name());
            button.setActionCommand(dataSource.name());
            button.addActionListener(visibilityUpdater);
            button.setSelected(dataSource == matchSelected);
            switch (element) {
                case PRECONDITION -> preconditionMatchTypeGroup.add(button);
                case PASS -> passMatchTypeGroup.add(button);
                case FAIL -> failMatchTypeGroup.add(button);
                case RETRY -> retryMatchTypeGroup.add(button);
                case CLOSE -> closeMatchTypeGroup.add(button);
            }
            matchTypePanel.add(button);
        }
        if(element != StepElementType.PASS){
            SwingUtilities.invokeLater(() -> visibilityUpdater.actionPerformed(null));
        }
        hideOnTogglePanel.add(matchTypePanel);

        // Match type label
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Image name or Text:"));
        hideOnNonePanel.add(labelPanel);

        // Match type input
        if (!isNewStep) {
            switch (element) {
                case PRECONDITION -> preconditionImageOrTextField.setText(testStep.getPreconditionElement().getPath());
                case PASS -> passImageOrTextField.setText(testStep.getPassElement().getPath());
                case FAIL -> failImageOrTextField.setText(testStep.getFailElement().getPath());
                case RETRY -> retryImageOrTextField.setText(testStep.getRetryElement().getPath());
                case CLOSE -> closeImageOrTextField.setText(testStep.getCloseElement().getPath());
            }
        }
        switch (element) {
            case PRECONDITION:
                preconditionImageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                hideOnNonePanel.add(preconditionImageOrTextField);
                break;
            case PASS:
                passImageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                hideOnNonePanel.add(passImageOrTextField);
                break;
            case FAIL:
                failImageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                hideOnNonePanel.add(failImageOrTextField);
                break;
            case RETRY:
                retryImageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                hideOnNonePanel.add(retryImageOrTextField);
                break;
            case CLOSE:
                closeImageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                hideOnNonePanel.add(closeImageOrTextField);
                break;
        }

        // Screenshot button
        JPanel screenshotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton screenshotButton = new JButton("Screenshot");
        screenshotButton.setPreferredSize(new Dimension(120, 25));
        screenshotPanel.add(screenshotButton);
        hideOnNonePanel.add(screenshotPanel);

        // Image label
        JLabel imageLabel = new JLabel(new ImageIcon());
        imageLabel.setPreferredSize(new Dimension(300, 100));
        hideOnNonePanel.add(imageLabel);
        if (!isNewStep) {
            switch (element) {
                case PRECONDITION ->
                        preconditionImage = SikulixUtils.loadImage(testStep.getName() + "/" + testStep.getPreconditionElement().getPath());
                case PASS ->
                        passImage = SikulixUtils.loadImage(testStep.getName() + "/" + testStep.getPassElement().getPath());
                case FAIL ->
                        failImage = SikulixUtils.loadImage(testStep.getName() + "/" + testStep.getFailElement().getPath());
                case RETRY -> retryImage = SikulixUtils.loadImage(testStep.getRetryElement().getPath());
                case CLOSE -> closeImage = SikulixUtils.loadImage(testStep.getCloseElement().getPath());
            }
        }
        screenshotButton.addActionListener(_ -> new Screenshot(image -> {
            setImageToLabel(imageLabel, image);
            switch (element) {
                case PRECONDITION -> preconditionImage = image;
                case PASS -> passImage = image;
                case FAIL -> failImage = image;
                case RETRY -> retryImage = image;
                case CLOSE -> closeImage = image;
            }
        }));

        StepAction action;
        DataSource textSource;
        if (isNewStep) {
            action = StepAction.FIND;
            textSource = DataSource.TEXT;
        } else {
            action = switch (element) {
                case PRECONDITION -> testStep.getPreconditionElement().getAction();
                case PASS -> testStep.getPassElement().getAction();
                case FAIL -> testStep.getFailElement().getAction();
                case RETRY -> testStep.getRetryElement().getAction();
                case CLOSE -> testStep.getCloseElement().getAction();
            };
            textSource = switch (element) {
                case PRECONDITION -> testStep.getPreconditionElement().getTextDataSource();
                case PASS -> testStep.getPassElement().getTextDataSource();
                case FAIL -> testStep.getFailElement().getTextDataSource();
                case RETRY -> testStep.getRetryElement().getTextDataSource();
                case CLOSE -> testStep.getCloseElement().getTextDataSource();
            };
        }
        JPanel actionPanel = switch (element) {
            case PRECONDITION ->
                    actionTypePanel(action, preconditionActionGroup, textSource, preconditionTextSourceGroup, preconditionTextOrJsonTextField);
            case PASS ->
                    actionTypePanel(action, passActionGroup, textSource, passTextSourceGroup, passTextOrJsonTextField);
            case FAIL ->
                    actionTypePanel(action, failActionGroup, textSource, failTextSourceGroup, failTextOrJsonTextField);
            case RETRY ->
                    actionTypePanel(action, retryActionGroup, textSource, retryTextSourceGroup, retryTextOrJsonTextField);
            case CLOSE ->
                    actionTypePanel(action, closeActionGroup, textSource, closeTextSourceGroup, closeTextOrJsonTextField);
        };
        hideOnNonePanel.add(actionPanel);
        hideOnTogglePanel.add(hideOnNonePanel);
        elementPanel.add(toggleButton);
        elementPanel.add(hideOnTogglePanel);
        hideOnTogglePanel.setVisible(false);
        return elementPanel;
    }

    public void setImageToLabel(JLabel label, BufferedImage image) {
        int width = label.getWidth();
        int height = label.getHeight();

        if (width <= 0 || height <= 0) {
            Dimension pref = label.getPreferredSize();
            width = pref.width;
            height = pref.height;
        }

        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }

    private JPanel bottomButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.add(new JButton("Save Step"));
        panel.add(new JButton("Cancel"));
        return panel;
    }
}
