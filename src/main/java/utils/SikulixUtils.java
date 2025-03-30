package utils;

import model.StepAction;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.awt.image.BufferedImage;

/**
 * @author Sin
 */
public class SikulixUtils {
    public static final Screen SCREEN = new Screen();
    private static final String IMAGE_ROOT = "image/";
    static{
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }

    private SikulixUtils(){}

    public static void findImage(Region region, String imagePath, double similarity, int timeoutSec) throws FindFailed {
        region.wait(new Pattern(imagePath).similar(similarity), timeoutSec);
    }

    public static void clickOnFound(Region region, String imagePath, double similarity, int timeoutSec) throws FindFailed {
        region.wait(new Pattern(imagePath).similar(similarity), timeoutSec).click();
    }

    public static void clickOnText(Region region, String text, int timeoutSec) throws FindFailed {
        region.waitText(text, timeoutSec).click();
    }

    public static void clickAndText(Region region, String imagePath, double similarity, int timeoutSec, String text, StepAction action, boolean enterKey) throws FindFailed {
        clickOnFound(region, imagePath, similarity, timeoutSec);
        if(action == StepAction.TYPE){
            region.type(text);
        } else if(action == StepAction.PASTE){
            region.paste(text);
        }
        if(enterKey){
            region.type(Key.ENTER);
        }
    }

    public static boolean compareImagesByPixel(BufferedImage firstImage, BufferedImage secondImage){
        if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) {
            return false;
        }
        for (int y = 0; y < firstImage.getHeight(); y++) {
            for (int x = 0; x < firstImage.getWidth(); x++) {
                if (firstImage.getRGB(x, y) != secondImage.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void setImagePath(String folderName){
        ImagePath.reset();
        ImagePath.add(IMAGE_ROOT + "universal/");
        ImagePath.add(IMAGE_ROOT + folderName + "/");
    }

    public static String getImagePath(){
        return ImagePath.getBundlePath();
    }

    public static void addImagePath(String path){
        ImagePath.add(path);
    }



}
