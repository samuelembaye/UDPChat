package Föreläsning5.WeatherReportingSystem;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver extends JFrame {
    private JPanel panel = new JPanel();
    private JButton button= new JButton("REFRESH") ;
    private JTextArea textArea  = new JTextArea(10,30);
    private JLabel label = new JLabel("WeatherReportingSystem") ;
    private JScrollPane scrollPane = new JScrollPane(textArea);

    int minport = 12345;
    byte[] buf = new byte[1024];
    DatagramSocket socket;
    DatagramPacket packet;



    public Receiver() throws SocketException, IOException {
        panel.setLayout(new BorderLayout());
        panel.setSize(300, 300);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        button.addActionListener(e -> textArea.setText(""));
        panel.add(button, BorderLayout.SOUTH);

        add(panel);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Start receiver thread
        new Thread(this::receiveMessages).start();



    }

    private void receiveMessages() {
        try {
            socket = new DatagramSocket(minport);
            while (true) {
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                textArea.append(received + "\n");
                System.out.println(packet.getAddress().getHostAddress() + packet.getPort()+ packet.getData());
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Receiver receiver = new Receiver();
    }
}
