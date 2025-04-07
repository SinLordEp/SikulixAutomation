package gui;

import exception.ImageIOException;
import exception.OperationCancelException;
import model.*;
import model.enums.DataSource;
import model.enums.StepAction;
import model.enums.StepElementType;
import org.sikuli.script.Region;
import interfaces.Callback;
import util.ImageUtils;
import util.SikulixUtils;
import util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author Sin
 */
public class TestStepGUI extends JFrame {
    private final String testCaseName;
    private final TestStep testStep;
    private final boolean isNewStep;
    private final JTextField stepNameTextField = new JTextField(20);
    private final JTextArea descriptionTextField = new JTextArea(3, 20);
    private final JTextField xTextField = new JTextField("1");
    private final JTextField yTextField = new JTextField("1");
    private final JTextField widthTextField = new JTextField("1399", 3);
    private final JTextField heightTextField = new JTextField("999", 3);

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

    public TestStepGUI(String testCaseName, TestStep testStep, Callback<TestStep> callback) {
        this.testCaseName = testCaseName;
        this.testStep = testStep;
        isNewStep = testStep.getName() == null || testStep.getName().isEmpty();
        setTitle("TestStep Editor");
        setSize(500, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setMainPanel(callback);
        if(!isNewStep){
            parseTestStep();
        }
        setVisible(true);
    }

    private void parseTestStep() {
        xTextField.setText(String.valueOf(testStep.getX()));
        yTextField.setText(String.valueOf(testStep.getY()));
        widthTextField.setText(String.valueOf(testStep.getWidth()));
        heightTextField.setText(String.valueOf(testStep.getHeight()));
        Arrays.stream(StepElementType.values()).forEach(stepElementType -> {
            if(testStep.getStepElements().get(stepElementType) != null){
                switch (stepElementType) {
                    case PASS -> parsePassElement(testStep.getPassElement());
                    case PRECONDITION -> parsePreconditionElement(testStep.getPreconditionElement());
                    case FAIL -> parseFailElement(testStep.getFailElement());
                    case RETRY -> parseRetryElement(testStep.getRetryElement());
                    case CLOSE -> parseCloseElement(testStep.getCloseElement());
                }
            }
        });
    }

    private void parsePassElement(StepElement element){
        passMatchTypeGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getDataSource().name())));
        passImageOrTextField.setText(element.getPath().replace(".PNG","").replace(".png",""));
        passActionGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getAction().name())));
        if(element.getAction() == StepAction.FIND || element.getAction() == StepAction.CLICK){
            return;
        }
        passTextSourceGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getTextDataSource().name())));
        passTextOrJsonTextField.setText(element.getOutputText());
    }

    private void parsePreconditionElement(StepElement element){
        preconditionMatchTypeGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getDataSource().name())));
        preconditionImageOrTextField.setText(element.getPath().replace(".PNG","").replace(".png",""));
        preconditionActionGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getAction().name())));
        if(element.getAction() == StepAction.FIND || element.getAction() == StepAction.CLICK){
            return;
        }
        preconditionTextSourceGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getTextDataSource().name())));
        preconditionTextOrJsonTextField.setText(element.getOutputText());
    }

    private void parseFailElement(StepElement element){
        failMatchTypeGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getDataSource().name())));
        failImageOrTextField.setText(element.getPath().replace(".PNG","").replace(".png",""));
        failActionGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getAction().name())));
        if(element.getAction() == StepAction.FIND || element.getAction() == StepAction.CLICK){
            return;
        }
        failTextSourceGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getTextDataSource().name())));
        failTextOrJsonTextField.setText(element.getOutputText());
    }

    private void parseRetryElement(StepElement element){
        retryMatchTypeGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getDataSource().name())));
        retryImageOrTextField.setText(element.getPath().replace(".PNG","").replace(".png",""));
        retryActionGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getAction().name())));
        if(element.getAction() == StepAction.FIND || element.getAction() == StepAction.CLICK){
            return;
        }
        retryTextSourceGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getTextDataSource().name())));
        retryTextOrJsonTextField.setText(element.getOutputText());
    }

    private void parseCloseElement(StepElement element){
        closeMatchTypeGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getDataSource().name())));
        closeImageOrTextField.setText(element.getPath().replace(".PNG","").replace(".png",""));
        closeActionGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getAction().name())));
        if(element.getAction() == StepAction.FIND || element.getAction() == StepAction.CLICK){
            return;
        }
        closeTextSourceGroup.getElements().asIterator().forEachRemaining(button -> button.setSelected(button.getActionCommand().equals(element.getTextDataSource().name())));
        closeTextOrJsonTextField.setText(element.getOutputText());
    }

    private void setMainPanel(Callback<TestStep> callback) {
        add(stepInfoPanel(), BorderLayout.NORTH);
        add(stepElementPane(), BorderLayout.CENTER);
        add(bottomButtonsPanel(callback), BorderLayout.SOUTH);
    }

    private JPanel stepInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Step Info"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Step name label and input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Step name:    "), BorderLayout.WEST);
        stepNameTextField.setText(isNewStep ? "" : testStep.getName());
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
        SwingUtils.makeTextFieldNumberOnly(xTextField);
        regionValuesPanel.add(xTextField);

        regionValuesPanel.add(new JLabel("Y: "));
        SwingUtils.makeTextFieldNumberOnly(yTextField);
        regionValuesPanel.add(yTextField);

        regionValuesPanel.add(new JLabel("Width: "));
        SwingUtils.makeTextFieldNumberOnly(widthTextField);
        regionValuesPanel.add(widthTextField);

        regionValuesPanel.add(new JLabel("Height: "));
        SwingUtils.makeTextFieldNumberOnly(heightTextField);
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

    public JPanel createElementPanel(StepElementType element) {
        ElementContext ctx = initElementContext(element);
        JPanel elementPanel = ctx.elementPanel;

        JButton toggleButton = createToggleButton(ctx.title, elementPanel, ctx.hideOnTogglePanel);
        elementPanel.add(toggleButton);

        JPanel matchTypePanel = createMatchTypePanel(element, ctx.matchTypeGroup, ctx.matchSelected, elementPanel, ctx.hideOnNonePanel);
        ctx.hideOnTogglePanel.add(matchTypePanel);

        setupMatchInputSection(ctx, element);
        setupScreenshotSection(ctx, element);
        setupActionPanel(ctx, element);

        ctx.hideOnTogglePanel.add(ctx.hideOnNonePanel);
        elementPanel.add(ctx.hideOnTogglePanel);
        ctx.hideOnTogglePanel.setVisible(false);

        return elementPanel;
    }

    private ElementContext initElementContext(StepElementType element) {
        ElementContext ctx = new ElementContext();
        ctx.title = switch (element) {
            case PASS -> "Pass element";
            case PRECONDITION -> "Precondition element";
            case FAIL -> "Fail element";
            case RETRY -> "Retry element";
            case CLOSE -> "Close element";
        };
        ctx.matchSelected = isNewStep || testStep.getStepElements().get(element) == null
                ? DataSource.NONE : testStep.getStepElements().get(element).getDataSource();

        switch (element) {
            case PASS -> {
                ctx.matchTypeGroup = passMatchTypeGroup;
                ctx.imageOrTextField = passImageOrTextField;
                ctx.image = passImage;
                ctx.actionGroup = passActionGroup;
                ctx.textSourceGroup = passTextSourceGroup;
                ctx.textOrJsonTextField = passTextOrJsonTextField;
            }
            case PRECONDITION -> {
                ctx.matchTypeGroup = preconditionMatchTypeGroup;
                ctx.imageOrTextField = preconditionImageOrTextField;
                ctx.image = preconditionImage;
                ctx.actionGroup = preconditionActionGroup;
                ctx.textSourceGroup = preconditionTextSourceGroup;
                ctx.textOrJsonTextField = preconditionTextOrJsonTextField;
            }
            case FAIL -> {
                ctx.matchTypeGroup = failMatchTypeGroup;
                ctx.imageOrTextField = failImageOrTextField;
                ctx.image = failImage;
                ctx.actionGroup = failActionGroup;
                ctx.textSourceGroup = failTextSourceGroup;
                ctx.textOrJsonTextField = failTextOrJsonTextField;
            }
            case RETRY -> {
                ctx.matchTypeGroup = retryMatchTypeGroup;
                ctx.imageOrTextField = retryImageOrTextField;
                ctx.image = retryImage;
                ctx.actionGroup = retryActionGroup;
                ctx.textSourceGroup = retryTextSourceGroup;
                ctx.textOrJsonTextField = retryTextOrJsonTextField;
            }
            case CLOSE -> {
                ctx.matchTypeGroup = closeMatchTypeGroup;
                ctx.imageOrTextField = closeImageOrTextField;
                ctx.image = closeImage;
                ctx.actionGroup = closeActionGroup;
                ctx.textSourceGroup = closeTextSourceGroup;
                ctx.textOrJsonTextField = closeTextOrJsonTextField;
            }
        }

        ctx.elementPanel = new JPanel();
        ctx.elementPanel.setLayout(new BoxLayout(ctx.elementPanel, BoxLayout.Y_AXIS));
        ctx.elementPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ctx.hideOnTogglePanel = new JPanel();
        ctx.hideOnTogglePanel.setLayout(new BoxLayout(ctx.hideOnTogglePanel, BoxLayout.Y_AXIS));
        ctx.hideOnTogglePanel.setBorder(BorderFactory.createTitledBorder(ctx.title));

        ctx.hideOnNonePanel = new JPanel();
        ctx.hideOnNonePanel.setLayout(new BoxLayout(ctx.hideOnNonePanel, BoxLayout.Y_AXIS));

        return ctx;
    }

    private JButton createToggleButton(String title, JPanel elementPanel, JPanel togglePanel) {
        JButton toggleButton = new JButton("▶ " + title);
        toggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, toggleButton.getPreferredSize().height));
        toggleButton.setPreferredSize(new Dimension(0, 30));
        toggleButton.addActionListener(_ -> {
            boolean visible = !togglePanel.isVisible();
            togglePanel.setVisible(visible);
            toggleButton.setText((visible ? "▼ " : "▶ ") + title);
            elementPanel.revalidate();
        });
        return toggleButton;
    }

    private JPanel createMatchTypePanel(StepElementType element, ButtonGroup group, DataSource selected, JPanel panel, JPanel showOnMatch) {
        JPanel matchTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ActionListener visibilityUpdater = _ -> {
            boolean show = !Objects.equals(group.getSelection().getActionCommand(), DataSource.NONE.name());
            showOnMatch.setVisible(show);
            panel.revalidate();
            panel.repaint();
        };

        for (DataSource dataSource : DataSource.values()) {
            if (dataSource == DataSource.JSON || (dataSource == DataSource.NONE && element == StepElementType.PASS)) {
                continue;
            }
            JRadioButton button = new JRadioButton(dataSource.name());
            button.setActionCommand(dataSource.name());
            button.setSelected(dataSource == selected);
            button.addActionListener(visibilityUpdater);
            group.add(button);
            matchTypePanel.add(button);
        }

        if (element != StepElementType.PASS) {
            SwingUtilities.invokeLater(() -> visibilityUpdater.actionPerformed(null));
        }

        return matchTypePanel;
    }

    private void setupMatchInputSection(ElementContext ctx, StepElementType element) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(new JLabel("Image name or Text:"));
        ctx.hideOnNonePanel.add(labelPanel);

        if (!isNewStep && testStep.getStepElements().get(element) != null) {
            ctx.imageOrTextField.setText(testStep.getStepElements().get(element).getPath());
        }
        ctx.imageOrTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        ctx.hideOnNonePanel.add(ctx.imageOrTextField);
    }

    private void setupScreenshotSection(ElementContext ctx, StepElementType element) {
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

        if (!isNewStep && testStep.getStepElements().get(element) != null) {
            // Sikulix Robot.class can not be called in a swing awake event
            new Thread(() -> {
                BufferedImage img = ImageUtils.loadImage(testCaseName + File.separator + testStep.getStepElements().get(element).getPath());
                SwingUtilities.invokeLater(() -> {
                    ctx.image = img;
                    setImageToLabel(imageLabel, ctx.image);
                    switch (element){
                        case PASS -> passImage = ctx.image;
                        case PRECONDITION -> preconditionImage = ctx.image;
                        case FAIL -> failImage = ctx.image;
                        case RETRY -> retryImage = ctx.image;
                        case CLOSE -> closeImage = ctx.image;
                    }
                });
            }).start();
        }

        screenshotButton.addActionListener(_ -> new Screenshot(captured -> {
            setImageToLabel(imageLabel, captured);
            ctx.image = captured;
            switch (element) {
                case PRECONDITION -> preconditionImage = captured;
                case PASS -> passImage = captured;
                case FAIL -> failImage = captured;
                case RETRY -> retryImage = captured;
                case CLOSE -> closeImage = captured;
            }
        }));
    }

    private void setupActionPanel(ElementContext ctx, StepElementType element) {
        StepAction action = StepAction.FIND;
        DataSource textSource = DataSource.TEXT;
        if (!isNewStep && testStep.getStepElements().get(element) != null) {
            action = testStep.getStepElements().get(element).getAction();
            textSource = testStep.getStepElements().get(element).getTextDataSource();
        }
        JPanel actionPanel = actionTypePanel(action, ctx.actionGroup, textSource, ctx.textSourceGroup, ctx.textOrJsonTextField);
        ctx.hideOnNonePanel.add(actionPanel);
    }

    private static class ElementContext {
        String title;
        DataSource matchSelected;
        BufferedImage image;
        ButtonGroup matchTypeGroup;
        JTextField imageOrTextField;
        ButtonGroup actionGroup;
        ButtonGroup textSourceGroup;
        JTextField textOrJsonTextField;
        JPanel elementPanel;
        JPanel hideOnTogglePanel;
        JPanel hideOnNonePanel;
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
            callback.onSubmit(buildTestStep());
            dispose();
        });
        panel.add(saveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> {
            dispose();
            throw new OperationCancelException();});
        panel.add(cancelButton);
        return panel;
    }

    private TestStep buildTestStep(){
        testStep.setName(stepNameTextField.getText());
        testStep.setDescription(descriptionTextField.getText());
        testStep.setRegion(buildRegion());
        Arrays.stream(StepElementType.values()).forEach(stepElementType ->
        {
            switch (stepElementType) {
                case PASS -> testStep.setPassElement(buildPassElement());
                case PRECONDITION -> testStep.setPreconditionElement(buildPreconditionElement());
                case FAIL -> testStep.setFailElement(buildFailElement());
                case RETRY -> testStep.setRetryElement(buildRetryElement());
                case CLOSE -> testStep.setCloseElement(buildCloseElement());
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

    private StepElement buildPassElement() {
        ElementContext context = new ElementContext();
        context.matchTypeGroup = passMatchTypeGroup;
        context.imageOrTextField = passImageOrTextField;
        context.image = passImage;
        context.actionGroup = passActionGroup;
        context.textSourceGroup = passTextSourceGroup;
        context.textOrJsonTextField = passTextOrJsonTextField;
        return buildElement(context);
    }

    private StepElement buildPreconditionElement() {
        ElementContext context = new ElementContext();
        context.matchTypeGroup = preconditionMatchTypeGroup;
        context.imageOrTextField = preconditionImageOrTextField;
        context.image = preconditionImage;
        context.actionGroup = preconditionActionGroup;
        context.textSourceGroup = preconditionTextSourceGroup;
        context.textOrJsonTextField = preconditionTextOrJsonTextField;
        return buildElement(context);
    }

    private StepElement buildFailElement() {
        ElementContext context = new ElementContext();
        context.matchTypeGroup = failMatchTypeGroup;
        context.imageOrTextField = failImageOrTextField;
        context.image = failImage;
        context.actionGroup = failActionGroup;
        context.textSourceGroup = failTextSourceGroup;
        context.textOrJsonTextField = failTextOrJsonTextField;
        return buildElement(context);
    }

    private StepElement buildRetryElement() {
        ElementContext context = new ElementContext();
        context.matchTypeGroup = retryMatchTypeGroup;
        context.imageOrTextField = retryImageOrTextField;
        context.image = retryImage;
        context.actionGroup = retryActionGroup;
        context.textSourceGroup = retryTextSourceGroup;
        context.textOrJsonTextField = retryTextOrJsonTextField;
        return buildElement(context);
    }

    private StepElement buildCloseElement() {
        ElementContext context = new ElementContext();
        context.matchTypeGroup = closeMatchTypeGroup;
        context.imageOrTextField = closeImageOrTextField;
        context.image = closeImage;
        context.actionGroup = closeActionGroup;
        context.textSourceGroup = closeTextSourceGroup;
        context.textOrJsonTextField = closeTextOrJsonTextField;
        return buildElement(context);
    }

    private StepElement buildElement(ElementContext context) {
        StepElement element = new StepElement();
        switch (DataSource.valueOf(context.matchTypeGroup.getSelection().getActionCommand())){
            case IMAGE: element.setDataSource(DataSource.IMAGE);
                try {
                    ImageUtils.saveImage(context.image, testCaseName + File.separator + context.imageOrTextField.getText() + ".PNG");
                } catch (IOException e) {
                    throw new ImageIOException("Cannot write image to target path");
                }
                break;
            case TEXT: element.setDataSource(DataSource.TEXT);
                break;
            default: return null;
        }
        element.setPath(context.imageOrTextField.getText() + ".PNG");
        switch (StepAction.valueOf(context.actionGroup.getSelection().getActionCommand())){
            case FIND : element.setAction(StepAction.FIND);
                break;
            case CLICK : element.setAction(StepAction.CLICK);
                break;
            case TYPE : element.setAction(StepAction.TYPE);
                element.setTextDataSource(DataSource.valueOf(context.textSourceGroup.getSelection().getActionCommand()));
                element.setOutputText(context.textOrJsonTextField.getText());
                break;
            case PASTE: element.setAction(StepAction.PASTE);
                element.setTextDataSource(DataSource.valueOf(context.textSourceGroup.getSelection().getActionCommand()));
                element.setOutputText(context.textOrJsonTextField.getText());
                break;
        }
        return element;
    }

}
