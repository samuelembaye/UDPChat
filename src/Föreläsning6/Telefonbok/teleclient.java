package Föreläsning6.Telefonbok;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class teleclient {
    int port = 12345;
    InetAddress address = InetAddress.getByName("localhost");

    public teleclient() throws IOException {
        try(Socket socket = new Socket(address, port);
            PrintWriter request = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ){
            String fromUser;
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Who do you want to call:");
            while ((fromUser = stdin.readLine()) != null) {
                request.println(fromUser);
                System.out.println(inFromServer.readLine());
                System.out.println("Who do you want to call:");

            }
        }
        }

    public static void main(String[] args) throws IOException {
        teleclient client = new teleclient();
    }
}
