package utils;

import exceptions.UndefinedException;
import model.StepAction;
import model.StepElement;
import model.StepState;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class SikulixUtils {
    public static final Screen screen = new Screen();
    private static final String imageRoot = "image/";
    static{
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }

    @FunctionalInterface
    interface FindFailedReturnBool {
        void run() throws FindFailed;
    }

    private SikulixUtils(){}



    private static StepState findImage(StepElement element){
        return handleFindFailed(() -> element.getRegion().wait(new Pattern(element.getImagePath()).similar(element.getSimilarity()), element.getTimeoutSec()));
    }

    public static boolean compareImagesByPixel(BufferedImage firstImage, BufferedImage secondImage){
        if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) return false;
        for (int y = 0; y < firstImage.getHeight(); y++) {
            for (int x = 0; x < firstImage.getWidth(); x++) {
                if (firstImage.getRGB(x, y) != secondImage.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    private static StepState clickOnFound(StepElement element){
        return handleFindFailed(() -> element.getRegion().wait(new Pattern(element.getImagePath()).similar(element.getSimilarity()), element.getTimeoutSec()).click());
    }

    private static StepState clickOnText(StepElement element){
        return handleFindFailed(() -> element.getRegion().waitText(element.getOutputText(), element.getTimeoutSec()).click());
    }

    private static StepState clickAndText(StepElement element){
        return handleFindFailed(() ->{
            clickOnFound(element);
            if (Objects.requireNonNull(element.getAction()) == StepAction.TYPE) {
                element.getRegion().type(element.getOutputText());
            } else if (element.getAction() == StepAction.PASTE) {
                element.getRegion().paste(element.getOutputText());
            }
            if(element.isEnterKey()){
                element.getRegion().type(Key.ENTER);
            }
        });
    }

    public static void setImagePath(String folderName){
        ImagePath.reset();
        ImagePath.add(imageRoot + "universal");
        ImagePath.add(imageRoot + folderName + "/");
    }

    public static String getImagePath(){
        return ImagePath.getBundlePath();
    }

    public static void addImagePath(String path){
        ImagePath.add(path);
    }

    private static StepState handleFindFailed(FindFailedReturnBool function){
        try{
            function.run();
            return StepState.MATCHED;
        }catch (FindFailed e){
            return StepState.NO_MATCH;
        }catch(Exception e){
            throw new UndefinedException("Undefined exception was threw in Sikulix matching process");
        }
    }

}
