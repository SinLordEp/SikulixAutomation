package service;

import com.sun.jna.platform.win32.WinDef;
import exception.WindowErrorException;
import interfaces.Callback;
import model.EventPackage;
import model.enums.EventCommand;
import util.JNAUtils;

public class WindowService {

    public WindowService() {
        // No parameter needed for now
    }

    public void captureWindow(Callback<EventPackage> callback){
        String windowName = "Servidor Tienda [Tienda PRE ] CastorTPV v@VERSION@";
        WinDef.HWND window = JNAUtils.getWindowByTitle(windowName);
        if(window == null){
            throw new WindowErrorException("Can not find windows with name: " + windowName);
        }
        JNAUtils.setWindowSize(window, 1400,1000);
        JNAUtils.setWindowAtLocation(window, 0, 0);
        JNAUtils.bringWindowToFront(window);
        JNAUtils.setWindowAlwaysOnTop(window, true);
        callback.onSubmit(new EventPackage(EventCommand.WINDOW_CAPTURED));
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
