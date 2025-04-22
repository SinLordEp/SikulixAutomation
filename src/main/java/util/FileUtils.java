package util;

import config.GlobalPaths;
import exception.OperationCancelException;
import model.enums.FileExtension;
import model.enums.FileOperation;

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

    public static String getPath(JFrame parent, FileOperation operation, FileExtension extension){
        JFileChooser fileChooser = new JFileChooser(GlobalPaths.BASE_ROOT.toFile());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(extension == FileExtension.JSON){
            fileChooser.setSelectedFile(new File("New config" + extension.getExtension()));
        }else{
            fileChooser.setSelectedFile(new File("New test result" + extension.getExtension()));
        }
        int result = operation == FileOperation.OPEN ? fileChooser.showOpenDialog(parent) : fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }else {
            throw new OperationCancelException();
        }
    }

}
