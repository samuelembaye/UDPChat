package Föreläsning5.WeatherReportingSystem;

import Föreläsning5.uppgift1a.Sender;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class WeatherSender extends Thread {
    JFrame jframe = new JFrame();
    JPanel panel = new JPanel();
    JTextArea textArea = new JTextArea(2,20);
    JButton button = new JButton("Send");
    JLabel label = new JLabel("WeatherReportingSystem") ;

    InetAddress toAddr ;
    int toPort = 12345;
    byte[] data = new byte[1024];
    DatagramSocket socket ;
    DatagramPacket packet ;
    String cityName;

    BufferedReader stdIn;

    public WeatherSender() throws IOException {
        toAddr = InetAddress.getLocalHost();
        socket = new DatagramSocket();
//        packet =  new DatagramPacket(data, data.length);
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        jframe.add(panel);
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the city name");
        String cityName = stdIn.readLine();
        label.setText(cityName);
        // Button action
        button.addActionListener(e -> {
            try {
                transmiter((cityName + ": " + textArea.getText().trim() + "*C"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(jframe,
                        "Error sending message: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        jframe.pack();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true);
        jframe.setLocationRelativeTo(null);
    }

    private void transmiter(String message) throws IOException {
        System.out.println(message);
        System.out.println(cityName);
        if (message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(jframe,
                    "Please enter a message to send",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
//        System.out.println(cityName );
//        message = cityName + message;
        byte[] data = message.getBytes();
        packet = new DatagramPacket(data, data.length, toAddr, toPort);
        socket.send(packet);
        System.out.println("Sent: " + message);
        textArea.setText("");
    }


    public static void main(String[] args) throws IOException {

        WeatherSender transfer = new WeatherSender();
    }
}
