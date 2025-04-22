package util;

import exception.OperationCancelException;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {
    private DialogUtils() {}

    public static int showConfirmDialog(Component parent, String title, String message) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarningDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static String showInputDialog(Component parent, String title, String message) {
        String input = JOptionPane.showInputDialog(parent, message, title, JOptionPane.PLAIN_MESSAGE);
        if(input == null){
            throw new OperationCancelException();
        }
        return input;
    }
}
