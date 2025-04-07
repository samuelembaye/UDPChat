package Greenest;

public class WateringException extends GreenestException {
    public WateringException(String plantName, String details) {
        super("Failed to water plant '" + plantName + "': " + details);
    }
}
