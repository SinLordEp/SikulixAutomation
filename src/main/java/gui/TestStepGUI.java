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
                ? DataSource.NONE
                : testStep.getStepElements().get(element).getDataSource();

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

        for (DataSource ds : DataSource.values()) {
            if (ds == DataSource.JSON || (ds == DataSource.NONE && element == StepElementType.PASS)) continue;
            JRadioButton button = new JRadioButton(ds.name());
            button.setActionCommand(ds.name());
            button.setSelected(ds == selected);
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
        ctx.hideOnNonePanel.add(imageLabel);

        if (!isNewStep && testStep.getStepElements().get(element) != null) {
            ctx.image = SikulixUtils.loadImage(testStep.getName() + "/" + testStep.getStepElements().get(element).getPath());
            setImageToLabel(imageLabel, ctx.image);
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

    private JPanel bottomButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.add(new JButton("Save Step"));
        panel.add(new JButton("Cancel"));
        return panel;
    }
}
