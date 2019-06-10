import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
 
public class Server {
    HashMap<String,HashMap<String,ServerThread>> globalMap;
    ServerSocket serverSocket = null;
    Socket socket = null;
    static int UserCount = 0;
    emotionReady emotion = new emotionReady();
    public Server() {
        globalMap = new HashMap<String,HashMap<String, ServerThread>>();
        Collections.synchronizedMap(globalMap);
        HashMap<String,ServerThread> room01 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room01);
        HashMap<String,ServerThread> room02 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room02);
        HashMap<String,ServerThread> room03 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room03);
        HashMap<String,ServerThread> room04 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room04);
        HashMap<String,ServerThread> room05 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room05);
        HashMap<String,ServerThread> room06 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room06);
        HashMap<String,ServerThread> room07 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room07);
        HashMap<String,ServerThread> room08 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room08);
        HashMap<String,ServerThread> room09 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room09);
        HashMap<String,ServerThread> room10 = new HashMap<String,ServerThread>();
        Collections.synchronizedMap(room10);
        globalMap.put("Chatroom1",room01);
        globalMap.put("Chatroom2",room02);
        globalMap.put("Chatroom3",room03);
        globalMap.put("Chatroom4",room04);
        globalMap.put("Chatroom5",room05);
        globalMap.put("Chatroom6",room06);
        globalMap.put("Chatroom7",room07);
        globalMap.put("Chatroom8", room08);
        globalMap.put("Chatroom9", room09);
        globalMap.put("Chatroom10", room10);
    }
    public void init() {
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("==========Server is started.==========");
            while (true) {
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress()+":"+socket.getPort());
                Thread thr = new ServerThread(socket);
                thr.start();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void sendGroupMsg (String loc, String msg) {
    	emotion.init(loc);
    	double analyze;
    	String recommsg = null;
    	recommender recom = new recommender();
    	String[] msgArr = msg.split("[|]");
    	if (msg.startsWith("say")) {
    		analyze = emotion.analyze(msgArr[2]);
    		System.out.println("analyze : " + analyze);
        	System.out.println("msgArr[2] : " + msgArr[2]);
        	recommsg = recom.recommend(analyze);
        	System.out.println("Recommend : " + recommsg);
    	}
        HashMap<String, ServerThread> getMap = globalMap.get(loc);    
        Iterator<String> group_it = globalMap.get(loc).keySet().iterator();        
        while (group_it.hasNext()) {
            try {
                    ServerThread st = getMap.get(group_it.next());
                    if (!st.chatMode) {
                        st.dos.writeUTF(msg);
                        if (msg.startsWith("say")) st.dos.writeUTF(recommsg);
                    }
            } catch (Exception e) {
                System.out.println("Exception:"+e);
            }
        }  
    }
    public void sendPvPMsg(String loc,String fromName, String toName, String msg) {
            try {
                globalMap.get(loc).get(toName).dos.writeUTF(msg);
                globalMap.get(loc).get(fromName).dos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }  
    }
    public void sendToMsg(String loc, String fromName, String toName, String msg) {     
        try {
                globalMap.get(loc).get(toName).dos.writeUTF("whisper|"+fromName+"|"+msg);
                globalMap.get(loc).get(fromName).dos.writeUTF("whisper|"+fromName+"|"+msg);
           } catch (Exception e){
                e.printStackTrace();
           }   
    }
    public String getEachMapSize() {
        return getEachMapSize(null);    
    }
    
    public String getEachMapSize(String loc) {
        Iterator global_it = globalMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        int sum=0;
        sb.append("=== List of Group ==="+System.getProperty("line.separator"));
        while (global_it.hasNext()) {
            try {
                String key = (String) global_it.next();
                HashMap<String, ServerThread> it_hash = globalMap.get(key);
                int size = it_hash.size();
                sum +=size;
                sb.append(key+": ("+size+" people)"+(key.equals(loc)?"(*)":"")+"\r\n");
            } catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }
        sb.append("⊙Joining People :"+ sum+ " people \r\n");
        return sb.toString();
    }
     
    public boolean isNameGlobal(String name) {
        boolean result=false;
        Iterator<String> global_it = globalMap.keySet().iterator();
        while (global_it.hasNext()) {
            try {
                String key = global_it.next();             
                HashMap<String, ServerThread> it_hash = globalMap.get(key);
                if (it_hash.containsKey(name)) {
                    result= true;
                    break;
                }               
            } catch(Exception e) {
                System.out.println("isNameGlobal()Exception:"+e);
            }
        }
        return result;
    }

    public static void main (String[] args) {
        Server ms = new Server();
        ms.init();
    }  

    class ServerThread extends Thread {
        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;
        String name="";
        String loc="";
        String nametmp = null;
        String fileServerIP;
        String filePath;
        boolean chatMode;
        public ServerThread(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch(Exception e) {
                System.out.println("ServerRecThread Exception:"+e);
            }
        }
        public String showUserList() {
            StringBuilder output = new StringBuilder("==List of Users==\r\n");
            Iterator it = globalMap.get(loc).keySet().iterator();
            while (it.hasNext()) {
                 try {
                    String key= (String) it.next();
                    if(key.equals(name)){
                        key += " (*) ";
                    }
                    output.append(key+"\r\n");                  
                 } catch (Exception e){
                     System.out.println("Exception:"+e);
                 }
             }
            output.append("=="+ globalMap.get(loc).size()+" people online==\r\n");
            System.out.println(output.toString());
            return output.toString();
         }
        public String[] getMsgParse(String msg) {
            System.out.println("msgParse():msg?   "+ msg);         
            String[] tmpArr = msg.split("[|]");        
            return tmpArr;
        }
        public void run() {
            HashMap<String, ServerThread> clientMap=null;
            try{  
                while (dis!=null) {
                    String msg = dis.readUTF();                 
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    if (msg.startsWith("LOGON")) {
                        if (!(msgArr[0].trim().equals(""))&&!isNameGlobal(msgArr[0])) {                      
                            name = msgArr[0];
                            Server.UserCount++;
                            dos.writeUTF("logon#yes|"+getEachMapSize());
                        }
                        else {
                             dos.writeUTF("logon#no|err01");
                        }
                    }
                    else if (msg.startsWith("ENTERROOM")) {
                         loc = msgArr[1];
                         if (isNameGlobal(msgArr[0])) {
                             dos.writeUTF("logon#no|"+name);  
                         }
                         else if (globalMap.containsKey(loc)) {
                             sendGroupMsg(loc, "show|[##] "+name + " Entered the room.");
                             clientMap= globalMap.get(loc);
                             clientMap.put(name, this);
                             System.out.println(getEachMapSize());                    
                             dos.writeUTF("enterRoom#yes|"+loc);
                         }
                         else {
                             dos.writeUTF("enterRoom#no|"+loc);                              
                         }
                    }
                    else if (msg.startsWith("COMMAND")) { 
                        if (msgArr[1].trim().equals("/users")) {
                            dos.writeUTF("show|"+showUserList());                       
                        }
                        else if (msgArr[1].trim().startsWith("/whisper")) {
                            String[] msgSubArr = msgArr[1].split(" ",3);                        
                            if (msgSubArr==null||msgSubArr.length<3) {
                                dos.writeUTF("show|[##] The usage of whipser is wrong.\r\n usage : /whisper [receiver] [message].");
                            }
                            else if (name.equals(msgSubArr[1])) {
                                dos.writeUTF("show|[##] You cannot whisper to yourself.\r\n usage : /whisper [receiver] [message].");
                            }
                            else {
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if (clientMap.containsKey(toName)) {
                                    System.out.println("Whisper!");
                                    sendToMsg(loc,name,toName,toMsg);
                                }
                                else {
                                    dos.writeUTF("show|[##] User does not exist.");
                                }
                            }                         
                        }
                        else if (msgArr[1].trim().startsWith("/changeroom")){
                            String[] msgSubArr = msg.split(" ");                           
                            if (msgSubArr.length==1){                    
                                dos.writeUTF("show|"+getEachMapSize(loc));                             
                            }
                            else if (msgSubArr.length==2) {
                                String tmpLoc = msgSubArr[1];
                                if (loc.equals(tmpLoc)){
                                    dos.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose current room.\r\n " +
                                                "usage : Showing roomlist : /changeroom" +
                                                "\r\n usage : Changing room : /changeroom [roomname].");
                                    continue;
                                }
                                if (globalMap.containsKey(tmpLoc)&& !this.chatMode){
                                        dos.writeUTF("show|[##] Change "+loc+" to "+ tmpLoc+". ");                        
                                        clientMap.remove(name);
                                        sendGroupMsg(loc, "show|[##] "+name+"exit the room.");
                                        System.out.println("Remove " + name + " at previous room (" + loc + ")");
                                        loc = tmpLoc;
                                        clientMap = globalMap.get(loc);
                                        sendGroupMsg(loc, "show|[##] "+name+" entered the room.");
                                        clientMap.put(name, this);
                                }
                                else {
                                    dos.writeUTF("##The chatroom does not exist or you cannot move now.");
                                }
                            }
                            else {
                                dos.writeUTF("show|[##] The usage of command is wrong.\r\n " +
                                        "usage : Showing list of chatroom : /changeroom" +
                                        "\r\n usage : Changing chatroom : /changeroom [Name of chatroom].");
                            }
                        }
                        else if (msgArr[1].trim().startsWith("/pvp")){
                            String[] msgSubArr =  msgArr[1].split(" ",2);
                            if (msgSubArr.length!=2) {
                                dos.writeUTF("show|[##] The usage of command is wrong.\r\n " +
                                        "usage : Request PvP chat : /pvp [Opponent's name]");
                                continue;
                            }
                            else if (name.equals(msgSubArr[1])) {
                                dos.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose your own name. Please choose opponent's name.\r\n " +
                                            "usage : Requesting PvP chat : /pvp [Opponent's name]");
                                continue;
                            }
                            if (!chatMode) {
                                String toName = msgSubArr[1].trim();
                                dos.writeUTF("show|[##] "+ "Request PvP chat to " + toName);
                                if (clientMap.containsKey(toName) && !clientMap.get(toName).chatMode) {
                                    clientMap.get(toName).dos.writeUTF("req_PvPchat|[##] "+name+" requested PvP Chat\r\n Would you accept?(y,n)");  
                                    nametmp = toName;
                                    clientMap.get(nametmp).nametmp = name;
                                }
                                else {
                                    dos.writeUTF("show|[##] User does not exist or user can't do PvP chat.");
                                }
                            }
                            else {
                                dos.writeUTF("show|[##] You cannot request pvp chat when you're in PvP chat.");
                            }
                        }
                        else if (msgArr[1].startsWith("/end")) {
                            if (chatMode) {
                                chatMode = false;
                                dos.writeUTF("show|[##] Quit PvP chat with " + nametmp);
                                clientMap.get(nametmp).chatMode=false;
                                clientMap.get(nametmp).dos.writeUTF("show|[##] "+name +" quit PvP chat");
                                clientMap.get(nametmp).nametmp="";
                                nametmp="";
                                clientMap = globalMap.get(loc);
                            }
                            else {
                                dos.writeUTF("show|[##] You can use this command only in PvP chat. ");
                            }
                        }
                        else if (msgArr[1].trim().startsWith("/filetransfer")){
                            if (!chatMode) {
                                dos.writeUTF("show|[##] You can use this command only in PvP chat. ");
                                continue;                              
                            }
                            String[] msgSubArr = msgArr[1].split(" ",2);
                            if (msgSubArr.length!=2) {
                                dos.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /filetransfer [Path of sendingfile]");
                                continue;                              
                            }
                            filePath = msgSubArr[1];                           
                            File sendFile = new File(filePath);
                            String availExtList = "txt,java,jpeg,jpg,png,gif,bmp";
                            if (sendFile.isFile()) {                     
                                String fileExt = filePath.substring(filePath.lastIndexOf(".")+1);
                                if(availExtList.contains(fileExt)){
                                    Socket s = globalMap.get(loc).get(nametmp).socket;
                                    System.out.println("s.getInetAddress():IP of fileserver=>"+s.getInetAddress());
                                    fileServerIP = s.getInetAddress().getHostAddress();
                                    clientMap.get(nametmp).dos.writeUTF("req_fileSend|[##] "+name +" is trying to send["+sendFile.getName()+"] \r\n수락하시겠습니까?(Y/N)");                        
                                    dos.writeUTF("show|[##] "+ "Sending file["+sendFile.getAbsolutePath()+"] to " + nametmp);
                                }
                                else {
                                    dos.writeUTF("show|[##] You can't send this file. \r\nYou only can send file having extension with ["+availExtList+"].");                             
                                }
                            }
                            else {                             
                                dos.writeUTF("show|[##] The file does not exist.");                            
                            }
                        }
                        else {
                            dos.writeUTF("show|[##] Wrong Command.");
                        }
                    }
                    else if(msg.startsWith("SAY")) {
                          if(!chatMode){
                        	  messageSave saver = new messageSave(loc);
                        	  saver.save(name, msgArr[1]);
                              sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                          }else {
                            sendPvPMsg(loc, name, nametmp , "say|"+name+"|"+msgArr[1]);
                          }
                    }
                    else if (msg.startsWith("req_whisper")) {
                        if (msgArr[1].trim().startsWith("/whisper")) {
                            String[] msgSubArr = msgArr[1].split(" ",3);
                            if (msgSubArr==null||msgSubArr.length<3) {
                                dos.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /whisper [opponent's name] [message].");
                            }
                            else {
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if (clientMap.containsKey(toName)) {
                                    sendToMsg(loc,name,toName,toMsg);
                                }
                                else {
                                    dos.writeUTF("show|[##] The user does not exist.");
                                }
                            }
                        }
                    } else if(msg.startsWith("PvPchat")) {
                        String result = msgArr[0];                             
                        if(result.equals("yes")){                              
                            chatMode = true;    
                            clientMap.get(nametmp).chatMode=true;
                            System.out.println("##Change mode to PvP chat");                               
                            try {
                                dos.writeUTF("show|[##] Start PvP chat with "+nametmp);
                                clientMap.get(nametmp).dos.writeUTF("show|[##] Start PvP chat with "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            clientMap.get(nametmp).dos.writeUTF("show|[##] "+name+" rejected pvp chat");
                        }                      
                    } else if (msg.startsWith("fileSend")) {
                        String result = msgArr[0];
                        if (result.equals("yes")) {
                            System.out.println("##filetransfer##YES");                             
                            try {                      
                                String tmpfileServerIP = clientMap.get(nametmp).fileServerIP;
                                String tmpfilePath = clientMap.get(nametmp).filePath;
                                clientMap.get(nametmp).dos.writeUTF("fileSender|"+tmpfilePath);
                                String fileName = new File(tmpfilePath).getName();
                                dos.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);
                                clientMap.get(nametmp).filePath="";
                                clientMap.get(nametmp).fileServerIP="";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            clientMap.get(nametmp).dos.writeUTF("show|[##] "+name+" rejected filesend.");
                        } 
                    }
                }
            } catch (Exception e) {
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
            } finally {
                if (clientMap!=null) {
                    clientMap.remove(name);
                    sendGroupMsg(loc,"## "+ name + "exit the server.");
                    System.out.println("##Now "+(--Server.UserCount)+" people are(is) online.");
                }              
            }
        }
    }
}