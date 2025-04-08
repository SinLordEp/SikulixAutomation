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

    public void captureWindow(String windowName, int width, int height, Callback<EventPackage> callback){
        if(windowName.isEmpty() || windowName.equals("No window is selected, please select target window!")){
            return;
        }
        WinDef.HWND window = JNAUtils.getWindowByTitle(windowName);
        if(window == null){
            throw new WindowErrorException("Can not find windows with name: " + windowName);
        }
        JNAUtils.bringWindowToFront(window);
        JNAUtils.setWindowSize(window, width, height);
        JNAUtils.setWindowAtLocation(window, 0, 0);
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
