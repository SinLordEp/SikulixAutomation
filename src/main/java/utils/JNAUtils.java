package utils;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.sikuli.script.Region;

public class JNAUtils {
    public static final WinDef.HWND HWND_TOPMOST = new WinDef.HWND(Pointer.createConstant(-1));
    public static final WinDef.HWND HWND_NOTOPMOST = new WinDef.HWND(Pointer.createConstant(-2));

    private JNAUtils(){}

    public static WinDef.HWND getWindowByTitle(String title){
        return User32.INSTANCE.FindWindow(null, title);
    }

    public static int getWindowCurrentWidth(WinDef.HWND window){
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, rect);
        return rect.right - rect.left;
    }

    public static int getWindowCurrentHeight(WinDef.HWND window){
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, rect);
        return rect.bottom - rect.top;
    }

    public static int getWindowCurrentX(WinDef.HWND window){
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, rect);
        return rect.left;
    }

    public static int getWindowCurrentY(WinDef.HWND window){
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, rect);
        return rect.top;
    }

    public static void setWindowSize(WinDef.HWND window, int width, int height){
        User32.INSTANCE.MoveWindow(window, getWindowCurrentX(window), getWindowCurrentY(window), width, height, true);
    }

    public static void setWindowAtLocation(WinDef.HWND window, int x, int y){
        User32.INSTANCE.MoveWindow(window, x, y, getWindowCurrentWidth(window), getWindowCurrentHeight(window), true);
    }

    public static Region convertWindowToRegion(WinDef.HWND window){
        return new Region(getWindowCurrentX(window), getWindowCurrentY(window), getWindowCurrentWidth(window), getWindowCurrentHeight(window));
    }

    public static void bringWindowToFront(WinDef.HWND window){
        User32 user32 = User32.INSTANCE;
        user32.ShowWindow(window, WinUser.SW_RESTORE);
        user32.SetForegroundWindow(window);
    }

    public static void setWindowAlwaysOnTop(WinDef.HWND window, boolean alwaysOnTop){
        User32 user32 = User32.INSTANCE;
        int flags = WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE;
        WinDef.HWND insertAfter = alwaysOnTop ? HWND_TOPMOST : HWND_NOTOPMOST;
        user32.SetWindowPos(window, insertAfter, 0, 0, 0, 0, flags);
    }

}
