package Inlämnings.TEST2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {
    private static final int PORT = 12346;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                Server clientHandler = new Server(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}