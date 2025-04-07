package Uppgift2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class HuvudProgram {
    public  void removeSko(List<Sko> sko, Sko skoToRemove)  {
        if(sko.contains(skoToRemove)){
            sko.remove(skoToRemove);
        }else{
            System.out.println("Skoet finns inte i listan");
        }

    }
    public  void addSko(List<Sko> sko, Sko skoToAdd){
        if(!sko.contains(skoToAdd)){
            sko.add(skoToAdd);
        }
    }

    public  void updateColor(List<Sko> skoList,Sko sko, String färg){
        if(skoList.contains(sko)){
            for(Sko skor: skoList){
                if (sko.equals(sko))
                    sko.setFärg(färg);
            }
        }
        else{
            System.out.println("Skoet finns inte i listan");
        }

    }

    public static void main(String[] args) {
        HuvudProgram hp = new HuvudProgram();
        List<Sko> minaSkor = new ArrayList<>();
        Sko nikeAir = new Sko(42,"snikers","white");
        minaSkor.add(nikeAir);
        Sko adidas = new Sko(42,"adidas","black");
        minaSkor.add(adidas);
        Sko sporty = new Sko(42,"sporty","red");
        minaSkor.add(sporty);
        for(Sko sko: minaSkor){
            System.out.println(sko);
        }
        hp.removeSko(minaSkor, adidas);
        for(Sko sko: minaSkor){
            System.out.println(sko);
        }

        hp.updateColor(minaSkor,sporty,"blue");
        System.out.println(sporty);

    }
}
