package util;

import config.GlobalPaths;
import exception.ImageIOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageUtils {

    private ImageUtils() {}

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(GlobalPaths.IMAGE_ROOT.resolve(path).toFile());
        } catch (Exception e) {
            throw new ImageIOException("Cannot read file - Path: " + GlobalPaths.IMAGE_ROOT.resolve(path));
        }
    }

    public static void saveImage(BufferedImage image, String path) throws IOException {
        ImageIO.write(image,"PNG", GlobalPaths.IMAGE_ROOT.resolve(path).toFile());
    }
}
