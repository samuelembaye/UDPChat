package Föreläsning6.Telefonbok;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class teleserver {
    int port = 12345;
    ArrayList<Kompis> kompisList = new ArrayList<Kompis>();

    public teleserver() throws IOException {
        kompisList.add(new Kompis("Ragnar","stockholmvägen", "0711111111"));
        kompisList.add(new Kompis("John", "Telivägen", "0722222222"));
        kompisList.add(new Kompis("Ruth","sveavägen", "0733333333"));

        try(ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ){
            String input;
            while((input = in.readLine() )!= null){
                System.out.println(getKompis(input));
                out.println(getKompis(input));

            }
        }
    }
    public Kompis getKompis(String kompis) {
        for(Kompis k : kompisList){
            if(k.getName().equalsIgnoreCase(kompis)){
                return k;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        teleserver server = new teleserver();
    }
}
