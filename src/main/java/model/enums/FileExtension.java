package model.enums;

public enum FileExtension {
    JSON(".json"),
    CSV(".csv"),
    PNG(".png");

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }
    public String getExtension() {
        return extension;
    }
}
