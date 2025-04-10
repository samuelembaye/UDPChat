package Inlämnings.UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class UDPChat extends JFrame {

    private static final String MULTICAST_ADDRESS = "230.0.0.1"; // Correct multicast address
//    private static final String MULTICAST_ADDRESS = "224.0.0.1"; // Scope: local machine only
    private static final int PORT = 5000;

    private String username;
    private MulticastSocket socket;
    private InetAddress groupAddress;
    private volatile boolean running = true;

//    JFRAME
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    // User tracking
    private Map<String, Long> activeUsers = new ConcurrentHashMap<>();
    private int sequenceNumber = 0;

    public UDPChat(String username) {
        this.username = username;
        new Thread(this::GUI_Init).start();
//        GUI_Init();
        setupNetwork();

    }

    private void GUI_Init() {
//        System.out.println(username);
        setTitle("SAMI Chat - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel fullPanel = new JPanel(new BorderLayout());

        JButton koplanerButton = new JButton("Koplaner");
        koplanerButton.setPreferredSize(new Dimension(120, 40));
        koplanerButton.setBackground(new Color(129, 3, 28)); // Softer red
        koplanerButton.setForeground(Color.WHITE);Font buttonFont;
        buttonFont = new Font("Segoe UI", Font.BOLD, 14); // Modern Windows font
        koplanerButton.setFont(buttonFont);koplanerButton.setFocusPainted(false); // Removes the dotted border when focused
        koplanerButton.addActionListener(e ->
                                        {sendLeaveMessage();
                                        running = false;;
                                        dispose();
                                        });
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(koplanerButton, BorderLayout.EAST);
        fullPanel.add(topPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatArea.setEditable(false);
        fullPanel.add(chatScroll, BorderLayout.CENTER);

        // User list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));
        JPanel eastpanel = new JPanel(new BorderLayout());
        eastpanel.setBorder(BorderFactory.createTitledBorder("Active"));
        eastpanel.add(userScroll, BorderLayout.CENTER);
        fullPanel.add(eastpanel, BorderLayout.EAST);

        JPanel messagepanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> getMessagetosend());
        messagepanel.add(messageField, BorderLayout.CENTER);

        JButton chatButton = new JButton("SKICKA");
        chatButton.addActionListener(e -> getMessagetosend());
        messagepanel.add(chatButton, BorderLayout.EAST);
        fullPanel.add(messagepanel, BorderLayout.SOUTH);

        add(fullPanel);
       // pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Add shutdown hook
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendLeaveMessage();
                running = false;
                if (socket != null) socket.close();
            }
        });

    }

    private void setupNetwork() {
        try {
            groupAddress = InetAddress.getByName(MULTICAST_ADDRESS);
            socket = new MulticastSocket(PORT);
//            socket.setLoopbackMode(false); // Allow receiving your own packets
//            socket.setInterface(InetAddress.getByName("0.0.0.0")); // Bind to all interfaces
            socket.joinGroup(groupAddress);

            // Send join message
            sendJoinMessage();
            // Start receiver thread
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error setting up network: " + e.getMessage(),
                    "Network Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void getMessagetosend() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            sendMessage("MESSAGE", text);
            messageField.setText("");
        }
    }

    private void sendJoinMessage() {
        sendMessage("JOIN", "");
    }

    private void sendLeaveMessage() {
        sendMessage("LEAVE", "");
    }

    private void sendackMessage() {
        sendMessage("ACK", "");
    }

    private void sendMessage(String type, String text) {
        try {
            String message = String.format("{\"type\":\"%s\",\"seq\":%d,\"sender\":\"%s\",\"text\":\"%s\"}",
                    type, sequenceNumber++, username, text);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, PORT);
            socket.send(packet);
        } catch (IOException e) {
            chatArea.append("Error sending message: " + e.getMessage() + "\n");
        }
    }

    private void receiveMessages() {
        byte[] buffer = new byte[1024];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                processMessage(received);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processMessage(String jsonMessage) {
        try {
            // Simple JSON parsing (for simplicity, in real app use a proper JSON library)
            String type = extractValue(jsonMessage, "type");
            String sender = extractValue(jsonMessage, "sender");
            String text = extractValue(jsonMessage, "text");

            // Update user activity
            activeUsers.put(sender, System.currentTimeMillis());

            // Update GUI
            SwingUtilities.invokeLater(() -> {

                switch (type) {
                    case "ACK":
                        break;
                    case "JOIN":
                        chatArea.append(sender + " has joined the chat\n");
                        sendackMessage();
                        break;
                    case "LEAVE":
                        chatArea.append(sender + " has left the chat\n");
                        activeUsers.remove(sender);
                        break;
                    case "MESSAGE":
                        chatArea.append(sender + ": " + text + "\n");
                        break;
                    // HEARTBEAT just updates last active time
                }

                updateUserList();
            });
        } catch (Exception e) {
            chatArea.append("Error processing message: " + e.getMessage() + "\n");
        }
    }

    private String extractValue(String jsonMessage, String key) {
        String pattern = "\"" + key + "\":\"(.*?)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(jsonMessage);
        if (m.find()) {
            return m.group(1);
        }
        pattern = "\"" + key + "\":(\\d+)"; // For numbers
        r = java.util.regex.Pattern.compile(pattern);
        m = r.matcher(jsonMessage);
        if (m.find()) {
            return m.group(1);
        }
        return "";

    }

    private void updateUserList() {
        userListModel.clear();
        activeUsers.keySet().stream()
                .sorted()
                .forEach(userListModel::addElement);
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
//        System.out.println(username);
        if (username == null || username.trim().isEmpty()) {
            username = "User" + new Random().nextInt(1000);
        }
        UDPChat chat = new UDPChat(username);
    }
}
