package Föreläsning6.Multiuser.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import Föreläsning6.Multiuser.Util.MultiUserKompis;

public class MultiUserClient {
    int port = 12345;
    InetAddress address = InetAddress.getByName("localhost");

    public MultiUserClient() throws IOException, ClassNotFoundException {
        try(Socket socket = new Socket(address, port);
            ObjectOutputStream request = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream response = new ObjectInputStream(socket.getInputStream());

        ) {
            String fromUser;
            BufferedReader userinput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Who do you want to call:");
            while ((fromUser = userinput.readLine()) != null) {
                request.writeObject(fromUser);
                request.flush();
                Object responseObj = response.readObject();
                if (responseObj instanceof MultiUserKompis) {
                    MultiUserKompis person = (MultiUserKompis) responseObj;
                    if (person != null) {
                        System.out.println(person.toString());
                    } else {
                        System.out.println("Person not found: " + fromUser);
                    }
                } else if (responseObj instanceof String) {
                    System.out.println(responseObj);
                }
                System.out.println("Who do you want to call:");
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MultiUserClient client = new MultiUserClient();
    }
}