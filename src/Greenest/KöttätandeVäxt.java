package Greenest;

public class KöttätandeVäxt extends Växt{
    private double längd;

    public KöttätandeVäxt(String namn, double längd) throws InvalidPlantDataException {
        super(namn, vätskaTyp.PROTEINDRYCK);
        if (längd <= 0){
            throw new InvalidPlantDataException("length", längd);
        }
        this.längd = längd;
    }

    @Override
    public String matinfo() {
        return "Köttätande växten "+namn + " "+ (0.1d+(0.2d*längd)) +" "+ vätska;
    }

    @Override
    public String toString() {
        return "KöttätandeVäxt{" +
                "längd=" + längd +
                ", namn='" + namn + '\'' +
                ", vätska='" + vätska + '\'' +
                '}';
    }
}