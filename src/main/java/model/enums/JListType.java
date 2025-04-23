package model.enums;

public enum JListType {
    CATEGORY("category"),
    CASE("case"),
    STEP("step");

    private final String value;

    JListType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
