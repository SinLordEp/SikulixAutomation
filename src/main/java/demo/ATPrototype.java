package demo;

import com.sun.jna.platform.win32.WinDef;
import utils.JNAUtils;

public class ATPrototype {
    public static void main(String[] args) {
        new DemoGUI().run();
        new ToolGUI();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WinDef.HWND window = JNAUtils.getWindowByTitle("Demo GUI");
        JNAUtils.setWindowAtLocation(window, 0, 0);
    }

}
