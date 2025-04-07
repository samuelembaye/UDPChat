package Föreläsning5.uppgift1a;

import java.io.IOException;

public class Huvudprogram { // Fixed class name (PascalCase)

        public static void main(String[] args) throws IOException, InterruptedException {
            Sender sending = new Sender();
            sending.start();  // Starts the Sender thread
//            Thread.sleep(3000);  // Wait 3 seconds
//            sending.interrupt();
        }

}
