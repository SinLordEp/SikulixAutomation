package service;

import com.sun.jna.platform.win32.WinDef;
import exception.WindowErrorException;
import model.EventPackage;
import util.Callback;
import util.JNAUtils;

public class WindowService {
    private final Callback<EventPackage> callback;

    public WindowService(Callback<EventPackage> callback) {
        this.callback = callback;
    }

    public void captureWindow(){
        String windowName = "Servidor Tienda [Tienda PRE ] CastorTPV v@VERSION@";
        WinDef.HWND window = JNAUtils.getWindowByTitle(windowName);
        if(window == null){
            throw new WindowErrorException("Can not find windows with name: " + windowName);
        }
        JNAUtils.setWindowSize(window, 1400,1000);
        JNAUtils.setWindowAtLocation(window, 0, 0);
        JNAUtils.bringWindowToFront(window);
        JNAUtils.setWindowAlwaysOnTop(window, true);
    }

    public void unsetWindowAlwaysOnTop(){
        String windowName = "Servidor Tienda [Tienda PRE ] CastorTPV v@VERSION@";
        WinDef.HWND window = JNAUtils.getWindowByTitle(windowName);
        if(window == null){
            throw new WindowErrorException("Can not find windows with name: " + windowName);
        }
        JNAUtils.setWindowAlwaysOnTop(window, false);
    }
}
