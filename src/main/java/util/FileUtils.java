package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private FileUtils() {}

    public static void renameFolder(Path sourceFolder, Path targetFolder) throws IOException {
        Files.move(sourceFolder, targetFolder);
    }

}
