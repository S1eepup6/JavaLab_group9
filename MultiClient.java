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

public class MultiClient {
    static boolean chatmode = false;
    static int chatState = 0;
    public static void main(String[] args) throws UnknownHostException, IOException {
        try{
            String ServerIP = "localhost";
            Socket socket = new Socket(ServerIP, 9999);  
            System.out.println("[##] Connected with Server......");
            Thread sender = new Sender(socket);  
            Thread receiver = new Receiver(socket);
            sender.start(); //스레드 시동
            receiver.start(); //스레드 시동           
        }catch(Exception e){
            System.out.println("Exception[MultiClient class]:"+e);
        }
    }
}

class Receiver extends Thread{
    Socket socket;
    DataInputStream in;
    public Receiver(Socket socket){
        this.socket = socket;
        try{
            in = new DataInputStream(this.socket.getInputStream());
        }catch(Exception e){
            System.out.println("Exception:"+e);
        }
   }  
   public String[] getMsgParse(String msg){
        String[] tmpArr = msg.split("[|]");
        return tmpArr;
    }
    public void run(){
        while(in!=null){
            try{
                String msg = in.readUTF();
                String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));               
                if(msg.startsWith("logon#yes")){
                    MultiClient.chatState = 1;
                    System.out.println(msgArr[0]);
                    System.out.println("▶Input room name : ");
                }else  if(msg.startsWith("logon#no")){
                    MultiClient.chatState = 0;                 
                    System.out.println("[##] The name you chose already exists.\n▶Please input name again : ");
                }else if(msg.startsWith("enterRoom#yes")){
                    System.out.println("[##] You entered ("+msgArr[0]+").");
                    messageSave loader = new messageSave(msgArr[0]);
                    loader.load();
                    MultiClient.chatState = 2;
                }else if(msg.startsWith("enterRoom#no")){
                    //enterRoom#no|지역
                     System.out.println("[##] ["+msgArr[0]+ "] does not exist.");
                     System.out.println("▶Please input room name again:");
                }else if(msg.startsWith("show")){
                    System.out.println(msgArr[0]);
                }else if(msg.startsWith("say")){
                    System.out.println("["+msgArr[0]+"] "+msgArr[1] ); 
                }else if(msg.startsWith("whisper")){
                    System.out.println("[whisper]["+msgArr[0]+"] "+msgArr[1] );
                }else if(msg.startsWith("req_PvPchat")){ 
                    MultiClient.chatState = 3;
                    System.out.println(msgArr[0]);
                    System.out.print("▶Choose:");
                }else if(msg.startsWith("req_fileSend")){
                    MultiClient.chatState = 5;
                    System.out.println(msgArr[0]);
                    System.out.print("▶Choose:");  
                    sleep(100);
                }else if(msg.startsWith("fileSender")){
                    System.out.println("fileSender:"+InetAddress.getLocalHost().getHostAddress());
                    System.out.println("fileSender:"+msgArr[0]);
                    try {
                        new FileSender(msgArr[0]).start();
                    } catch (Exception e) {
                        System.out.println("FileSender Thread Error:");
                        e.printStackTrace();
                    }
                }else if(msg.startsWith("fileReceiver")){
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
                }  else if(msg.startsWith("req_exit")){    
                }
            }catch(SocketException e){             
                 System.out.println("Exception:"+e);
                 System.out.println("##Disconnected with server.");
                return;
            } catch(Exception e){              
                System.out.println("Receiver:run() Exception:"+e);
            }
        }
    }
}

class Sender extends Thread {
    Socket socket;
    DataOutputStream out;
    String name;
    public Sender(Socket socket){
        this.socket = socket;      
        try{
            out = new DataOutputStream(this.socket.getOutputStream());
        }catch(Exception e){
            System.out.println("Exception:"+e);
        }
    }
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("▶Please input your name:");
        while(out!=null){
            try {                  
               String msg = scanner.nextLine();
               if (msg==null||msg.trim().equals("")){
                   msg=" ";
                }
               if (MultiClient.chatState == 0){            
                    //추후 대화명 관련 처리시 사용.                   
                     if(!msg.trim().equals("")){                 
                         name=msg;
                         out.writeUTF("LOGON|"+msg);
                       
                     }else{
                         System.out.println("[##] You cannot input blank name.\r\n" +
                                "▶Please input name again:");
                     }            
                }else if(MultiClient.chatState == 1) {
                     if(!msg.trim().equals("")){                 
                         out.writeUTF("ENTERROOM|"+name+"|"+msg);                      
                     }else{
                         System.out.println("[##] You cannot input blank.\r\n" +
                                "▶Please input room name again:");
                     }
                }else if(msg.trim().startsWith("/")){
                    if(msg.equalsIgnoreCase("/exit")){
                      System.out.println("[##] Shutdown Client.");
                      System.exit(0);
                      break;
                    }else{
                        out.writeUTF("COMMAND|"+name+"|"+msg);
                    }
                }else if(MultiClient.chatState==3){
                    msg = msg.trim();                   
                    if(msg.equalsIgnoreCase("y")){
                        out.writeUTF("PvPchat|yes");                           
                    }else if(msg.equalsIgnoreCase("n")){
                        out.writeUTF("PvPchat|no");                                
                    }else{                       
                        System.out.println("Wrong input.");  
                        out.writeUTF("PvPchat|no");  
                    }
                    MultiClient.chatState=2;              
                }else if(MultiClient.chatState == 5) {
                    if(msg.trim().equalsIgnoreCase("y")){
                        out.writeUTF("fileSend|yes");                          
                    }else if(msg.trim().equalsIgnoreCase("n")){
                        out.writeUTF("fileSend|no");                               
                    }else{
                        System.out.println("Wrong input.");
                        out.writeUTF("fileSend|no");          
                    }
                    MultiClient.chatState=2;
                }else{
                    out.writeUTF("SAY|"+name+"|"+msg);                 
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