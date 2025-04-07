package Uppgift2;

public class Sko {
   private int storlek;
   String typAvSko;
   String färg;

   public Sko(){}

    public Sko(int storlek, String typAvSko, String färg) {
        this.storlek = storlek;
        this.typAvSko = typAvSko;
        this.färg = färg;
    }

    public int getStorlek() {
        return storlek;
    }

    public void setStorlek(int storlek) {
        this.storlek = storlek;
    }

    public String getTypAvSko() {
        return typAvSko;
    }

    public void setTypAvSko(String typAvSko) {
        this.typAvSko = typAvSko;
    }

    public String getFärg() {
        return färg;
    }

    public void setFärg(String färg) {
        this.färg = färg;
    }

    @Override
    public String toString() {
        return "Sko{" +
                "storlek=" + storlek +
                ", typAvSko='" + typAvSko + '\'' +
                ", färg='" + färg + '\'' +
                '}';
    }
}
