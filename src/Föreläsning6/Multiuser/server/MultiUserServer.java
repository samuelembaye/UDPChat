package Föreläsning6.Multiuser.server;

import Föreläsning6.Multiuser.Util.MultiUserKompis;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MultiUserServer extends Thread {
    private Socket clientSocket;
    private MultiUserDatabase db = new MultiUserDatabase();

    public MultiUserServer(Socket clientSocket) {
        this.clientSocket = clientSocket;

        }

    @Override
    public void run() {
        try(
                ObjectOutputStream response = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream request = new ObjectInputStream(clientSocket.getInputStream());
                )
        {

            String inputLine;
            String returnPerson;
            Object searchedPerson;

            System.out.println("up and running");
            while((inputLine = (String) request.readObject()) != null){
//                returnPerson = db.getKompisData(inputLine.trim());
                searchedPerson = db.getKompisObject(inputLine.trim());
                if (searchedPerson == null) {
                    response.writeObject("Denna Person finns inte i databasen");
                } else {
                    response.writeObject(searchedPerson);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
