package interfaces;

import java.awt.image.BufferedImage;

/**
 * @author Sin
 */

@FunctionalInterface
public interface ScreenshotCallback {
        void onScreenshot(BufferedImage image);
}
