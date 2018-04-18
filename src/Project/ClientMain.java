import java.io.IOException;
import java.net.InetAddress;

public class ClientMain {

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
        //serverAddress = InetAddress.getByName("localhost");
        //serverPort = Integer.parseInt("7777");
        //new Client().initializeClient();
    }
}
