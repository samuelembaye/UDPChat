package Föreläsning4.Threading;

//import static com.sun.tools.javac.jvm.ByteCodes.pop;

public class MedicinTimer extends Thread {
    String medicine;
    int interval;

    public MedicinTimer(String medicine, int interval) {
        this.medicine = medicine;
        this.interval = interval;
    }

    public void run() {
        while(!Thread.interrupted()){
            try {
                Thread.sleep(interval);
                System.out.println(medicine);
                MedicinePop med = new MedicinePop(medicine);
            } catch (InterruptedException e) {
                System.out.println("MedicinTimer interrupted");
                break;
            }
        }
        if (Thread.interrupted()){
            System.out.println("Thread.interrupted");
        }
    }


}
