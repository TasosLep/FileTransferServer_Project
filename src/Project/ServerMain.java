import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerMain {

    public static void main(String[] args) throws UnknownHostException {

        InetAddress addr;
        int port;

        if(args.length < 4) inputPrompt();
        if(!args[0].equals("-ip") || !args[2].equals("-p")) inputPrompt();

        System.out.println("Launched Server");

        addr = InetAddress.getByName(args[1]);
        port = Integer.parseInt(args[3]);

        System.out.println("Server IP: \"" + addr + "\" and port: " + port + " will be used for UDP connections.");

        Server udpServer = new Server();
        udpServer.initializeServer(port, addr);


    }

    private static void inputPrompt(){
        System.out.println("Usage is: java ServerMain -ip <server ip address> -p <server port>");
        System.exit(0);
    }
}
