package Greenest;

public class Kaktus extends Växt{
    private double längd;

    public Kaktus(String namn, double längd) throws InvalidPlantDataException {
        super(namn, vätskaTyp.MINERALVATTEN);
        if (längd <= 0) {
            throw new InvalidPlantDataException("length", längd);
        }
        this.längd = längd;
    }


    @Override
    public String matinfo() {
        return "Kaktusen "+namn + " ska ha 2 cl "+ vätska;
    }

    @Override
    public String toString() {
        return "Kaktus{" +
                "längd=" + längd +
                ", namn='" + namn + '\'' +
                ", vätska='" + vätska + '\'' +
                '}';
    }
}
