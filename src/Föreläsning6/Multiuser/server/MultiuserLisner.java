package Föreläsning6.Multiuser.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiuserLisner {

    public static void main(String[] args) {
        while (true){
            try(ServerSocket serverSocket = new ServerSocket(12345)) {
                final Socket socketToclient = serverSocket.accept();
                MultiUserServer clientHandler = new MultiUserServer(socketToclient);
                clientHandler.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
