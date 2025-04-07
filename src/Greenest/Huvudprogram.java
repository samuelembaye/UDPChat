package Greenest;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Huvudprogram {
    private List<Växt> gäster;

    public Huvudprogram() {

        gäster = new LinkedList<>();
        try {
        //System.out.println(vätskaTyp.PROTEINDRYCK);
        // Initialize plants
        Kaktus igge = new Kaktus("Igge", 20);
        Palm laura = new Palm("Laura", 5);
        Palm olof = new Palm("Olof", 1);
        KöttätandeVäxt meatLoaf = new KöttätandeVäxt("Meatloaf", 0.7);
        gäster.add(laura);
        gäster.add(igge);
        gäster.add(olof);
        gäster.add(meatLoaf);
        } catch (InvalidPlantDataException e) {
            handleException(e);
        }


        while (true) {
            try {
                fedhelp();
                //feeder();
            } catch (GreenestException e) {
                handleException(e);
            } catch (Exception e) {
                handleException(new GreenestException("Unexpected error", e));
            }
        }
    }


    public void fedhelp() throws PlantNotFoundException, InvalidPlantDataException, WateringException {
        String namn = JOptionPane.showInputDialog("Vilken växt ska få mat?");

        if (namn == null || namn.trim().isEmpty()) {
            throw new InvalidPlantDataException("name", namn);
        }

        for (Växt växt : gäster) {
            if (namn.equalsIgnoreCase(växt.getNamn())) {
                try {
                    System.out.println(växt.matinfo());
                    return;
                } catch (Exception e) {
                    throw new WateringException(växt.getNamn(), e.getMessage());
                }
            }
        }

        throw new PlantNotFoundException(namn);
    }

    public void feeder() throws PlantNotFoundException, InvalidPlantDataException ,WateringException{
        System.out.println("Vilken växt ska få mat?");

        // List available plants (optional)
        System.out.println("Tillgängliga växter:");
        for (Växt växt : gäster) {
            System.out.println("- " + växt.getNamn());
        }

        Scanner scanner = new Scanner(System.in);
        String inputNamn = scanner.nextLine().trim();

        if (inputNamn.isEmpty()) {
            throw new InvalidPlantDataException("plant name", "empty string");
        }

        for (Växt växt : gäster) {
            if (inputNamn.equalsIgnoreCase(växt.getNamn())) {
                try {
                    System.out.println("\n" + växt.matinfo());
                    return;
                } catch (Exception e) {
                    throw new WateringException(växt.getNamn(), e.getMessage());
                }
            }
        }

        throw new PlantNotFoundException(inputNamn);
    }

    private void handleException(GreenestException e) {
        String message = e.getMessage();
        String title = "Error";
        int messageType = JOptionPane.ERROR_MESSAGE;

        if (e instanceof PlantNotFoundException) {
            title = "Plant Not Found";
            messageType = JOptionPane.WARNING_MESSAGE;
        } else if (e instanceof InvalidPlantDataException) {
            title = "Invalid Data";
        }

        JOptionPane.showMessageDialog(null, message, title, messageType);
        System.err.println("[" + title + "] " + message);
        if (e.getCause() != null) {
            e.getCause().printStackTrace();
        }
    }



    public static void main(String[] args) {
        Huvudprogram hp = new Huvudprogram();
    }
}