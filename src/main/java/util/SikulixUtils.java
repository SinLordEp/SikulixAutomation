package util;

import config.GlobalPaths;
import exception.ImageIOException;
import exception.TestInterruptException;
import model.enums.StepAction;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sin
 */
public class SikulixUtils {
    public static final Screen SCREEN = new Screen();
    static{
        ImagePath.setBundlePath(GlobalPaths.IMAGE_ROOT.toString());
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }

    private SikulixUtils(){}

    public static void findImage(Region region, String imagePath, double similarity, int timeoutSec) throws FindFailed {
        region.wait(new Pattern(imagePath).similar(similarity), timeoutSec);
    }

    public static void clickOnFound(Region region, String imagePath, double similarity, int timeoutSec, int clickTime) throws FindFailed {
        Region foundRegion = region.wait(new Pattern(imagePath).similar(similarity), timeoutSec);
        for(int i = 0; i < clickTime; i++){
            foundRegion.click();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new TestInterruptException("Failed to click on image at the \"%s\" time".formatted(i));
            }
        }
    }

    public static void clickOnText(Region region, String text, int timeoutSec) throws FindFailed {
        region.waitText(text, timeoutSec).click();
    }

    public static void clickAndText(Region region, String imagePath, double similarity, int timeoutSec, int clickTime, String text, StepAction action, boolean enterKey) throws FindFailed {
        clickOnFound(region, imagePath, similarity, timeoutSec, clickTime);
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
        ImagePath.add(GlobalPaths.IMAGE_ROOT.resolve("universal").toString());
        ImagePath.add(GlobalPaths.IMAGE_ROOT.resolve(folderName).toString());
    }

    public static void highlightRegion(Region region){
        region.highlight();
    }


}
