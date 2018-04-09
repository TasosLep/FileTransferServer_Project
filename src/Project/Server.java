package Project;

import com.sun.javafx.iio.ios.IosDescriptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLSyntaxErrorException;

public class Server {

    private ServerSocket serverSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public void initializeServer(){

        try {
            serverSocket = new ServerSocket(666,1);
            System.out.println("Server started...");

        }
        catch (IOException e){
            System.out.println("Something went wrong when starting the server!");
        }

        while (true){
            try {
                Socket socket = serverSocket.accept();
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("We have a new client connection...\n");


            }
            catch (IOException io){
                System.out.println("Something went wrong with a client!");
            }

        }


    }

    public static void main(String args[]){
        new Server().initializeServer();
    }
}
