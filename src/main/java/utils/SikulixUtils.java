package utils;

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
}
