package util;

import config.GlobalPaths;
import exception.ImageIOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
        Path targetPath = GlobalPaths.IMAGE_ROOT.resolve(path);
        Files.createDirectories(targetPath.getParent());
        ImageIO.write(image,"PNG", GlobalPaths.IMAGE_ROOT.resolve(path).toFile());
    }

    public static BufferedImage loadIcon(){
        try {
            URL url = ImageUtils.class.getResource("/icons/AqsAutomationFlow-64x.png");
            if(url == null){throw new ImageIOException("Cannot find icon file");}
            return ImageIO.read(url.openStream());
        } catch (IOException e) {
            throw new ImageIOException("Cannot read icon file");
        }
    }
}
