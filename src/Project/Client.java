package Project;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client
{

    /* Declarations */
    private DatagramSocket udpSocket;           // The DatagramSocket through which the server communicates with all of its clients
    private static InetAddress serverAddress;   // The server's IP address
    private static int serverPort = 0;          // The server's port
    private FileOutputStream fileOut;           // An output stream to write data
    private DatagramPacket packet;
    private String path = "E:\\Users\\tasos\\DownloadsNew\\readme.txt";
    private String folderpath;
    private int payload_length = 60000;

    private byte[] header;
    private byte[] payload;
    private byte[] packetBuf;

    private void takeHeader()
    {
        for (int i = 0; i < header.length; i++)
            header[i] = packetBuf[i];
    }

    private void takePayload(DatagramPacket packet)
    {
        /*The payload length is the number of bytes read minus
        the number of the header length in bytes*/
        payload = new byte[packet.getLength() - header.length];
        for (int i = 0; i < payload.length; i++)
            payload[i] = packetBuf[i + header.length];
    }

    private byte[] createPacketBuffer(byte[] header, byte[] payload)
    {
        // Create the new packet with size the total of the payload and its header
        byte[] out = new byte[header.length + payload.length];
        // Insert the header at the beginning of the new packet
        for (int i = 0; i < header.length; i++)
            out[i] = header[i];
        // Insert the payload after the header
        for (int i = 0; i < payload.length; i++)
            out[i + header.length] = payload[i];
        return out;
    }

    public void sendPacket() throws IOException
    {
        packetBuf = createPacketBuffer(header, payload);
        packetBuf = path.getBytes();
        packet = new DatagramPacket(packetBuf, packetBuf.length, serverAddress, serverPort);
        udpSocket.send(packet);
    }

    public void sendAck() throws IOException
    {
        packet = new DatagramPacket(header, header.length, serverAddress, serverPort);
        udpSocket.send(packet);
    }

    public void recvPacket()
    {
        try
        {
            packet = new DatagramPacket(packetBuf, packetBuf.length);
            udpSocket.receive(packet);
        }catch (IOException ioe)
        {
            System.out.println("IOE");
            ioe.printStackTrace();

        }
    }

    public void recvPacket_withoutTimeOut()
    {
        try
        {
            packetBuf = createPacketBuffer(header, payload);
            packet = new DatagramPacket(packetBuf, packetBuf.length);
            udpSocket.receive(packet);
        }catch (SocketTimeoutException ste)
        {
            // The timeout expired so we send the same packet.
            System.out.println("Timeout");
        }catch (IOException ioe)
        {
            System.out.println("IOE");
            ioe.printStackTrace();
        }
    }

    public void initializeClient() throws IOException {

        header = new byte[1];
        payload = new byte[1000];
        packetBuf = new byte[header.length + payload.length];
        boolean flag = true, end = false; // Flag is true when we receive a packet out of order.
        try {
            // Initialize the output stream to write the data to the new file
            fileOut = new FileOutputStream("C:\\Users\\tasos\\Desktop\\aaaaaaaaaek.txt");
            //fileOut = new FileOutputStream("/home/marios/Desktop/test.mkv");

            // Create a datagram socket and connect it to the local client machine port
            udpSocket = new DatagramSocket(7778);

            initiateHandshake();

            int packetId = 0;

            while (!end)
            {
                try
                {
                    if (!flag)  // If all packets are received in order
                    {
                        fileOut.write(payload); // Write payload.length bytes from the payload array to fileOut
                        packetId = (packetId + 1) % 2;
                    }

                    // Reveive the data packet from the server.
                    recvPacket();
                    takeHeader();
                    takePayload(packet);

                    // If it is the end of the file we are done
                    if (header[0] == 2)
                        end = true;

                    flag = false;



                    if (!end && packetId != header[0])
                    {
                        System.out.printf("out of order\n");
                        flag = true;
                    }

                    // Send the acknowledgement to the server
                    header[0] = (byte) packetId;
                    sendAck();
                } catch (IOException ioe)
                {
                    System.out.println("IOE");
                }

            }

        } catch (SocketException se)
        {
            System.out.println("SE");
        } catch (IOException ioe)
        {
            System.out.println("IOE");
        } finally
        {
            try
            {
                fileOut.close();
            } catch (IOException e)
            {

            }
        }
    }

    private void initiateHandshake() throws IOException {

        //sending folder path name and filename
        header = new byte[1];
        header[0] = 7;
        payload = path.getBytes(StandardCharsets.UTF_8);
        System.out.print(payload.length);
        sendPacket();
        //end of sending folder path name and filename


        //sending payload length
        header = new byte[1];
        header[0] = 7;
        ByteBuffer b = ByteBuffer.allocate(payload_length);
        b.putInt(payload_length);
        payload =  b.array();

        String s = StandardCharsets.UTF_8.decode(b).toString();
        System.out.print("\naaaaaek" + s );
        sendPacket();
        //end of sending payload length


        //recieving the welcome message
        header = new byte[1];
        header[0] = 8;
        payload = new byte[60000];

        recvPacket_withoutTimeOut();
        takeHeader();
        takePayload(packet);

        path = new String(packet.getData(),0, packet.getLength());
        System.out.print("\n" + path);
        //end of recieving the welcome message
    }

    public void statistics()
    {

        System.out.println("The total time of the transfer was " + " sec");
        System.out.println("The speed of the transfer was " + " Kbyte/sec");
        System.out.println("The total number of UDP/IP packets of the transfer was ");
        System.out.println("The payload length of the packet was ");

    }


    public static void main(String args[]) throws IOException
    {

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
                payload_length = Integer.parseInt(args[9].trim());
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
