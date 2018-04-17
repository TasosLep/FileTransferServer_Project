package Project;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    private static int payload_length;
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

    private void takePayload(DatagramPacket packet) throws UnsupportedEncodingException {
        /*The payload length is the number of bytes read minus
        the number of the header length in bytes*/
  //      String aaa = new String(payload,"UTF-8");
    //    System.out.println("takePayload1   " + "\npath is  " + aaa + "\n");
        payload = new byte[packet.getLength() - header.length];
  //       aaa = new String(payload,"UTF-8");
  //      System.out.println("takePayload1   " + "\npath is  " + aaa + "\n");
        for (int i = 0; i < payload.length; i++)
            payload[i] = packetBuf[i + header.length];
 //       aaa = new String(payload,"UTF-8");
  //      System.out.println("takePayload1   " + "\npath is  " + aaa + "\n");
    }


    public void sendPacket() throws IOException
    {
        packetBuf = createPacketBuffer(header, payload);
        packet = new DatagramPacket(packetBuf, packetBuf.length, Address, Port);
        udpSocket.send(packet);
    }

    public void recvPacket_withoutTimeOut()
    {
        try
        {
            packetBuf = createPacketBuffer(header, payload);
          //  packetBuf = path.getBytes();
            packet = new DatagramPacket(packetBuf, packetBuf.length);
            udpSocket.receive(packet);
        }catch (SocketTimeoutException ste)
        {
            // The timeout expired so we send the same packet.
       //     flag = true;
            System.out.println("Timeout");
        }catch (IOException ioe)
        {
            System.out.println("IOE");
            ioe.printStackTrace();
        }
    }

    public void recvPacket(int sec)
    {
        try
        {
            udpSocket.setSoTimeout(sec * 1000);
            packetBuf = createPacketBuffer(header, payload);
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

    public void recvACK(int sec)
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
            System.out.println("Server started!");
            // Create the DatagramSocket through which the server will communicate with the client
            udpSocket = new DatagramSocket(7777);

            initiateHandshake();
            System.out.println("\n" + path);

            //int packetNum = 0;
            //boolean flag = false;
            fileIn = new FileInputStream(new File(path));
            //fileIn = new FileInputStream(new File("/home/marios/Programming/cpp_prog/test.cpp"));
            boolean end = false;
            header = new byte[1];
            payload = new byte[payload_length];
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
                    recvACK(2);
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
            System.out.println("We couldn't find a Server");
        // If the attempt to open the file has failed
        } catch (FileNotFoundException fnfe)
        {
            System.out.println("FNFE");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            try
            {
        //        System.out.print("Whyyy???");
                fileIn.close();
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    private void initiateHandshake() throws IOException {

        System.out.println("\nWaiting for a client!");

        header = new byte[1];
       // header[0] = 8;
        payload = new byte[60000];

        recvPacket_withoutTimeOut();
        takeHeader();
        takePayload(packet);

        if (header[0] == 7) {
        //    path = new String(packet.getData(), 0, packet.getLength()) + " from : ";

            path = new String(packet.getData(), 1, packet.getLength()) + " from : ";
            path = path.trim();
            String temp = new String(packet.getData(),"UTF-8");
            System.out.print("\n " + path);

        }

        packet = null;

        //recieving payload length from the user
        header = new byte[1];
 //       header[0] = 8;
        payload = new byte[60000];

        recvPacket_withoutTimeOut();
        takeHeader();
        takePayload(packet);
        if (header[0] == 8) {
            //   System.out.println(packet.getLength() + " why\n");
            String paylen = new String(packet.getData(), 0, packet.getLength());
            //   System.out.print("whaaaaaaaaaaaaaaat? " + paylen);
            int number = Integer.parseInt(paylen.trim());
            System.out.print("\n  " + number);
            payload_length = number;
        }
        //end of recieving payload length from the user


        //send welcome message
        header = new byte[1];
        header[0] = 9;
        payload = new byte[60000];
        String welcome = "Welcome!";
        payload = welcome.getBytes(StandardCharsets.UTF_8);
     //   System.out.print("\n"+payload.length+"\n");
        sendPacket();
        //end of welcome message

    }

    public static void main(String args[]) throws UnknownHostException
    {
        new Server().initializeServer();
    }
}