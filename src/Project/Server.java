package Project;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.sql.SQLSyntaxErrorException;

public class Server
{
    private  InetAddress Address;
    private  int Port=0;
    private FileInputStream fileIn;
    private DatagramSocket udpSocket;

    public void initializeServer() throws UnknownHostException
    {
        Address = InetAddress.getByName("localhost");
        Port = Integer.parseInt("7778");

        try
        {
            udpSocket = new DatagramSocket(7777);
            //int packetNum = 0;
            boolean flag = false;
            fileIn = new FileInputStream(new File("/home/marios/Programming/c_prog/test.c"));
            int length = 5;
            byte[] buf = new byte[length];
            byte[] ack = new byte[1];
            boolean end = false;
            while (!end)
            {
                try
                {
                    if (!flag)
                    {
                        if (fileIn.read(buf) == -1)
                        {
                            for(int i = 0; i<buf.length; i++)
                                buf[i] = 0;
                            end = true;
                            System.out.println("New data.");
                        }

                        //flag = false;
                    }

                    DatagramPacket packet = new DatagramPacket(buf, buf.length,Address,Port);
                    udpSocket.send(packet);
                    udpSocket.setSoTimeout(2 * 1000);
                    packet = new DatagramPacket(ack,ack.length);
                    udpSocket.receive(packet);
                    flag = false;
                    if (ack[0] != 1)
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

        }catch (FileNotFoundException fnfe)
        {
            System.out.println("FNFE");

        }finally
        {
            try{
                fileIn.close();
            }catch (IOException ioe)
            {

            }
        }
    }

    public static void main(String args[]) throws UnknownHostException
    {
        new Server().initializeServer();
    }
}
