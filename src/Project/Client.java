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
    private StringBuilder absolutepath = new StringBuilder();

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
                             /*           if(request_connection.isConnected()){
                                            System.out.println("You may entered a wrong IP Address or a port of the Server\n");
                                            initializeClient();
                                        }*/
            //end of connecting to Server

            in = new ObjectInputStream(request_connection.getInputStream());
       //     out = new ObjectOutputStream(request_connection.getOutputStream());

            Object welcome = in.readObject();//Reading Welcoming
            System.out.println("\n" + welcome);

            //file name & folder path
            System.out.println("Give me the name of the file you want to transfer: ");
            scanner = new Scanner(System.in);
            String filename = scanner.next();
            System.out.println("Give me the folder path of the file you want to transfer: ");
            scanner = new Scanner(System.in);
            String folder_path = scanner.next();
            absolutepath.append(folder_path).append("\\").append(filename);
            System.out.println(absolutepath.toString());
            //end of file name & folder path

        }
        catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (Exception ioException) {
            System.err.println("Something went wrong!\n" + "You may entered a wrong IP Address or a port of the Server\n");
            //ioException.printStackTrace();
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

    public void statistics(){

    }



    public static void main(String args[]){
        new Client().initializeClient();
    }
}
