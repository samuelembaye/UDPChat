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
        initializeStreams();
    }

    private void initializeStreams() {
        try {
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
                if (message == null) break;
                processMessage((String) message);
            }
        } catch (Exception e) {
            if (running) System.err.println("Client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void processMessage(String jsonMessage) throws IOException {
        ChatMessage message = mapper.readValue(jsonMessage, ChatMessage.class);

        switch (message.getType()) {
            case "JOIN":
                handleJoin(message);
                break;
            case "LEAVE":
                handleLeave(message);
                break;
            case "MESSAGE":
                broadcastMessage(message);
                break;
            case "ACK":
                break; // No action needed for ACK
        }
    }

    private void handleJoin(ChatMessage message) throws IOException {
        this.username = message.getSender();
        activeClients.put(username, this);

        // 1. Send ACK to new client with full user list
        sendAckWithUserList();

        // 2. Broadcast JOIN to all clients (which will trigger them to add the new user)
        broadcastMessage(new ChatMessage("JOIN", username, "has joined the chat"));

        // 3. Send updated ACK to all clients with new user list
        ChatMessage updateAck = new ChatMessage("ACK", "SERVER", String.join(",", activeClients.keySet()));
        broadcastMessage(updateAck);
    }

    private void sendAckWithUserList() throws JsonProcessingException,IOException {
        ChatMessage ack = new ChatMessage("ACK", "SERVER", String.join(",", activeClients.keySet()));
        out.writeObject(mapper.writeValueAsString(ack));
        out.flush();
    }

    private void handleLeave(ChatMessage message) throws JsonProcessingException , IOException {
        // 1. Broadcast LEAVE message to all clients
        broadcastMessage(new ChatMessage("LEAVE", username, "has left the chat"));

        // 2. Remove user from active clients
        activeClients.remove(username);

//        // 3. Send ACK to leaving client (optional)
//        sendAckMessage();
//
//        // 4. Broadcast updated user list to all remaining clients
//        broadcastUserListUpdate();

        running = false;
    }
//    private void sendAckMessage() throws JsonProcessingException,IOException {
//        ChatMessage ack = new ChatMessage("ACK", "SERVER", "ack");
//        out.writeObject(mapper.writeValueAsString(ack));
//        out.flush();
//    }
//    private void broadcastUserListUpdate() throws JsonProcessingException {
//        String userList = String.join(",", activeClients.keySet());
//        ChatMessage update = new ChatMessage("ACK", "SERVER", userList);
//        broadcastMessage(update);
//    }

    private void broadcastMessage(ChatMessage message) {
        try {
            String jsonMessage = mapper.writeValueAsString(message);
            for (Server client : activeClients.values()) {
                if (client != this) {
                    client.out.writeObject(jsonMessage);
                    client.out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Broadcast error: " + e.getMessage());
        }
    }

    private void closeConnection() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Close error: " + e.getMessage());
        }
        if (username != null) activeClients.remove(username);
    }
}