import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String serverName = "localhost";
        int port = 7777;
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Подключение " + serverName + " к порту " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Подключен к: " + client.getRemoteSocketAddress() + "\n");
            while (true) {
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                System.out.print("Сообщение к серверу: ");
                String str = scanner.nextLine();

                out.writeUTF(str);
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);

                System.out.println("Ответ сервера: " + in.readUTF());
                //client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
