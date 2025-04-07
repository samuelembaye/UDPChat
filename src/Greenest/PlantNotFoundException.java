package Greenest;

// Specific exceptions
public class PlantNotFoundException extends GreenestException {
    public PlantNotFoundException(String plantName) {
        super("Plant '" + plantName + "' not found in the registry");
    }
}
