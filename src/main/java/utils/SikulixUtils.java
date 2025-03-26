package utils;

import exceptions.ImageNotFoundException;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;

import java.awt.image.BufferedImage;

public class SikulixUtils {

    private SikulixUtils(){}

    public static boolean compareRegionBeforeAfterRunnable(Runnable runnable, Region targetRegion){
        BufferedImage imageBefore = targetRegion.getScreen().capture().getImage();
        runnable.run();
        BufferedImage imageAfter = targetRegion.getScreen().capture().getImage();
        return compareImagesByPixel(imageBefore, imageAfter);
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

    public static boolean compareTwoRegions(Region firstRegion, Region secondRegion){
        return compareImagesByPixel(firstRegion.getScreen().capture().getImage(), secondRegion.getScreen().capture().getImage());
    }

    public static void clickOnImageMatchOrThrow(Region targetRegion, String imagePath, int timeoutSec){
        try {
            targetRegion.wait(imagePath, timeoutSec).click();
        } catch (FindFailed e) {
            throw new ImageNotFoundException("Target image is not found at target area!");
        }
    }

    public static void clickOnImageSimilarOrThrow(Region targetRegion, String imagePath, double tolerance, int timeoutSec){
        try {
            targetRegion.wait(new Pattern(imagePath).similar(tolerance), timeoutSec).click();
        } catch (FindFailed e) {
            throw new ImageNotFoundException("Target image is has no match at target area!");
        }
    }
}
