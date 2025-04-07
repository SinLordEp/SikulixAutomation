package util;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SwingUtils {

    private SwingUtils() {}
    public static void makeTextFieldIntegerOnly(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    public static void makeTextFieldDecimalAndNonEmpty(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            private static final String REGEX = "\\d+(\\.\\d+)?";

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.insert(offset, string);

                if (!sb.toString().isEmpty() && sb.toString().matches(REGEX)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.replace(offset, offset + length, text);

                if (!sb.toString().isEmpty() && sb.toString().matches(REGEX)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.delete(offset, offset + length);

                if (!sb.toString().isEmpty() && sb.toString().matches(REGEX)) {
                    super.remove(fb, offset, length);
                }
            }
        });
    }

    public static void makeTextFieldIntegerWithMax(JTextField textField, int maxValue) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            private String oldValue;

            @Override
            public void focusGained(FocusEvent e) {
                oldValue = textField.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                validateAndRollback();
            }

            private void validateAndRollback() {
                String text = textField.getText();
                try {
                    if (text.isEmpty()) {
                        throw new NumberFormatException("Cannot be empty");
                    }
                    int value = Integer.parseInt(text);
                    if (value < 0) {
                        textField.setText(oldValue);
                    }
                    if(value > maxValue){
                        textField.setText(String.valueOf(maxValue));
                    }
                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(() -> {
                        textField.setText(oldValue);
                        DialogUtils.showWarningDialog(null, "Number format error", "Input cannot be empty");
                    });
                }
            }
        });
    }



}
