import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    static boolean mode = false;
    static int State = 0;
    public static void main(String[] args) throws UnknownHostException, IOException {
        try{
            Socket socket = new Socket("localhost", 9999);  
            System.out.println("[##] Connected with Server......");
            Thread sender = new Sender(socket);  
            Thread receiver = new Receiver(socket);
            sender.start();
            receiver.start();
        }catch(Exception e){
            System.out.println("Exception[Client class]:"+e);
        }
    }
}

class Receiver extends Thread{
    Socket socket;
    DataInputStream dis;
    public Receiver(Socket socket){
        this.socket = socket;
        try{
            dis = new DataInputStream(this.socket.getInputStream());
        }catch(Exception e){
            System.out.println("Exception:"+e);
        }
    }  
    public String[] MsgParse(String msg) {
        String[] tmpArr = msg.split("[|]");
        return tmpArr;
    }
    public void run() {
        while(dis!=null) {
            try {
                String msg = dis.readUTF();
                String[] msgArr = MsgParse(msg.substring(msg.indexOf("|")+1));               
                if (msg.startsWith("logon#yes")) {
                    Client.State = 1;
                    System.out.println(msgArr[0]);
                    System.out.println("¢ºInput room name : ");
                }
                else if (msg.startsWith("logon#no")) {
                    Client.State = 0;                 
                    System.out.println("[##] The name you chose already exists.\n¢ºPlease input name again : ");
                }
                else if (msg.startsWith("enterRoom#yes")) {
                    System.out.println("[##] You entered ("+msgArr[0]+").");
                    messageSave loader = new messageSave(msgArr[0]);
                    loader.load();
                    Client.State = 2;
                }
                else if (msg.startsWith("enterRoom#no")) {
                     System.out.println("[##] ["+msgArr[0]+ "] does not exist.");
                     System.out.println("¢ºPlease input room name again:");
                }
                else if (msg.startsWith("show")) {
                    System.out.println(msgArr[0]);
                }
                else if (msg.startsWith("say")){
                    System.out.println("["+msgArr[0]+"] "+msgArr[1] );
                }
                else if (msg.startsWith("whisper")) {
                    System.out.println("[whisper]["+msgArr[0]+"] "+msgArr[1] );
                }
                else if (msg.startsWith("req_PvPchat")) { 
                    Client.State = 3;
                    System.out.println(msgArr[0]);
                    System.out.print("¢ºChoose:");
                }else if(msg.startsWith("req_fileSend")) {
                    Client.State = 5;
                    System.out.println(msgArr[0]);
                    System.out.print("¢ºChoose:");  
                    sleep(100);
                }else if(msg.startsWith("fileSender")) {
                    System.out.println("fileSender:"+InetAddress.getLocalHost().getHostAddress());
                    System.out.println("fileSender:"+msgArr[0]);
                    try {
                        new FileSender(msgArr[0]).start();
                    } catch (Exception e) {
                        System.out.println("FileSender Thread Error:");
                        e.printStackTrace();
                    }
                }
                else if(msg.startsWith("fileReceiver")) {
                    System.out.println("fileReceiver:"+InetAddress.getLocalHost().getHostAddress());
                    System.out.println("fileReceiver:"+msgArr[0]+"/"+msgArr[1]);
                    String ip = msgArr[0];
                    String fileName = msgArr[1];
                    try {
                        new FileReceiver(ip,fileName).start();
                    } catch (Exception e) {
                        System.out.println("FileSender Thread Error:");
                        e.printStackTrace();
                    }
                }  
            } catch(SocketException e) {             
                 System.out.println("Exception:"+e);
                 System.out.println("##Disconnected with server.");
                return;
            } catch(Exception e) {              
                System.out.println("Receiver:run() Exception:"+e);
            }
        }
    }
}

class Sender extends Thread {
    Socket socket;
    DataOutputStream dos;
    String name;
    public Sender(Socket socket){
        this.socket = socket;      
        try{
            dos = new DataOutputStream(this.socket.getOutputStream());
        }catch(Exception e){
            System.out.println("Exception:"+e);
        }
    }
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("¢ºPlease input your name:");
        while(dos!=null) {
            try {                  
               String msg = scanner.nextLine();
               if (msg==null||msg.trim().equals("")) {
                   msg=" ";
               }
               if (Client.State == 0){            
                     if (!msg.trim().equals("")) {                 
                         name=msg;
                         dos.writeUTF("LOGON|"+msg);
                     }
                     else {
                         System.out.println("[##] You cannot input blank name.\r\n" +
                                "¢ºPlease input name again:");
                     }            
               }
               else if (Client.State == 1) {
                     if(!msg.trim().equals("")){                 
                         dos.writeUTF("ENTERROOM|"+name+"|"+msg);                      
                     }else{
                         System.out.println("[##] You cannot input blank.\r\n" +
                                "¢ºPlease input room name again:");
                     }
               }
               else if (msg.trim().startsWith("/")) {
                    if (msg.equalsIgnoreCase("/exit")) {
                      System.out.println("[##] Shutdown Client.");
                      System.exit(0);
                      break;
                    }
                    else {
                        dos.writeUTF("COMMAND|"+name+"|"+msg);
                    }
                }
               else if (Client.State==3) {
                    msg = msg.trim();                   
                    if (msg.equalsIgnoreCase("y")) {
                        dos.writeUTF("PvPchat|yes");                           
                    }
                    else if(msg.equalsIgnoreCase("n")){
                        dos.writeUTF("PvPchat|no");                                
                    }
                    else{                       
                        System.out.println("Wrong input.");  
                        dos.writeUTF("PvPchat|no");  
                    }
                    Client.State=2;              
                }
               else if (Client.State == 5) {
                    if (msg.trim().equalsIgnoreCase("y")) {
                        dos.writeUTF("fileSend|yes");                          
                    }
                    else if (msg.trim().equalsIgnoreCase("n")) {
                        dos.writeUTF("fileSend|no");                               
                    }
                    else {
                        System.out.println("Wrong input.");
                        dos.writeUTF("fileSend|no");          
                    }
                    Client.State=2;
                }else{
                    dos.writeUTF("SAY|"+name+"|"+msg);
                }
            }catch(SocketException e){
                 System.out.println("Sender:run()Exception:"+e);
                 System.out.println("##Disconnected with server.");
                return;              
           } catch (IOException e) {
                System.out.println("Exception:"+e);
           }
        }
    }
}