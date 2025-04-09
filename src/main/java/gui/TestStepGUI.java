package gui;

import exception.ImageIOException;
import exception.OperationCancelException;
import model.*;
import model.enums.DataSource;
import model.enums.StepAction;
import model.enums.StepElementType;
import org.sikuli.script.Region;
import interfaces.Callback;
import util.DialogUtils;
import util.ImageUtils;
import util.SikulixUtils;
import util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @author Sin
 */
public class TestStepGUI extends JFrame {
    private final String testCaseName;
    private final TestStep testStep;
    private final boolean isNewStep;
    private final JTextField stepNameTextField = new JTextField(20);
    private final JTextArea descriptionTextField = new JTextArea(3, 20);
    private final JTextField xTextField = new JTextField("0");
    private final JTextField yTextField = new JTextField("0");
    private final JTextField widthTextField = new JTextField("1400", 3);
    private final JTextField heightTextField = new JTextField("1000", 3);

    private final Map<StepElementType, ElementContext> elementContexts = new EnumMap<>(StepElementType.class);

    public TestStepGUI(String testCaseName, TestStep testStep, Callback<TestStep> callback) {
        this.testCaseName = testCaseName;
        this.testStep = testStep;
        isNewStep = testStep.getPassElement() == null;
        setTitle("TestStep Editor");
        setSize(500, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initElementContexts();
        if(!isNewStep){
            parseTestStep();
        }
        setMainPanel(callback);
        setVisible(true);
    }

    private void initElementContexts() {
        Arrays.stream(StepElementType.values()).forEach(stepElementType -> elementContexts.put(stepElementType,
                new ElementContext(stepElementType, stepElementType.name())));
    }

    private void parseTestStep() {
        xTextField.setText(String.valueOf(testStep.getX()));
        yTextField.setText(String.valueOf(testStep.getY()));
        widthTextField.setText(String.valueOf(testStep.getWidth()));
        heightTextField.setText(String.valueOf(testStep.getHeight()));
        Arrays.stream(StepElementType.values()).forEach(stepElementType -> {
            StepElement element = testStep.getStepElements().get(stepElementType);
            if(element != null){
                parseElement(element, elementContexts.get(stepElementType));
            }
        });
    }

    private void parseElement(StepElement element, ElementContext context){
        context.matchType = element.getMatchType();
        context.timeOutTextField.setText(String.valueOf(element.getTimeoutSec()));
        SwingUtils.makeTextFieldIntegerWithMax(context.timeOutTextField, 60);

        context.similarityTextField.setText(String.valueOf((int)(element.getSimilarity()*100)));
        SwingUtils.makeTextFieldIntegerWithMax(context.similarityTextField, 100);
        context.imageOrTextField.setText(element.getImageNameOrText().replace(".PNG", "").replace(".png", ""));

        context.action = element.getAction();

        if (element.getAction() == StepAction.TYPE || element.getAction() == StepAction.PASTE) {
            context.action = element.getAction();
            context.textOrJsonTextField.setText(element.getOutputText());
            context.enterKey.setSelected(element.isEnterKey());
        }

    }

    private void setMainPanel(Callback<TestStep> callback) {
        add(createStepInfoPanel(), BorderLayout.NORTH);
        add(createStepElementPane(), BorderLayout.CENTER);
        add(bottomButtonsPanel(callback), BorderLayout.SOUTH);
    }

    private JPanel createStepInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Step Info"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Step name label and input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Step name:    "), BorderLayout.WEST);
        stepNameTextField.setText(testStep.getName());
        namePanel.add(stepNameTextField, BorderLayout.CENTER);
        panel.add(namePanel);

        // Description label
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        panel.add(descriptionPanel);

        // Description input (2 rows height)
        descriptionTextField.setLineWrap(true);
        descriptionTextField.setWrapStyleWord(true);
        descriptionTextField.setText(isNewStep ? "" : testStep.getDescription());
        JScrollPane descScroll = new JScrollPane(descriptionTextField);
        descScroll.setPreferredSize(new Dimension(400, 60));
        panel.add(descScroll);

        // 4 region info labels + 4 inputs
        JPanel regionValuesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        regionValuesPanel.add(new JLabel("X: "));
        SwingUtils.makeTextFieldIntegerOnly(xTextField);
        regionValuesPanel.add(xTextField);

        regionValuesPanel.add(new JLabel("Y: "));
        SwingUtils.makeTextFieldIntegerOnly(yTextField);
        regionValuesPanel.add(yTextField);

        regionValuesPanel.add(new JLabel("Width: "));
        SwingUtils.makeTextFieldIntegerOnly(widthTextField);
        regionValuesPanel.add(widthTextField);

        regionValuesPanel.add(new JLabel("Height: "));
        SwingUtils.makeTextFieldIntegerOnly(heightTextField);
        regionValuesPanel.add(heightTextField);
        panel.add(regionValuesPanel);

        // Toggle highlight button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton highlightButton = new JButton("Toggle area highlight");
        highlightButton.addActionListener(_ -> SikulixUtils.highlightRegion(buildRegion()));
        buttonPanel.add(highlightButton);
        panel.add(buttonPanel);

        return panel;
    }

    private JScrollPane createStepElementPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Step elements"));
        panel.setAlignmentY(TOP_ALIGNMENT);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        // Collapsible element panels
        Arrays.stream(StepElementType.values()).forEach(element -> panel.add(createElementPanel(elementContexts.get(element))));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createElementPanel(ElementContext context) {
        setupToggleButton(context);
        setupMatchTypePanel(context);
        setupTimeOutAndSimilarityPanel(context);
        setupMatchInputSection(context);
        setupScreenshotSection(context);
        setupActionPanel(context);

        context.hideOnTogglePanel.add(context.hideOnNonePanel);
        context.elementPanel.add(context.hideOnTogglePanel);
        context.hideOnTogglePanel.setVisible(false);
        return context.elementPanel;
    }

    private void setupToggleButton(ElementContext context) {
        JButton toggleButton = new JButton("▶ " + context.title);
        toggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, toggleButton.getPreferredSize().height));
        toggleButton.setPreferredSize(new Dimension(0, 30));
        toggleButton.addActionListener(_ -> {
            boolean visible = !context.hideOnTogglePanel.isVisible();
            context.hideOnTogglePanel.setVisible(visible);
            toggleButton.setText((visible ? "▼ " : "▶ ") + context.title);
            context.elementPanel.revalidate();
        });
        context.elementPanel.add(toggleButton);
    }

    private void setupMatchTypePanel(ElementContext context) {
        JPanel matchTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ActionListener visibilityUpdater = _ -> {
            context.matchType = DataSource.valueOf(context.matchTypeGroup.getSelection().getActionCommand());
            context.hideOnNonePanel.setVisible(context.matchType != DataSource.NONE);
            context.elementPanel.revalidate();
            context.elementPanel.repaint();
        };

        for (DataSource dataSource : DataSource.values()) {
            if (dataSource == DataSource.JSON || (dataSource == DataSource.NONE && context.elementType == StepElementType.PASS)) {
                continue;
            }
            JRadioButton button = new JRadioButton(dataSource.name());
            button.setActionCommand(dataSource.name());
            button.setSelected(dataSource == context.matchType);
            button.addActionListener(visibilityUpdater);
            context.matchTypeGroup.add(button);
            matchTypePanel.add(button);
        }

        if (context.elementType != StepElementType.PASS) {
            SwingUtilities.invokeLater(() -> visibilityUpdater.actionPerformed(null));
        }

        context.hideOnTogglePanel.add(matchTypePanel);
    }

    private void setupTimeOutAndSimilarityPanel(ElementContext context){
        JPanel timeOutAndSimilarityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        StepElement stepElement = testStep.getStepElements().get(context.elementType);
        if(!isNewStep && stepElement != null){
            context.timeOutTextField.setText(String.valueOf(stepElement.getTimeoutSec()));
            context.similarityTextField.setText(String.valueOf(stepElement.getSimilarity()));
        }
        timeOutAndSimilarityPanel.add(new JLabel("Time out second(1-60): "));
        timeOutAndSimilarityPanel.add(context.timeOutTextField);
        timeOutAndSimilarityPanel.add(new JLabel("Similarity(1-100): "));
        timeOutAndSimilarityPanel.add(context.similarityTextField);
        context.hideOnNonePanel.add(timeOutAndSimilarityPanel);
    }

    private void setupMatchInputSection(ElementContext context) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Image name or Text:"));
        context.hideOnNonePanel.add(labelPanel);
        context.imageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        context.hideOnNonePanel.add(context.imageOrTextField);
    }

    private void setupScreenshotSection(ElementContext ctx) {
        JPanel screenshotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton screenshotButton = new JButton("Screenshot");
        screenshotButton.setPreferredSize(new Dimension(120, 25));
        screenshotPanel.add(screenshotButton);
        ctx.hideOnNonePanel.add(screenshotPanel);

        JLabel imageLabel = new JLabel(new ImageIcon());
        imageLabel.setPreferredSize(new Dimension(300, 100));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel imageContainer = new JPanel();
        imageContainer.setLayout(new BorderLayout());
        imageContainer.setBorder(BorderFactory.createTitledBorder("Captured Image Preview"));
        imageContainer.add(imageLabel, BorderLayout.CENTER);

        ctx.hideOnNonePanel.add(imageContainer);

        if (!isNewStep && testStep.getStepElements().get(ctx.elementType) != null) {
            // Sikulix Robot.class can not be called in a swing awake event
            new Thread(() -> {
                BufferedImage img = ImageUtils.loadImage(testCaseName + File.separator + testStep.getStepElements().get(ctx.elementType).getImageNameOrText());
                SwingUtilities.invokeLater(() -> {
                    ctx.image = img;
                    setImageToLabel(imageLabel, ctx.image);
                });
            }).start();
        }

        screenshotButton.addActionListener(_ -> new Screenshot(captured -> {
            ctx.image = captured;
            setImageToLabel(imageLabel, ctx.image);
            if(ctx.imageOrTextField.getText().isEmpty()){
                ctx.imageOrTextField.setText("%s_%s_%s".formatted(testCaseName, stepNameTextField.getText(), ctx.elementType));
            }
        }));
    }

    private void setupActionPanel(ElementContext context) {
        JPanel wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createTitledBorder("Action type"));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        JPanel sourcePanel = new JPanel();

        // Toggle text source panel
        ActionListener typeSwitch = _ -> {
            context.action = StepAction.valueOf(context.actionGroup.getSelection().getActionCommand());
            sourcePanel.setVisible(context.action == StepAction.TYPE || context.action == StepAction.PASTE);
            wrapper.revalidate();
            wrapper.repaint();
        };
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (StepAction action : StepAction.values()) {
            JRadioButton actionButton = new JRadioButton(action.name());
            actionButton.setActionCommand(action.name());
            actionButton.setSelected(action == context.action);
            actionButton.addActionListener(typeSwitch);
            context.actionGroup.add(actionButton);
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

        if (context.textSource == DataSource.TEXT) {
            textOption.setSelected(true);
        } else {
            jsonOption.setSelected(true);
        }
        context.textSourceGroup.add(textOption);
        context.textSourceGroup.add(jsonOption);
        sourceOptionPanel.add(textOption);
        sourceOptionPanel.add(jsonOption);
        sourceOptionPanel.add(context.enterKey);
        sourcePanel.add(sourceOptionPanel);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sourceLabel = new JLabel("Output Text:");
        labelPanel.add(sourceLabel);
        sourcePanel.add(labelPanel);

        context.textOrJsonTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        sourcePanel.add(context.textOrJsonTextField);

        sourcePanel.setVisible(false);
        wrapper.add(sourcePanel);

        // Toggle label and input field
        ActionListener formatSwitch = _ -> sourceLabel.setText(textOption.isSelected() ? "Output Text:" : "Json file path:");
        textOption.addActionListener(formatSwitch);
        jsonOption.addActionListener(formatSwitch);
        if(context.action == StepAction.TYPE || context.action == StepAction.PASTE){
            SwingUtilities.invokeLater(() -> typeSwitch.actionPerformed(null));
        }
        context.hideOnNonePanel.add(wrapper);
    }

    protected static class ElementContext {
        StepElementType elementType;
        String title;
        DataSource matchType = DataSource.NONE;
        BufferedImage image;
        StepAction action = StepAction.FIND;
        DataSource textSource = DataSource.TEXT;
        ButtonGroup matchTypeGroup = new ButtonGroup();
        JTextField imageOrTextField = new JTextField();
        JTextField timeOutTextField = new JTextField("2", 3);
        JTextField similarityTextField = new JTextField("90", 3);
        ButtonGroup actionGroup = new ButtonGroup();
        ButtonGroup textSourceGroup = new ButtonGroup();
        JTextField textOrJsonTextField = new JTextField();
        JPanel elementPanel = new JPanel();
        JPanel hideOnTogglePanel = new JPanel();
        JPanel hideOnNonePanel = new JPanel();
        JCheckBox enterKey = new JCheckBox("Enter Key");
        public ElementContext(StepElementType elementType, String title) {
            this.elementType = elementType;
            this.title = title;
            if(elementType == StepElementType.PASS){
                matchType = DataSource.IMAGE;
            }
            elementPanel.setLayout(new BoxLayout(elementPanel, BoxLayout.Y_AXIS));
            elementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            hideOnTogglePanel.setLayout(new BoxLayout(hideOnTogglePanel, BoxLayout.Y_AXIS));
            hideOnTogglePanel.setBorder(BorderFactory.createTitledBorder(title));

            hideOnNonePanel.setLayout(new BoxLayout(hideOnNonePanel, BoxLayout.Y_AXIS));
        }
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

    private JPanel bottomButtonsPanel(Callback<TestStep> callback) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JButton saveButton = new JButton("Save Step");
        saveButton.addActionListener(_ -> {
            if(stepNameTextField.getText().isEmpty()){
                DialogUtils.showWarningDialog(this, "Field empty", "Step name cannot be empty!");
                return;
            }
            elementContexts.forEach((_, context) -> {
                if(context.matchType == DataSource.NONE){
                    return;
                }
                if(context.imageOrTextField.getText().isEmpty()){
                    DialogUtils.showErrorDialog(this, "Field empty", "Image name or Text is empty!");
                    throw new OperationCancelException();
                }
            });
            callback.onSubmit(buildTestStep());
            dispose();
        });
        panel.add(saveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        panel.add(cancelButton);
        return panel;
    }

    private TestStep buildTestStep(){
        testStep.setName(stepNameTextField.getText());
        testStep.setDescription(descriptionTextField.getText());
        testStep.setX(Integer.parseInt(xTextField.getText()));
        testStep.setY(Integer.parseInt(yTextField.getText()));
        testStep.setWidth(Integer.parseInt(widthTextField.getText()));
        testStep.setHeight(Integer.parseInt(heightTextField.getText()));
        Arrays.stream(StepElementType.values()).forEach(stepElementType ->
        {
            switch (stepElementType) {
                case PASS -> testStep.setPassElement(buildElement(elementContexts.get(stepElementType)));
                case PRECONDITION -> testStep.setPreconditionElement(buildElement(elementContexts.get(stepElementType)));
                case FAIL -> testStep.setFailElement(buildElement(elementContexts.get(stepElementType)));
                case RETRY -> testStep.setRetryElement(buildElement(elementContexts.get(stepElementType)));
                case CLOSE -> testStep.setCloseElement(buildElement(elementContexts.get(stepElementType)));
            }
        });
        return testStep;
    }

    private Region buildRegion(){
        return new Region(Integer.parseInt(xTextField.getText()),
                Integer.parseInt(yTextField.getText()),
                Integer.parseInt(widthTextField.getText()),
                Integer.parseInt(heightTextField.getText()));
    }

    private StepElement buildElement(ElementContext context) {
        if(context.matchType == DataSource.NONE){
            return null;
        }
        StepElement element = new StepElement();
        element.setMatchType(context.matchType);
        if(context.matchType == DataSource.IMAGE){
            try {
                ImageUtils.saveImage(context.image, testCaseName + File.separator + context.imageOrTextField.getText() + ".PNG");
            } catch (IOException e) {
                throw new ImageIOException("Cannot write image to target path");
            }
        }
        element.setTimeoutSec(Integer.parseInt(context.timeOutTextField.getText()));
        element.setSimilarity(Double.parseDouble(context.similarityTextField.getText())/100);
        element.setImageNameOrText(context.imageOrTextField.getText() + ".PNG");
        StepAction stepAction = StepAction.valueOf(context.actionGroup.getSelection().getActionCommand());
        element.setAction(stepAction);
        if (stepAction == StepAction.TYPE || stepAction == StepAction.PASTE) {
            element.setTextSource(context.textSource);
            element.setOutputText(context.textOrJsonTextField.getText());
            element.setEnterKey(context.enterKey.isSelected());
        }
        return element;
    }

}
