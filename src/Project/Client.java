package Project;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private DatagramSocket udpSocket;
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
    private FileOutputStream fileOut;

    public void sendPacket(Byte[] payload)
    {

    }

    public void initializeClient(){
        int packetSize = 5;
        boolean flag = true, end = false;
        try
        {
            fileOut = new FileOutputStream("/home/marios/Desktop/test.c");
            byte[] data = new byte[packetSize];
            for (int i = 0; i < data.length; i++)
                data[i] = 1;
            byte[] ack = new byte[1];
            udpSocket = new DatagramSocket(7778);

            while (!end)
            {
                try
                {
                    if (!flag)
                    {
                        fileOut.write(data);
                    }

                    DatagramPacket packet = new DatagramPacket(data,data.length);
                    udpSocket.setSoTimeout(2*1000);
                    udpSocket.receive(packet);
                    end = true;
                    for (int i = 0; i < data.length; i++)
                        if (data[i] != 0)
                            end = false;
                    flag = false;
                    ack[0] = 1;
                    packet = new DatagramPacket(ack,ack.length,serverAddress,serverPort);
                    udpSocket.send(packet);
                }catch (SocketTimeoutException ste)
                {
                    flag = true;
                    System.out.println("Timeout");
                } catch (IOException ioe)
                {
                    System.out.println("IOE");
                }

            }



        }catch (SocketException se)
        {
            flag = false;
            System.out.println("SE");
        }catch (IOException ioe)
        {
            System.out.println("IOE");
        }finally
        {
            try
            {
                fileOut.close();
            } catch (IOException e)
            {

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

        /*if(args.length < 4 ) {
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
        }*/
        serverAddress = InetAddress.getByName("localhost");
        serverPort = Integer.parseInt("7777");
        new Client().initializeClient();
    }
}
