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
    private  ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket request_connection;
    private Scanner scanner;
    private StringBuilder absolutepath = new StringBuilder();
    private static int ACK_NUM = 0;
    private static int SYNC_NUM = 0;
    private FileOutputStream fileOut;

    private byte[] header;
    private byte[] payload;
    private byte[] packetBuf;

    public void sendPacket(Byte[] payload)
    {

    }

    private void takeHeader(byte[] packet)
    {
       for (int i = 0; i < header.length; i++)
       {
           //System.out.println(packet[i]);
           header[i] = packet[i];
       }
    }

    private void takePayload(DatagramPacket packet)
    {
        payload = new byte[packet.getLength() - header.length];
        for (int i = 0; i < payload.length; i++)
            payload[i] = packetBuf[i+header.length];
    }

    public void initializeClient(){

        header = new byte[1];
        payload = new byte[60000];
        packetBuf = new byte[header.length + payload.length];
        boolean flag = true, end = false;
        try
        {
            fileOut = new FileOutputStream("/home/marios/Desktop/test.mkv");
            udpSocket = new DatagramSocket(7778);
            int packetId = 0;
            while (!end)
            {
                try
                {
                    if (!flag)
                    {
                        fileOut.write(payload);
                        packetId = (packetId+1)%2;
                    }

                    DatagramPacket packet = new DatagramPacket(packetBuf,packetBuf.length);
                    //udpSocket.setSoTimeout(2*1000);
                    udpSocket.receive(packet);
                    takeHeader(packetBuf);
                    takePayload(packet);
                    //System.out.println("end before: " + end);
                    if (header[0] == 2)
                        end = true;
                    flag = false;
                    System.out.println("end after: " + end);

                    //System.out.println(header[0] + " the client take");
                    if (packetId != header[0])
                        flag = true;
                    header[0] = (byte)packetId;
                    packet = new DatagramPacket(header,header.length,serverAddress,serverPort);
                    udpSocket.send(packet);
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
