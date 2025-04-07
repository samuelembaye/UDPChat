package Föreläsning4.FilFelHantering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.*;
import static java.lang.Integer.parseInt;

//import static java.lang.System.out;

public class FilFelHantering {
    String line;
    List<String> data = new  ArrayList<>();
    int i = 0;
    int j = 0;
    Path filePath = Paths.get("src\\Föreläsning4\\FilFelHantering\\personuppgifter.txt");
    Path merrän2Path = Paths.get("src\\Föreläsning4\\FilFelHantering\\merän2.txt");
    Path mindreän2Path = Paths.get("src\\Föreläsning4\\FilFelHantering\\mindreän2.txt");

    public FilFelHantering() {
        String line;
        try (BufferedReader buffin = new BufferedReader(
                new FileReader(filePath.toString()))) {

                while ((line = buffin.readLine()) != null) {
                    data.add(line);
                }

            } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("IO Error");
            throw new RuntimeException(e);
        }
        control(data);


    }

    private void control(List<String> data) {
        System.out.println("control");
        List<String> data2 = new  ArrayList<>();
        String tempupp;
        String tempupp2;
        String[] temparr;
        for (int j = 0; j < data.size(); j=j+2) {
            tempupp = data.get(j).trim();
            tempupp2 = data.get(j+1).trim();
            temparr = tempupp2.split(",");
//            System.out.println(temparr);
            if(parseInt(temparr[2]) >= 200) {
                System.out.println(temparr[2]);
            }




        }
    }


}
