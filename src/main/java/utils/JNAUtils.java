package utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import exceptions.WindowErrorException;
import org.sikuli.script.Region;

public class JNAUtils {

    private JNAUtils(){}

    public static WinDef.HWND getWindowByPreciseTitle(String title){
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, title);
        if (hwnd != null) {
            return hwnd;
        } else {
            throw new WindowErrorException("No Window with precise title is found");
        }
    }

    //TODO
    public static WinDef.HWND getWindowByNameMatching(String partialTitle){

        return null;
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

}
