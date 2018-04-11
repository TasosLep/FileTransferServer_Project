package Project;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.sql.SQLSyntaxErrorException;

public class Server
{
    private InetAddress Address;
    private int Port = 0;
    private FileInputStream fileIn;
    private DatagramSocket udpSocket;

    private byte[] createPacketBuffer(byte[] header, byte[] payoload)
    {
        byte[] out = new byte[header.length + payoload.length];
        for (int i = 0; i < header.length; i++)
            out[i] = header[i];
        for (int i = 0; i < payoload.length; i++)
            out[i+header.length] = payoload[i];
        return out;
    }

    public void initializeServer() throws UnknownHostException
    {
        Address = InetAddress.getByName("localhost");
        Port = Integer.parseInt("7778");
        try
        {
            udpSocket = new DatagramSocket(7777);
            //int packetNum = 0;
            boolean flag = false;
            fileIn = new FileInputStream(new File("/home/marios/Downloads/Blade.Runner.2049/Blade.Runner.2049.mkv"));
            boolean end = false;
            byte[] header = new byte[1];

            byte[] payload = new byte[60000];
            byte[] buf;
            int packetId = -1;

            while (!end)
            {
                try
                {
                    if (!flag)
                    {
                        int len = fileIn.read(payload);
                        if (len == -1)
                        {
                            end = true;
                        }else
                        {
                            byte[] temp = new byte[len];
                            for (int i = 0; i < len; i++)
                                temp[i] = payload[i];
                            payload = temp;
                        }
                        packetId = (packetId + 1) % 2;
                    }
                    if (end)
                        header[0] = 2;
                    else
                        header[0] = (byte) packetId;
                    //System.out.println(header[0] + " header we send");
                    buf = createPacketBuffer(header, payload);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, Address, Port);
                    udpSocket.send(packet);
                    udpSocket.setSoTimeout(2 * 1000);
                    packet = new DatagramPacket(header, header.length);
                    udpSocket.receive(packet);
                    flag = false;
                    //System.out.println(header[0] + " " + packetId);
                    if (header[0] != packetId)
                    {

                        flag = true;
                    }

                } catch (SocketTimeoutException ste)
                {
                    flag = true;
                    System.out.println("Timeout");
                } catch (IOException ioe)
                {
                    System.out.println("IOE");

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
