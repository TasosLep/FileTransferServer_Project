package Project;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private static final int MAXIMUM_BUFFER_SIZE = 512;
    private static InetAddress serverAddress;
    private static int serverPort=0;
    private static File filename;
    private static String folderpath;
    private static int payload;
    private  ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket request_connection;
    private Scanner scanner;
    private StringBuilder absolutepath = new StringBuilder();
    private static int ACK_NUM = 0;
    private static int SYNC_NUM = 0;

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

            //setting the payload
            System.out.println("Give me the lenght of the payload: ");
            scanner = new Scanner(System.in);
            int payload_length = Integer.parseInt(scanner.next().trim());
            //end of setting the payload

            //finding the length of the file
            File file = new File(absolutepath.toString());
            double file_size = file.length();// in Bytes
            //end of finding the length of the file

            //3 way handshake

            //end of 3 way handshake

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

        System.out.println("The total time of the transfer was " + " sec");
        System.out.println("The speed of the transfer was " + " Kbyte/sec");
        System.out.println("The total number of UDP/IP packets of the transfer was ");
        System.out.println("The payload length of the packet was ");

    }



    public static void main(String args[]) throws UnknownHostException {

        if(args.length < 4 ) {
            System.out.println("Usage is: java Server -ip <server ip address> -p <server port>");
            System.exit(0);
        }
        else if(args.length >= 4 && args.length <= 10 ){
            if(!args[0].equals("-ip"))
            {
                System.out.println("Usage is: java Server -ip <server ip address>");
                System.exit(0);
            }
            if(!args[2].equals("-p"))
            {
                System.out.println("Usage is: java Server -ip <server ip address> -p <server port>");
                System.exit(0);
            }
            if(!(args.length > 5) || (!args[4].equals("-fn")))
            {
                System.out.println("\"Usage is: java Server -ip <server ip address> -p <server port> -fn <filename>");
                System.exit(0);
            }
            if(!(args.length > 7) || !args[6].equals("-fp"))
            {
                System.out.println("\"Usage is: java Server -ip <server ip address> -p <server port> -fn <filename> -fp <folderpath>");
                System.exit(0);
            }
            if(!(args.length >= 8) || !args[8].equals("-pl"))
            {
                System.out.println("Usage is: java Server -ip <server ip address> -p <server port> -fn <filename> -fp <folderpath> -pl <payload> ");
                System.exit(0);
            }


            if((Integer.parseInt(args[9].trim()) < 1 || Integer.parseInt(args[9].trim()) > 65500)){
                System.out.println("Payload length must be between 1 - 65500 bytes!");
                System.exit(0);
            }
            if((args.length > 9)){

                serverAddress = InetAddress.getByName(args[1]);
                serverPort = Integer.parseInt(args[3].trim());
                filename = new File(args[5]);
                folderpath = args[7];
                payload = Integer.parseInt(args[9].trim());
            }
        }
        else{
            System.out.println("Usage is: java Server -ip <server ip address> -p <server port> -fn <filename> -fp <folderpath> -pl <payload> ");
            System.exit(0);
        }
        //    new Client().initializeClient();

    }
}
