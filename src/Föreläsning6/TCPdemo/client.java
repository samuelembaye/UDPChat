package Föreläsning6.TCPdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class client {
    int port = 8080;
    InetAddress address = InetAddress.getByName("localhost");

    public client() throws IOException {
        try(Socket socket = new Socket(address, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
        {
            String input;
            while ((input = in.readLine()) != null ) {
                System.out.println(input);
            }

        }



    }


    public static void main(String[] args) throws IOException, InterruptedException {
        client client = new client();

    }
}
