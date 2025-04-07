package Inlämnings.UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MulticastChat extends JFrame {
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final int PORT = 5000;
    private static final int HEARTBEAT_INTERVAL = 50000; // 5 seconds
    private static final int USER_TIMEOUT = 3000; // 30 seconds

    private String username;
    private MulticastSocket socket;
    private InetAddress group;
    private volatile boolean running = true;

    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    // User tracking
    private Map<String, Long> activeUsers = new ConcurrentHashMap<>();
    private int sequenceNumber = 0;

    public MulticastChat(String username) {
        this.username = username;
        initializeGUI();
        setupNetwork();
        startHeartbeatThread();
        startUserCleanupThread();
    }

    private void initializeGUI() {
        setTitle("Multicast Chat - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        mainPanel.add(chatScroll, BorderLayout.CENTER);

        // User list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScroll, BorderLayout.EAST);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
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
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

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

    private void sendJoinMessage() {
        sendMessage("JOIN", "");
    }

    private void sendLeaveMessage() {
        sendMessage("LEAVE", "");
    }

    private void sendHeartbeat() {
        sendMessage("HEARTBEAT", "");
    }

    private void sendChatMessage(String text) {
        sendMessage("MESSAGE", text);
    }

    private void sendMessage(String type, String text) {
        try {
            String message = String.format("{\"type\":\"%s\",\"seq\":%d,\"sender\":\"%s\",\"text\":\"%s\"}",
                    type, sequenceNumber++, username, text);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
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
                if (running) {
                    chatArea.append("Error receiving message: " + e.getMessage() + "\n");
                }
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
                updateUserList();

                switch (type) {
                    case "JOIN":
                        chatArea.append(sender + " has joined the chat\n");
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
            });
        } catch (Exception e) {
            chatArea.append("Error processing message: " + e.getMessage() + "\n");
        }
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"(.*?)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        pattern = "\"" + key + "\":(\\d+)"; // For numbers
        r = java.util.regex.Pattern.compile(pattern);
        m = r.matcher(json);
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

    private void startHeartbeatThread() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (running) {
                sendHeartbeat();
            }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void startUserCleanupThread() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            activeUsers.entrySet().removeIf(entry ->
                    !entry.getKey().equals(username) && (now - entry.getValue() > USER_TIMEOUT));
            SwingUtilities.invokeLater(this::updateUserList);
        }, USER_TIMEOUT, USER_TIMEOUT/2, TimeUnit.MILLISECONDS);
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            sendChatMessage(text);
            messageField.setText("");
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            username = "User" + new Random().nextInt(1000);
        }
        new MulticastChat(username);
    }
}