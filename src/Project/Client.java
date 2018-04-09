package Project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private  ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket request_connection;
    private Scanner scanner;

    public void initializeClient(){

        System.out.println("File Transfer Server platform!\n");

        try {

            //connecting to Server
            System.out.println("Give me the IP Address of the Server you want to connect: ");
            scanner = new Scanner(System.in);
            String ipAddress = scanner.next();
            System.out.println("Give me the port of the Server you want to connect: ");
            scanner = new Scanner(System.in);
            String portNymber = scanner.next();
            int port = Integer.parseInt(portNymber.trim());
            request_connection = new Socket(ipAddress,port);
            //end of connecting to Server

            in = new ObjectInputStream(request_connection.getInputStream());
       //     out = new ObjectOutputStream(request_connection.getOutputStream());

            Object welcome = in.readObject();//Reading Welcoming
            System.out.println(welcome);



        }
        catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
        finally {
            try {
                in.close();
             //   out.close();
                request_connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void main(String args[]){
        new Client().initializeClient();
    }
}
