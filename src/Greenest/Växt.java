package Greenest;

public abstract class Växt {
    protected String namn;
    protected vätskaTyp vätska;

    public Växt(String namn, vätskaTyp vätska) {
        this.namn = namn;
        this.vätska = vätska;
    }

    public String getNamn() {
        return namn;
    }

    public abstract String matinfo();

}
