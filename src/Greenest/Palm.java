package Greenest;

public class Palm extends Växt{

    double längd;

    public Palm(String namn, double längd) throws InvalidPlantDataException {
        super(namn, vätskaTyp.KRANVATTEN);
        if (längd <= 0) {
            throw new InvalidPlantDataException("length", längd);
        }
        this.längd = längd;
    }

    @Override
    public String matinfo() {
        return "Palmen "+namn + " "+ 0.5*längd +" "+ vätska;
    }

    @Override
    public String toString() {
        return "Palm{" +
                "längd=" + längd +
                ", namn='" + namn + '\'' +
                ", vätska='" + vätska + '\'' +
                '}';
    }
}




