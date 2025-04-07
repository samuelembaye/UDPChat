package Föreläsning5.uppgift1a;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;

public class DatagramReceiver {

    public static void main(String[] args) throws SocketException, IOException {
        JFrame jframe = new JFrame("Datagram Receiver");
        jframe.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea("starter");
        JButton button = new JButton("CLEAR");

        panel.add(textArea, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);
        jframe.add(panel );
//        jframe.add(textArea, BorderLayout.NORTH);
//        jframe.add(button, BorderLayout.SOUTH);

        jframe.pack();
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);

        button.addActionListener(l -> textArea.setText(""));

        int minPort = 12345;
        DatagramSocket socket = new DatagramSocket(minPort);
        byte[] data = new byte[256];

        while (true) {
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            System.out.println("Meddelande från " + packet.getAddress().getHostAddress() + " " + LocalDateTime.now());
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(message+"\n");
            textArea.append(message);
        }
    }
}