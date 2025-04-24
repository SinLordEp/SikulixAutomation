package executable;

import com.formdev.flatlaf.FlatLightLaf;
import controller.ToolController;
import org.apache.logging.log4j.core.config.Configurator;


import javax.swing.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Sin
 */
public class ATLauncher {

    public static void main(String[] args) {
        loadFlatLightLaf();
        loadLogger();
        new ToolController().run();
    }

    private static void loadFlatLightLaf() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Cannot setup FlatLaf");
        }
    }

    private static void loadLogger(){
        Configurator.initialize(null, "/resources/log4j2.xml");
    }

    public static Path getCurrentParentPath() {
        try {
            URI uri = ATLauncher.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
            Path path = Paths.get(uri);

            if (path.toString().endsWith(".jar")) {
                return path.getParent();
            } else {
                return Paths.get(System.getProperty("user.dir")).getParent();
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot get program parent path", e);
        }
    }
}
