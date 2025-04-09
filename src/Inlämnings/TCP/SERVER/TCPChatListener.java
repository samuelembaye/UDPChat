package Inlämnings.TCP.SERVER;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TCPChatListener {
    private static final int PORT = 5000;
    private static final int USERLIST_UPDATE_INTERVAL = 5000;
    public static final Set<CHATServer> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("TCP Chat Server running...");

        // Start periodic user list updates
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        //executor.scheduleAtFixedRate(TCPChatListener::updateAllUserLists,
           //     USERLIST_UPDATE_INTERVAL, USERLIST_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                CHATServer client = new CHATServer(clientSocket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private static void updateAllUserLists() {
        for (CHATServer client : clients) {
            client.sendCurrentUserList();
        }
    }
}