package Greenest;

public enum vätskaTyp {
    PROTEINDRYCK("Proteindryck"),
    MINERALVATTEN("Mineralvatten"),
    KRANVATTEN("Kranvatten");

    private final String displayName;

    vätskaTyp(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}