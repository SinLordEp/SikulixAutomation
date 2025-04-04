package config;

import java.nio.file.Path;

import static executable.ATLauncher.getCurrentParentPath;

public class GlobalPaths {
    public static final Path BASE_ROOT = getCurrentParentPath();
    public static final Path IMAGE_ROOT = BASE_ROOT.resolve("image");

    private GlobalPaths() {}
}
