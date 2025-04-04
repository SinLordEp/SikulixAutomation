package executable;

import com.formdev.flatlaf.FlatLightLaf;
import controller.ToolController;

import javax.swing.*;

/**
 * @author Sin
 */
public class ATPrototype {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Cannot setup FlatLaf");
        }
        new ToolController().run();
    }

}
