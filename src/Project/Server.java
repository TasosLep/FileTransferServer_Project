package Project;

import java.io.*;
import java.net.*;

public class Server
{
    // The complete address of the destination of the datagram packet
    private InetAddress Address;
    private int Port = 0;
    private FileInputStream fileIn;
    private DatagramSocket udpSocket;
    byte[] header;
    byte[] payload;
    byte[] packetBuf;
    private DatagramPacket packet;
    private String path = "";
    private boolean flag; // This flag is true when we must send the same packet.

    /**
     * This method concatenates the header and the payload
     * into a new packet.
     *
     * @param header  The header of the packet.
     * @param payload The payload of the packet.
     * @return The new packet.
     */
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


    public void sendPacket() throws IOException
    {
        packetBuf = createPacketBuffer(header, payload);
        packet = new DatagramPacket(packetBuf, packetBuf.length, Address, Port);
        udpSocket.send(packet);
    }

    public void recvPacket(int sec)
    {
        try
        {
            udpSocket.setSoTimeout(sec * 1000);
            packet = new DatagramPacket(header, header.length);
            udpSocket.receive(packet);
        }catch (SocketTimeoutException ste)
        {
            // The timeout expired so we send the same packet.
            flag = true;
            System.out.println("Timeout");
        }catch (IOException ioe)
        {
            System.out.println("IOE");
            ioe.printStackTrace();

        }
    }

    public void initializeServer() throws UnknownHostException
    {

        // Provide the complete address of the destination of the datagram packet
        Address = InetAddress.getByName("localhost");
        Port = Integer.parseInt("7778");
        try
        {
            // Create the DatagramSocket through which the server will communicate with the client
            udpSocket = new DatagramSocket(7777);
            initiateandshake();

            //int packetNum = 0;
            //boolean flag = false;
            fileIn = new FileInputStream(new File("E:\\Users\\tasos\\DownloadsNew\\readme.txt"));
            //fileIn = new FileInputStream(new File("/home/marios/Programming/cpp_prog/test.cpp"));
            boolean end = false;
            header = new byte[1];
            payload = new byte[60000];
            int packetId = -1;

            while (!end)    // While the end of the file has not been reached
            {
                try
                {
                    if (!flag)  // If we do not need to retransmit the packet
                    {
                        int len = fileIn.read(payload);
                        if (len == -1)  // If there is no data because the end of the file has been reached
                            end = true; // Tell it to the client
                        else
                        {
                            /*Adjust the payload length to the exact number of bytes
                            that were read from the file(always <= payload.length)*/
                            byte[] temp = new byte[len];
                            for (int i = 0; i < len; i++)
                                temp[i] = payload[i];
                            payload = temp;
                        }
                        // Increment the packet id(slide the window)
                        packetId = (packetId + 1) % 2;
                    }
                    // We have a special header for the end of the file.
                    if (end)
                        header[0] = 2;  // The client will not recognise it as a packet
                    else
                        header[0] = (byte) packetId;

                    // Send the packet.
                    /*packetBuf = createPacketBuffer(header, payload);
                    DatagramPacket packet = new DatagramPacket(packetBuf, packetBuf.length, Address, Port);
                    udpSocket.send(packet);*/
                    sendPacket();
                    // Receive the acknowledgement(A header that contains the id of the packet we sent).
                    recvPacket(2);
                    // If no exception occured.
                    flag = false;
                    // Check the header.
                    if (header[0] != packetId)
                        flag = true;
                    // Simulate packet loss.
                    /*if (Math.random() < 0.5)
                        flag = true;*/

                } /*catch (SocketTimeoutException ste)
                {
                    // The timeout expired so we send the same packet.
                    flag = true;
                    System.out.println("Timeout");
                }*/ catch (IOException ioe)
                {
                    System.out.println("IOE");
                    ioe.printStackTrace();

                }
            }
        // If the socket cannot be opened or cannot be bound to the specific port
        } catch (SocketException se)
        {
            System.out.println("SE");
        // If the attempt to open the file has failed
        } catch (FileNotFoundException fnfe)
        {
            System.out.println("FNFE");

        } finally
        {
            try
            {
                fileIn.close();
            } catch (IOException ioe)
            {

            }
        }
    }

    private void initiateandshake() {

    //    recvPacket(2);

        recvPacket(2);
        Address = packet.getAddress();
        Port = packet.getPort();
        takeHeader();
        takePayload(packet);
        path = new String(payload,0,payload.length);
        System.out.print(path);
    }

    public static void main(String args[]) throws UnknownHostException
    {
        new Server().initializeServer();
    }
}
