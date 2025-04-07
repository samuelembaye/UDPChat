package Föreläsning6.TCPdemo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Server {
    int port = 8080;
    ArrayList<String> serverData;

    public Server() throws IOException {
        serverData = new ArrayList<>();
        serverData.add("Manday 2025-03-21");
        serverData.add("Tuesday 2025-04-01");
        serverData.add("Wednsday 2025-04-02");
        serverData.add("Thursday 2025-04-03");

        try(ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {while(true){
                for (String day : serverData) {
                    out.println(day);
                    sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();

    }
}
