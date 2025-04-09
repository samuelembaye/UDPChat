package Inlämnings.TCP.SERVER;

import java.io.*;
import java.net.Socket;
import java.util.StringJoiner;

public class CHATServer extends Thread {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public CHATServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            registerUser();
            handleClientMessages();
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void initializeStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void registerUser() throws IOException {
        username = in.readLine();
        sendCurrentUserList();
        broadcast(username + " joined the chat", "SERVER");
        System.out.println(username + " connected");
    }

    private void handleClientMessages() throws IOException {
        String clientMessage;
        while ((clientMessage = in.readLine()) != null) {
            if (clientMessage.startsWith("/quit")) {
                break;
            }
            broadcast(clientMessage, username);
        }
    }

    void sendCurrentUserList() {
        StringJoiner joiner = new StringJoiner(",");
        for (CHATServer client : TCPChatListener.clients) {
            if (client.username != null) {
                joiner.add(client.username);
            }
        }
        sendMessage("USERLIST|" + joiner);
    }

    void sendMessage(String message) {
        out.println(message);
    }

    void broadcast(String message, String sender) {
        String formatted = sender + ": " + message;
        for (CHATServer client : TCPChatListener.clients) {
            client.sendMessage(formatted);
        }
        System.out.println("Broadcasting: " + formatted);
    }

    private void cleanup() {
        try {
            if (username != null) {
                TCPChatListener.clients.remove(this);
                broadcast(username + " left the chat", "SERVER");
                System.out.println(username + " disconnected");
            }
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}