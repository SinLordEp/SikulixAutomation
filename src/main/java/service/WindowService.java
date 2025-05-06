package service;

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
        JNAUtils.getWindowByTitle(windowName);
        if(!JNAUtils.isWindowExists()){
            throw new WindowErrorException("Can not find windows with name: " + windowName);
        }
        JNAUtils.bringWindowToFront();
        JNAUtils.setWindowSize(width, height);
        JNAUtils.setWindowAtLocation(0, 0);
        callback.onSubmit(new EventPackage(EventCommand.WINDOW_CAPTURED));
    }

}
