import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Message implements Runnable{
    Socket mySocket;

    public Message(){}

    public Message(Socket mySocket){
        this.mySocket=mySocket;
    }

    public void setMsg(String msg){
    }

    @Override
    public void run(){
        while(true){
            try{
                Scanner  sc = new Scanner(System.in);
                String title = sc.nextLine();
                String author = sc.nextLine();
                String publisher = sc.nextLine();
                String xml="<?xml version = \"1.0\"?>"+
                        "<class>"+
                        "  <car mark = \"393\">"+
                        "    <cash>\""+title+"\"</cash>"+
                        "   <size>\""+author+"\"</size>"+
                        "  <serial>\""+publisher+"\"</serial>"+
                        "</car>"+
                        "</class>";
                OutputStream outToServer  = mySocket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                out.writeUTF(xml);
            } catch (IOException ex) {
                Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}