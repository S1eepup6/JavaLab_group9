import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
 
public class MultiServer {
    HashMap<String,HashMap<String,ServerRecThread>> globalMap;
    ServerSocket serverSocket = null;
    Socket socket = null;
    static int connUserCount = 0;
    public MultiServer(){
        globalMap = new HashMap<String,HashMap<String, ServerRecThread>>();
        Collections.synchronizedMap(globalMap);
        HashMap<String,ServerRecThread> group01 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group01);
        HashMap<String,ServerRecThread> group02 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group02);
        HashMap<String,ServerRecThread> group03 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group03);
        HashMap<String,ServerRecThread> group04 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group04);
        HashMap<String,ServerRecThread> group05 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group05);
        HashMap<String,ServerRecThread> group06 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group06);
        HashMap<String,ServerRecThread> group07 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group07);
        HashMap<String,ServerRecThread> group08 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group08);
        HashMap<String,ServerRecThread> group09 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group09);
        HashMap<String,ServerRecThread> group10 = new HashMap<String,ServerRecThread>();
        Collections.synchronizedMap(group10);
        globalMap.put("Chatroom1",group01);
        globalMap.put("Chatroom2",group02);
        globalMap.put("Chatroom3",group03);
        globalMap.put("Chatroom4",group04);
        globalMap.put("Chatroom5",group05);
        globalMap.put("Chatroom6",group06);
        globalMap.put("Chatroom7",group07);
        globalMap.put("Chatroom8", group08);
        globalMap.put("Chatroom9", group09);
        globalMap.put("Chatroom10",  group10);
    }
    public void init(){
        try{
            serverSocket = new ServerSocket(9999);
            System.out.println("##Server is started.");
            while (true){
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress()+":"+socket.getPort());
                Thread srthr = new ServerRecThread(socket);
                srthr.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void sendAllMsg(String msg){
        Iterator global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                HashMap<String, ServerRecThread> it_hash = globalMap.get(global_it.next());
                Iterator it = it_hash.keySet().iterator();
                while(it.hasNext()){
                    ServerRecThread srt = it_hash.get(it.next());
                    srt.out.writeUTF(msg);
                }              
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }
    }
    public void sendGroupMsg(String loc, String msg){      
        HashMap<String, ServerRecThread> gMap = globalMap.get(loc);    
        Iterator<String> group_it = globalMap.get(loc).keySet().iterator();        
        while(group_it.hasNext()){
            try{    
                    ServerRecThread st = gMap.get(group_it.next());
                    if(!st.chatMode){
                        st.out.writeUTF(msg);  
                    }
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }  
    }
    public void sendPvPMsg(String loc,String fromName, String toName, String msg){
            try {
                globalMap.get(loc).get(toName).out.writeUTF(msg);
                globalMap.get(loc).get(fromName).out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }  
    }
    public void sendToMsg(String loc, String fromName, String toName, String msg){     
        try{
                globalMap.get(loc).get(toName).out.writeUTF("whisper|"+fromName+"|"+msg);
                globalMap.get(loc).get(fromName).out.writeUTF("whisper|"+fromName+"|"+msg);
           }catch(Exception e){
                System.out.println("Exception:"+e);
           }
         
     }
    public String getEachMapSize(){
        return getEachMapSize(null);    
    }
    public String getEachMapSize(String loc){
        Iterator global_it = globalMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        int sum=0;
        sb.append("=== List of Group ==="+System.getProperty("line.separator"));
        while(global_it.hasNext()){
            try{
                String key = (String) global_it.next();
                HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                int size = it_hash.size();
                sum +=size;
                sb.append(key+": ("+size+" people)"+(key.equals(loc)?"(*)":"")+"\r\n");
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }
        sb.append("⊙Joining People :"+ sum+ " people \r\n");
        return sb.toString();
    }
     
public boolean isNameGlobal(String name){
        boolean result=false;
        Iterator<String> global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                String key = global_it.next();             
                HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                if(it_hash.containsKey(name)){
                    result= true;
                    break;
                }               
            }catch(Exception e){
                System.out.println("isNameGlobal()Exception:"+e);
            }
        }
        return result;
    }

    public String nVL(String str, String replace){
        String output="";
        if(str==null || str.trim().equals("")){
            output = replace;      
        }else{
            output = str;
        }
        return output;     
    }

    public static void main(String[] args) {
        MultiServer ms = new MultiServer();
        ms.init();
    }  

    class ServerRecThread extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name="";
        String loc="";
        String toNameTmp = null;
        String fileServerIP;
        String filePath;
        boolean chatMode;
       
       public ServerRecThread(Socket socket){
            this.socket = socket;
            try{
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                System.out.println("ServerRecThread Exception:"+e);
            }
        }    
        public String showUserList(){
            StringBuilder output = new StringBuilder("==List of Users==\r\n");
            Iterator it = globalMap.get(loc).keySet().iterator();
            while(it.hasNext()){
                 try{
                    String key= (String) it.next();
                    if(key.equals(name)){
                        key += " (*) ";
                    }
                    output.append(key+"\r\n");                  
                 }catch(Exception e){
                     System.out.println("예외:"+e);
                 }
             }
            output.append("=="+ globalMap.get(loc).size()+" people online==\r\n");
            System.out.println(output.toString());
            return output.toString();
         }
        public String[] getMsgParse(String msg){
            System.out.println("msgParse():msg?   "+ msg);         
            String[] tmpArr = msg.split("[|]");        
            return tmpArr;
        }
        @Override
        public void run(){
            HashMap<String, ServerRecThread> clientMap=null;
            try{  
                while(in!=null){
                    String msg = in.readUTF();                 
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    if(msg.startsWith("LOGON")){
                        if(!(msgArr[0].trim().equals(""))&&!isNameGlobal(msgArr[0])){                      
                            name = msgArr[0];
                            MultiServer.connUserCount++;
                            out.writeUTF("logon#yes|"+getEachMapSize());
                        }else{
                             out.writeUTF("logon#no|err01");
                        }
                    }else if(msg.startsWith("ENTERROOM")){
                         loc = msgArr[1];
                         if(isNameGlobal(msgArr[0])){
                             out.writeUTF("logon#no|"+name);  
                         }else if(globalMap.containsKey(loc)){
                             sendGroupMsg(loc, "show|[##] "+name + " Entered the room.");
                             clientMap= globalMap.get(loc);
                             clientMap.put(name, this);
                             System.out.println(getEachMapSize());                    
                             out.writeUTF("enterRoom#yes|"+loc);
                         }else{                        
                             out.writeUTF("enterRoom#no|"+loc);                              
                         }
                         
                    }else if(msg.startsWith("COMMAND")){ 
                        if(msgArr[1].trim().equals("/users")){
                            out.writeUTF("show|"+showUserList());                       
                        }else if(msgArr[1].trim().startsWith("/whisper")){
                            String[] msgSubArr = msgArr[1].split(" ",3);                        
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of whipser is wrong.\r\n usage : /whisper [receiver] [message].");
                            }else if(name.equals(msgSubArr[1])){
                                out.writeUTF("show|[##] You cannot whisper to yourself.\r\n usage : /whisper [receiver] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){
                                    System.out.println("Whisper!");
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] User does not exist.");
                                }
                            }                         
                        }else if(msgArr[1].trim().startsWith("/changeroom")){
                            String[] msgSubArr = msg.split(" ");                           
                            if(msgSubArr.length==1){                    
                                out.writeUTF("show|"+getEachMapSize(loc));                             
                            }else if(msgSubArr.length==2) {
                                String tmpLoc = msgSubArr[1];
                                if(loc.equals(tmpLoc)){
                                    out.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose current room.\r\n " +
                                                "usage : Showing roomlist : /changeroom" +
                                                "\r\n usage : Changing room : /changeroom [roomname].");
                                    continue;
                                }
                                if(globalMap.containsKey(tmpLoc)&& !this.chatMode){
                                        out.writeUTF("show|[##] Change "+loc+" to "+ tmpLoc+". ");                        
                                        clientMap.remove(name);
                                        sendGroupMsg(loc, "show|[##] "+name+"exit the room.");
                                        System.out.println("Remove " + name + " at previous room (" + loc + ")");
                                        loc = tmpLoc;
                                        clientMap = globalMap.get(loc);
                                        sendGroupMsg(loc, "show|[##] "+name+" entered the room.");
                                        clientMap.put(name, this);
                                }else{
                                    out.writeUTF("##The chatroom does not exist or you cannot move now.");
                                }
                            }else{
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n " +
                                        "usage : Showing list of chatroom : /changeroom" +
                                        "\r\n usage : Changing chatroom : /changeroom [Name of chatroom].");
                            }
                        }else if(msgArr[1].trim().startsWith("/pvp")){
                            String[] msgSubArr =  msgArr[1].split(" ",2);
                            if(msgSubArr.length!=2){
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n " +
                                        "usage : Request PvP chat : /pvp [Opponent's name]");
                                continue;
                            }else if(name.equals(msgSubArr[1])){
                                    out.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose your own name. Please choose opponent's name.\r\n " +
                                            "usage : Requesting PvP chat : /pvp [Opponent's name]");
                                continue;
                            }
                            if(!chatMode){
                                String toName = msgSubArr[1].trim();
                                out.writeUTF("show|[##] "+ "Request PvP chat to " + toName);
                                if(clientMap.containsKey(toName) && !clientMap.get(toName).chatMode){
                                    clientMap.get(toName).out.writeUTF("req_PvPchat|[##] "+name+" requested PvP Chat\r\n Would you accept?(y,n)");  
                                    toNameTmp = toName;
                                    clientMap.get(toNameTmp).toNameTmp = name;
                                }else{
                                    out.writeUTF("show|[##] User does not exist or user can't do PvP chat.");
                                }
                            }else{
                                out.writeUTF("show|[##] You cannot request pvp chat when you're in PvP chat.");
                            }
                        }else if(msgArr[1].startsWith("/end")){
                            if(chatMode){
                                chatMode = false;
                                out.writeUTF("show|[##] Quit PvP chat with " +toNameTmp);
                                clientMap.get(toNameTmp).chatMode=false;
                                clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name +" quit PvP chat");
                                toNameTmp="";
                                clientMap.get(toNameTmp).toNameTmp="";
                            }else{
                                out.writeUTF("show|[##] You can use this command only in PvP chat. ");
                            }
                        }else if(msgArr[1].trim().startsWith("/filetransfer")){
                            if(!chatMode){
                                out.writeUTF("show|[##] You can use this command only in PvP chat. ");
                                continue;                              
                            }
                            String[] msgSubArr = msgArr[1].split(" ",2);
                            if(msgSubArr.length!=2){
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /filetransfer [Path of sendingfile]");
                                continue;                              
                            }
                            filePath = msgSubArr[1];                           
                            File sendFile = new File(filePath);
                            String availExtList = "txt,java,jpeg,jpg,png,gif,bmp";
                            if(sendFile.isFile()){                     
                                String fileExt = filePath.substring(filePath.lastIndexOf(".")+1);
                                if(availExtList.contains(fileExt)){
                                    Socket s = globalMap.get(loc).get(toNameTmp).socket;
                                    System.out.println("s.getInetAddress():IP of fileserver=>"+s.getInetAddress());
                                    fileServerIP = s.getInetAddress().getHostAddress();
                                    clientMap.get(toNameTmp).out.writeUTF("req_fileSend|[##] "+name +" is trying to send["+sendFile.getName()+"] \r\n수락하시겠습니까?(Y/N)");                        
                                    out.writeUTF("show|[##] "+ "Sending file["+sendFile.getAbsolutePath()+"] to " + toNameTmp);
                                }else{
                                    out.writeUTF("show|[##] You can't send this file. \r\nYou only can send file having extension with ["+availExtList+"].");                             
                                }
                            }else{                             
                                out.writeUTF("show|[##] The file does not exist.");                            
                            }
                        }
                        else{
                            out.writeUTF("show|[##] Wrong Command.");
                        }
                    }else if(msg.startsWith("SAY")){
                          if(!chatMode){
                            sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                            messageSave Saver = new messageSave(loc);
                            emotionReady emotion = new emotionReady(loc);
                            emotion.analyze(msgArr[1]);
                            Saver.save(name, msgArr[1]);
                          }else{
                            sendPvPMsg(loc, name,toNameTmp , "say|"+name+"|"+msgArr[1]);
                          }
                    }else if(msg.startsWith("req_whisper")){
                        if(msgArr[1].trim().startsWith("/whisper")){
                            String[] msgSubArr = msgArr[1].split(" ",3);
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /whisper [opponent's name] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] The user does not exist.");
                                }
                            }
                        }
                    }else if(msg.startsWith("PvPchat")){
                        //PvPchat|result                       
                        String result = msgArr[0];                             
                        if(result.equals("yes")){                              
                            chatMode = true;    
                            clientMap.get(toNameTmp).chatMode=true;
                            System.out.println("##Change mode to PvP chat");                               
                            try {
                                out.writeUTF("show|[##] Start PvP chat with "+toNameTmp);
                                clientMap.get(toNameTmp).out.writeUTF("show|[##] Start PvP chat with "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else /*(r.equals("no"))*/{
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" rejected pvp chat");
                        }                      
                    } else if(msg.startsWith("fileSend")){
                        String result = msgArr[0];
                        if(result.equals("yes")){
                            System.out.println("##filetransfer##YES");                             
                            try {                      
                                String tmpfileServerIP = clientMap.get(toNameTmp).fileServerIP;
                                String tmpfilePath = clientMap.get(toNameTmp).filePath;
                                clientMap.get(toNameTmp).out.writeUTF("fileSender|"+tmpfilePath);
                                String fileName = new File(tmpfilePath).getName();
                                out.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);
                                clientMap.get(toNameTmp).filePath="";
                                clientMap.get(toNameTmp).fileServerIP="";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" rejected filesend.");
                        }                     
                    }else if(msg.startsWith("req_exit")){
                    }
                }
            }catch(Exception e){
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
            }finally{
                if(clientMap!=null){
                    clientMap.remove(name);
                    sendGroupMsg(loc,"## "+ name + "exit the server.");
                    System.out.println("##Now "+(--MultiServer.connUserCount)+"people are(is) online.");
                }              
            }
        }
    }
}