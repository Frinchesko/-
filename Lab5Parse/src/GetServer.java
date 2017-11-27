import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetServer extends Thread {
    private ServerSocket serverSocket;
    public List<Socket> clients=new ArrayList<Socket>();
    public List<Client> clientsHandler=new ArrayList<Client>();

    public GetServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    void updateConnections(List<Socket> clients){
        for(int i=0;i<this.clientsHandler.size();i++){
            this.clientsHandler.get(i).setClients(clients);
        }
    }

    public void run() {
        while(true) {
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                clients.add(server);
                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                Client clientHandler=new Client(server,clients);
                clientsHandler.add(clientHandler);
                Thread newClient=new Thread(clientHandler);
                newClient.start();
                updateConnections(clients);
            } catch(SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch(IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = 7777;
        try {
            Thread t = new GetServer(port);
            t.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}