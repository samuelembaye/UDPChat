package Föreläsning6.Multiuser.server;

import Föreläsning6.Multiuser.Util.MultiUserKompis;

import java.util.ArrayList;

public class MultiUserDatabase {

    ArrayList<MultiUserKompis> kompisList = new ArrayList<MultiUserKompis>();

    public MultiUserDatabase() {

        kompisList.add(new MultiUserKompis("Ragnar","stockholmvägen", "0711111111"));
        kompisList.add(new MultiUserKompis("John", "Telivägen", "0722222222"));
        kompisList.add(new MultiUserKompis("Ruth","sveavägen", "0733333333"));

    }


    public String getKompisData(String s) {

        for (MultiUserKompis c : kompisList) {
            if (c.getName().equalsIgnoreCase(s)) {
                return c.toString();
            }
        }
        return null;
    }

    public MultiUserKompis getKompisObject(String s) {
        for (MultiUserKompis c : kompisList) {
            if (c.getName().equalsIgnoreCase(s)) {
                return c;
            }
        }
        return null;
    }
}
