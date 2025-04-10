package Inlämnings.TEST2;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client extends JFrame {
    // Constants and variables
    private final String SERVER_ADDRESS = "localhost";
    private final int PORT = 12346;
    private final ObjectMapper mapper = new ObjectMapper();
    private String username;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean running = true;

    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JButton sendButton;
    private JButton KoplanerBtton;

    public Client(String username) {
        this.username = username;
        initializeGUI();
        connectToServer();
    }

    private void initializeGUI() {
        setTitle("Chat Client - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with disconnect button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        KoplanerBtton = new JButton("Koplaner");
        KoplanerBtton.setBackground(new Color(200, 50, 50));
        KoplanerBtton.setForeground(Color.WHITE);
        KoplanerBtton.addActionListener(e -> Koplaner());
        topPanel.add(KoplanerBtton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with chat area and user list
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        centerPanel.add(chatScroll, BorderLayout.CENTER);

        // User list
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Online Users"));
        userPanel.setPreferredSize(new Dimension(150, 0));
        userList = new JList<>(userListModel);
        userList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        userList.setBackground(new Color(197, 239, 214));
        centerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with message input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageField.addActionListener(e -> sendMessage());

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Window listener for proper cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Koplaner();
            }
        });

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            sendServerMessage("JOIN", "");
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void receiveMessages() {
        try {
            while (running) {
                String jsonMessage = (String) in.readObject();
                ChatMessage message = mapper.readValue(jsonMessage, ChatMessage.class);

                SwingUtilities.invokeLater(() -> {  // code runs on the Event Dispatch Thread (EDT)
                    switch (message.getType()) {
                        case "ACK":
                            // Full user list update from server
                            updateUserList(message.getText().split(","));
                            break;
                        case "JOIN":
                            // Add new user immediately to the list
                            if (!userListModel.contains(message.getSender())) {
                                userListModel.addElement(message.getSender());
                                sortUserList();
                            }
                            chatArea.append(message.getSender() + " has joined the chat\n");
                            break;
                        case "LEAVE":
                            userListModel.removeElement(message.getSender());
                            chatArea.append(message.getSender() + " has left the chat\n");
                            break;
                        case "MESSAGE":
                            chatArea.append(message.getSender() + ": " + message.getText() + "\n");
                            break;
                    }
                    // Auto-scroll to bottom
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                });
            }
        } catch (Exception e) {
            if (running) {
                SwingUtilities.invokeLater(() ->
                        chatArea.append("Connection error: " + e.getMessage() + "\n"));
            }
        } finally {
            Koplaner();
        }
    }
    // New helper method to sort the user list
    private void sortUserList() {
        String[] users = new String[userListModel.size()];
        userListModel.copyInto(users);
        Arrays.sort(users);
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }
    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            // Immediately show your own message in the chat area
            chatArea.append("You: " + text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());

            // Send the message to server
            sendServerMessage("MESSAGE", text);
            messageField.setText("");
        }
    }

    private void updateUserList(String[] users) {
        userListModel.clear();
        Arrays.stream(users)
                .filter(name -> !name.isEmpty())
                .sorted()
                .forEach(userListModel::addElement);
    }



    private void sendServerMessage(String type, String text) {
        try {
            out.writeObject(mapper.writeValueAsString(new ChatMessage(type, username, text)));
            out.flush();
        } catch (IOException e) {
            chatArea.append("Send error: " + e.getMessage() + "\n");
        }
    }

    private void Koplaner() {
        running = false;
        try {
            sendServerMessage("LEAVE", "");
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Disconnect error: " + e.getMessage());
        }
        dispose();
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username == null || username.trim().isEmpty()) {
            username = "User" + (int)(Math.random() * 1000);
        }
        Client client = new Client(username);
    }
}