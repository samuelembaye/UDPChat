package Föreläsning4.Threading;

public class Huvudprogram {

    public static void main(String[] args) throws InterruptedException {
        MedicinTimer  medicine1 = new MedicinTimer("Alvidon", 10000);
        MedicinTimer  medicine2 = new MedicinTimer("Ephedrine", 7000);
        medicine1.start();
        medicine2.start();

        Thread.sleep(30000);
        medicine1.interrupt();
        medicine2.interrupt();


    }
}
