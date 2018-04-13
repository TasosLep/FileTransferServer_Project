package Project;

import java.io.*;
import java.net.*;

public class Server
{
    private InetAddress Address;
    private int Port = 0;
    private FileInputStream fileIn;
    private DatagramSocket udpSocket;
    private byte[] header;
    private byte[] payload;
    private byte[] packetBuf;
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
        byte[] out = new byte[header.length + payload.length];
        for (int i = 0; i < header.length; i++)
            out[i] = header[i];
        for (int i = 0; i < payload.length; i++)
            out[i + header.length] = payload[i];
        return out;
    }

    public void sendPacket() throws IOException
    {
        packetBuf = createPacketBuffer(header, payload);
        DatagramPacket packet = new DatagramPacket(packetBuf, packetBuf.length, Address, Port);
        udpSocket.send(packet);
    }

    public void recvPacket(int sec)
    {
        try
        {
            udpSocket.setSoTimeout(sec * 1000);
            DatagramPacket packet = new DatagramPacket(header, header.length);
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
        Address = InetAddress.getByName("localhost");
        Port = Integer.parseInt("7778");
        try
        {
            udpSocket = new DatagramSocket(7777);
            //int packetNum = 0;
            //boolean flag = false;
            fileIn = new FileInputStream(new File("/home/marios/Downloads/Blade.Runner.2049/Blade.Runner.2049.mkv"));
            //fileIn = new FileInputStream(new File("/home/marios/Programming/cpp_prog/test.cpp"));
            boolean end = false;
            header = new byte[1];
            payload = new byte[60000];
            int packetId = -1;

            while (!end)
            {
                try
                {
                    if (!flag)
                    {
                        int len = fileIn.read(payload);
                        if (len == -1)
                            end = true;
                        else
                        {
                            // Adjust the payload length to the exact number of bytes
                            // that were read from the file(always <= payload.length).
                            byte[] temp = new byte[len];
                            for (int i = 0; i < len; i++)
                                temp[i] = payload[i];
                            payload = temp;
                        }
                        // Increment the packet id(slide the window).
                        packetId = (packetId + 1) % 2;
                    }
                    // We have a special header for the end of the file.
                    if (end)
                        header[0] = 2;
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

        } catch (SocketException se)
        {
            System.out.println("SE");

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

    public static void main(String args[]) throws UnknownHostException
    {
        new Server().initializeServer();
    }
}
