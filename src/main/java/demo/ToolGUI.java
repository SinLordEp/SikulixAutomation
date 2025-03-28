package demo;

import org.sikuli.script.Region;
import utils.SikulixUtils;

import javax.swing.*;
import java.awt.*;


public class ToolGUI {
    private final JTextField textFieldRegionInfo = new JTextField();
    private final JPanel mainPanel;
    private Region screenshotRegion;

    public ToolGUI(){
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelText = new JPanel(new GridLayout(3, 1, 10, 0));
        panelText.add(new JLabel("Screenshot Region info:"));
        panelText.add(textFieldRegionInfo);
        panelText.add(new JLabel("File name:"));
        JTextField textFiledFileName = new JTextField();
        panelText.add(textFiledFileName);
        mainPanel.add(panelText, BorderLayout.NORTH);

        JPanel panelButton = new JPanel(new GridLayout(1, 2, 10, 0));
        panelButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton buttonScreenshot = new JButton("Screenshot");
        JButton buttonSaveImage = new JButton("Save Image");
        panelButton.add(buttonScreenshot);
        panelButton.add(buttonSaveImage);
        buttonScreenshot.addActionListener(_ -> screenshot());
        buttonSaveImage.addActionListener(_ -> saveImage());
        mainPanel.add(panelButton, BorderLayout.SOUTH);
    }

    public void run(){
        JFrame frame = new JFrame("Demo GUI");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void screenshot(){

    }

    private void saveImage(){
        if(screenshotRegion == null){
            return;
        }
        screenshotRegion.getScreen().capture().save(SikulixUtils.getImagePath(), "screenshot.png");

    }

}
