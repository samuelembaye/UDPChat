package Föreläsning4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class filehandling {
    double temp;
    double mintemp = Double.MAX_VALUE;
    double maxtemp = -Double.MAX_VALUE;
    double sumtemp = 0;
    List<Double> list = new ArrayList<Double>();


    public filehandling() {
        String line;
        try (BufferedReader buffin = new BufferedReader(
                new FileReader("src\\Föreläsning4\\FileHandling\\temp.txt"));) {
            while ((line = buffin.readLine()) != null) {
                temp = Double.parseDouble(line.trim());
                list.add(temp);

//                if (temp > maxtemp) {
//                    maxtemp = temp;
//                    list.add(temp);
//                    sumtemp += temp;
//
//                } else if (temp < mintemp) {
//                    mintemp = temp;
//                    list.add(temp);
//                    sumtemp += temp;
//                } else {
//                    list.add(temp);
//                    sumtemp += temp;
//                }
            }

//            System.out.println("Max Temp: " + maxtemp);
//            System.out.println("Min Temp: " + mintemp);
//            System.out.println("Average: " + sumtemp/list.size());

            System.out.println("Max Temp: " + list.stream().mapToDouble(d -> d).max().getAsDouble());
            System.out.println("Min Temp: " + list.stream().mapToDouble(d -> d).min().getAsDouble());
            System.out.println("Average: " +  list.stream().mapToDouble(d -> d).average().getAsDouble());

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("IO Error");
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        new filehandling();

    }
}
