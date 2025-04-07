package Greenest;

public class InvalidPlantDataException extends GreenestException {
    public InvalidPlantDataException(String field, Object value) {
        super("Invalid plant data: " + field + " cannot be " + value);
    }
}
