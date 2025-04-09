package Inlämnings.TEST2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, Server> activeClients = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            // Initialize streams in this order to prevent deadlock
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
            closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                Object message = in.readObject();
                if (message == null) break; // Client disconnected
                processMessage((String) message);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message format: " + e.getMessage());
        } catch (IOException e) {
            if (running) { // Only log if we didn't intentionally close
                System.err.println("Client connection error: " + e.getMessage());
            }
        } finally {
            closeConnection();
        }
    }

    private void processMessage(String jsonMessage) {
        try {
            ChatMessage message = mapper.readValue(jsonMessage, ChatMessage.class);

            // Handle JOIN messages first to set username
            if ("JOIN".equals(message.getType())) {
                this.username = message.getSender();
                activeClients.put(username, this);
                System.out.println(username + " joined the chat");
            }

            // Broadcast to all clients except sender
            broadcastMessage(message);

            // Handle LEAVE messages last
            if ("LEAVE".equals(message.getType())) {
                activeClients.remove(username);
                System.out.println(username + " left the chat");
                running = false; // Stop this thread
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private void broadcastMessage(ChatMessage message) {
        try {
            String jsonMessage = mapper.writeValueAsString(message);
            for (Server client : activeClients.values()) {
                if (client != this) { // Don't send back to sender
                    try {
                        client.out.writeObject(jsonMessage);
                        client.out.flush();
                    } catch (IOException e) {
                        // Remove disconnected clients
                        activeClients.remove(client.username);
                        System.err.println("Removed disconnected client: " + client.username);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error creating JSON: " + e.getMessage());
        }
    }

    private void closeConnection() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }

        if (username != null) {
            activeClients.remove(username);
            System.out.println("Cleaned up resources for: " + username);
        }
    }

    public static class ChatMessage {
        private String type;
        private String sender;
        private String text;

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}