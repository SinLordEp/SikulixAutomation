package util;

import config.GlobalExtensions;
import config.GlobalPaths;
import exception.OperationCancelException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private FileUtils() {}

    public static void renameFolder(Path sourceFolder, Path targetFolder) throws IOException {
        Files.move(sourceFolder, targetFolder);
    }

    public static String getPath(String extension) {
        JFileChooser fileChooser = new JFileChooser(GlobalPaths.BASE_ROOT.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(extension.equals(GlobalExtensions.JSON_EXTENSION)){
            fileChooser.setSelectedFile(new File("New config" + extension));
        }else{
            fileChooser.setSelectedFile(new File("New test result" + extension));
        }
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }else {
            throw new OperationCancelException();
        }
    }

}
