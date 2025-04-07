package Föreläsning5.uppgift1a;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Sender extends Thread {
    JFrame jframe = new JFrame();
    JPanel panel = new JPanel();
    JTextArea textArea = new JTextArea();

//    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    InetAddress toAdr = InetAddress.getLocalHost();
//    InetAddress toAdr = InetAddress.getByName("172.20.202.55");// jing
//    InetAddress toAdr = InetAddress.getByName("172.20.213.117");// alexandra
//    InetAddress toAdr = InetAddress.getByName("172.20.203.170");// Jama
    int toPort = 12345;
    DatagramSocket socket = new DatagramSocket();
    DatagramPacket packet;
    byte[] data;
    String prompt = "Och vad har du på hjärtat? ";
    String message;
    ArrayList<String> mylist = new ArrayList<>();

    public Sender() throws UnknownHostException, SocketException, IOException {
        jframe.add(panel);
        panel.add(textArea);

        mylist.add("first");
        mylist.add("second");
        mylist.add("third");
        mylist.add("fourth");
        mylist.add("fifth");

        jframe.pack();
        jframe.setVisible(true);
        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public void run() {
        int i = 0; // Moved outside the loop
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(1000);
//                System.out.println(mylist.get(i)); // Use get(i) instead of [i]
                message = mylist.get(i);
                data = message.getBytes();
                packet = new DatagramPacket(data, data.length, toAdr, toPort);
                socket.send(packet);
                textArea.setText(message);
                System.out.println("Sent message to " + toAdr + ":" + toPort + ": " + message);
                if (i >= 4) {
                    i = 0;
                } else {
                    i++;
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (Thread.interrupted()) {
            System.out.println("Thread.interrupted");
        }
    }

//    public static void main(String[] args) throws UnknownHostException, SocketException, IOException {
//        Sender sender = new Sender();
//        sender.start(); // Starts the run() method in a new thread
//    }
}