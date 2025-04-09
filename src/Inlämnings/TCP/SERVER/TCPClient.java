package Inlämnings.TCP.SERVER;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;

    // GUI Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    public TCPClient(String username) {
        this.username = username;
        GUI_Init();
        setupNetwork();
    }

    private void GUI_Init() {
        setTitle("TCP Chat - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        messageField.addActionListener(e -> sendChatMessage());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendChatMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    out.println("/quit");
                    running = false;
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void setupNetwork() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username first
            out.println(username);

            // Start message receiver thread
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendChatMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            out.println(text);
            messageField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                String finalMessage = message;
                SwingUtilities.invokeLater(() -> chatArea.append(finalMessage + "\n"));
            }
        } catch (IOException e) {
            if (running) {
                SwingUtilities.invokeLater(() ->
                        chatArea.append("Connection lost: " + e.getMessage() + "\n"));
            }
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            username = "User" + new Random().nextInt(1000);
        }
        new TCPClient(username);
    }
}
