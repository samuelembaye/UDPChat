package Inlämnings.TEST2;

import Inlämnings.UDP.UDPChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends JFrame {
    private final String SERVER_ADDRESS = "localhost";
    private final int PORT = 12346;

    private String username;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;


    //    JFRAME
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    // User tracking
    private Map<String, Long> activeUsers = new ConcurrentHashMap<>();

    public Client(String username) throws IOException, ClassNotFoundException {
        this.username = username;
        GUI_Init();
        setupNetwork();
    }

    private void GUI_Init() {
        System.out.println(username);
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
            running = false;});
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
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

    }

    private void setupNetwork() throws IOException, ClassNotFoundException {
        try{ socket= new Socket(SERVER_ADDRESS, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            sendJoinMessage();
            // Start message receiver thread
            new Thread(this::receiveMessages).start();

        }catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    private void receiveMessages() {
        try {
            Object message;
            while (running && (message = in.readObject()) != null) {
                String receivedMessage = (String) message;
//                SwingUtilities.invokeLater(() -> chatArea.append(receivedMessage + "\n"));
                processMessage(receivedMessage);
            }
        } catch (IOException e) {
            if (running) {
                SwingUtilities.invokeLater(() ->
                        chatArea.append("Connection lost: " + e.getMessage() + "\n"));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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


    private void getMessagetosend() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            sendMessage("MESSAGE", text);
            messageField.setText("");
        }
    }

    private void sendMessage(String type, String text) {
        try {
            String message = String.format("{\"type\":\"%s\",\"sender\":\"%s\",\"text\":\"%s\"}",
                    type, username, text);

            out.writeObject(message);
            out.flush();

        } catch (IOException e) {
            chatArea.append("Error sending message: " + e.getMessage() + "\n");
        }
    }

    private void sendJoinMessage() {
        sendMessage("JOIN", "");
    }

    private void sendLeaveMessage() {
        sendMessage("LEAVE", "");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String username = JOptionPane.showInputDialog("Enter your username:");
        System.out.println(username);
        if (username == null || username.trim().isEmpty()) {
            username = "User" + new Random().nextInt(1000);
        }
       Client chat = new Client(username);
    }
}
