package utils;

import exceptions.TargetNotFoundException;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.awt.image.BufferedImage;

public class SikulixUtils {
    public static final Screen screen = new Screen();
    static{
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }
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

    public static void clickOnImageMatchOrThrow(Region region, String imagePath, int timeoutSec){
        try {
            region.wait(imagePath, timeoutSec).click();
        } catch (FindFailed e) {
            throw new TargetNotFoundException("Target image is not found in target area!");
        }
    }

    public static void clickOnImageSimilarOrThrow(Region region, String imagePath, double tolerance, int timeoutSec){
        try {
            region.wait(new Pattern(imagePath).similar(tolerance), timeoutSec).click();
        } catch (FindFailed e) {
            throw new TargetNotFoundException("Target image is has no match in target area!");
        }
    }

    public static String getTextFromRegion(Region region){
        return region.text();
    }

    public static void clickIfTextFoundOrThrow(Region region, String text, int timeoutSec){
        try {
            region.waitText(text, timeoutSec).click();
        } catch (FindFailed e) {
            throw new TargetNotFoundException("Text is not found in target area!");
        }
    }

    public static void setImagePath(String path){
        ImagePath.setBundlePath(path);
    }

    public static void addImagePath(String path){
        ImagePath.add(path);
    }

    public static void printSelectedRegionLocation(){
        Region region = screen.selectRegion();
        System.out.printf("Region selected: X: %s - Y: %s - Width: %s - Height: %s",
                region.getX(), region.getY(), region.getW(), region.getH());
    }

}
