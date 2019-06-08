import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
 
/*�ܼ� ��Ƽä�� ���� ���α׷�*/
public class MultiServer {
    HashMap<String,HashMap<String,ServerRecThread>> globalMap; //������ �ؽ����� �����ϴ� �ؽø�
    ServerSocket serverSocket = null;
    Socket socket = null;
    static int connUserCount = 0;
    public MultiServer(){
       globalMap = new HashMap<String,HashMap<String, ServerRecThread>>();
        //clientMap = new HashMap<String,DataOutputStream>(); //Ŭ���̾�Ʈ�� ��½�Ʈ���� ������ �ؽ��� ����.
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
            serverSocket = new ServerSocket(9999); //9999��Ʈ�� �������� ��ü����.
            System.out.println("##Server is started.");
            while(true){ //������ ����Ǵ� ���� Ŭ���̾�Ʈ���� ������ ��ٸ�.
                socket = serverSocket.accept(); //Ŭ���̾�Ʈ�� ������ ��ٸ��ٰ� ������ �Ǹ� Socket��ü�� ����.
                System.out.println(socket.getInetAddress()+":"+socket.getPort()); //Ŭ���̾�Ʈ ���� (ip, ��Ʈ) ���
                Thread msr = new ServerRecThread(socket); //������ ����.
                msr.start(); //������ �õ�.
            }      
           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /** ���ӵ� ��� Ŭ���̾�Ʈ�鿡�� �޽����� ����. */
    public void sendAllMsg(String msg){
        Iterator global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                HashMap<String, ServerRecThread> it_hash = globalMap.get(global_it.next());
                Iterator it = it_hash.keySet().iterator();
                while(it.hasNext()){
                    ServerRecThread st = it_hash.get(it.next());
                    st.out.writeUTF(msg);
                }              
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }
    }//sendAllMsg()-----------
    /**�ش� Ŭ���̾�Ʈ�� �����ִ� �׷쿡���ؼ��� �޽��� ����.*/
    public void sendGroupMsg(String loc, String msg){      
        HashMap<String, ServerRecThread> gMap = globalMap.get(loc);    
        Iterator<String> group_it = globalMap.get(loc).keySet().iterator();        
        while(group_it.hasNext()){
            try{    
                    ServerRecThread st = gMap.get(group_it.next());
                    if(!st.chatMode){ //1:1��ȭ��尡 �ƴ� ������Ը�.
                        st.out.writeUTF(msg);  
                    }
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }  
    }//sendGroupMsg()-----------
    /**1:1 ��ȭ*/
    public void sendPvPMsg(String loc,String fromName, String toName, String msg){
     
            try {
                globalMap.get(loc).get(toName).out.writeUTF(msg);
                globalMap.get(loc).get(fromName).out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
         
    }//sendPvPMsg()-----------
   
    /** �ӼӸ� */
    public void sendToMsg(String loc, String fromName, String toName, String msg){     
        try{
                globalMap.get(loc).get(toName).out.writeUTF("whisper|"+fromName+"|"+msg);
                globalMap.get(loc).get(fromName).out.writeUTF("whisper|"+fromName+"|"+msg);
               
           }catch(Exception e){
                System.out.println("Exception:"+e);
           }
         
     }
    /**���׷��� �����ڼ��� ������ ���ӵ� ������ ��ȯ
     * �ϴ� �޼ҵ�**/
    public String getEachMapSize(){
        return getEachMapSize(null);    
    }//getEachMapSize()-----------
   
    /**���׷��� �����ڼ��� ������ ���ӵ� ������ ��ȯ �ϴ� �޼ҵ�
     * �߰� ������ ���޹����� �ش� ������ üũ
     * */
    public String getEachMapSize(String loc){
       
        Iterator global_it = globalMap.keySet().iterator();
        StringBuffer sb = new StringBuffer();
        int sum=0;
        sb.append("=== List of Group ==="+System.getProperty("line.separator"));
        while(global_it.hasNext()){
            try{
                String key = (String) global_it.next();
               
                HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                //if(key.equals(loc)) key+="(*)"; //���� ������ ���ӵ� �� ǥ��
                int size = it_hash.size();
                sum +=size;
                sb.append(key+": ("+size+"people)"+(key.equals(loc)?"(*)":"")+"\r\n");
               
            }catch(Exception e){
                System.out.println("����:"+e);
            }
        }
        //sb.append("������ ��ȭ�� �����ϰ��ִ� ������ :"+ MultiServer.connUserCount);
        sb.append("��Joining People :"+ sum+ " people \r\n");
        //System.out.println(sb.toString());
        return sb.toString();
    }//getEachMapSize()-----------
   
   
    /**���ӵ� ���� �ߺ�üũ*/        
public boolean isNameGlobal(String name){
        boolean result=false;
        Iterator<String> global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                String key = global_it.next();             
                HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                if(it_hash.containsKey(name)){
                    result= true; //�ߺ��� ���̵� ����.
                    break;
                }
               
            }catch(Exception e){
                System.out.println("isNameGlobal()����:"+e);
            }
        }
        return result;
    }
 
   
    /**���ڿ� null �� �� "" �� ��ü ���ڿ��� ���԰���.*/
    public String nVL(String str, String replace){
        String output="";
        if(str==null || str.trim().equals("")){
            output = replace;      
        }else{
            output = str;
        }
        return output;     
    }
    
    //main�޼���
    public static void main(String[] args) {
        MultiServer ms = new MultiServer(); //������ü ����.
        ms.init();//����.
    }//main()------  
   
   
   
    ////////////////////////////////////////////////////////////////////////
    //----// ���� Ŭ���� //--------//
   
    // Ŭ���̾�Ʈ�κ��� �о�� �޽����� �ٸ� Ŭ���̾�Ʈ(socket)�� ������ ������ �ϴ� �޼���
    class ServerRecThread extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name=""; //�̸� ����
        String loc="";  //���� ����
        String toNameTmp = null;//1:1��ȭ ���  
        String fileServerIP; //���ϼ��� ������ ����
        String filePath; //���� �������� ������ ���� �н� ����.
        boolean chatMode; //1:1��ȭ��� ����
       
       public ServerRecThread(Socket socket){
            this.socket = socket;
            try{
                //Socket���κ��� �Է½�Ʈ���� ��´�.
                in = new DataInputStream(socket.getInputStream());
                //Socket���κ��� ��½�Ʈ���� ��´�.
                out = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                System.out.println("ServerRecThread ������ ����:"+e);
            }
        }//������ ------------
       
       
       
        /**���ӵ� ��������Ʈ  ���ڿ��� ��ȯ*/        
        public String showUserList(){
           
            StringBuilder output = new StringBuilder("==List of Users==\r\n");
            Iterator it = globalMap.get(loc).keySet().iterator(); //�ؽ��ʿ� ��ϵ� ������̸��� ������.
            while(it.hasNext()){ //�ݺ��ϸ鼭 ������̸��� StringBuilder�� �߰�
                 try{
                    String key= (String) it.next();                                    
                    //out.writeUTF(output);
                    if(key.equals(name)){ //�������� üũ
                        key += " (*) ";
                    }
                    output.append(key+"\r\n");                  
                 }catch(Exception e){
                     System.out.println("����:"+e);
                 }
             }//while---------
            output.append("=="+ globalMap.get(loc).size()+"people online==\r\n");
            System.out.println(output.toString());
            return output.toString();
         }//showUserList()-----------
       /**�޽��� �ļ� */    
       public String[] getMsgParse(String msg){
            System.out.println("msgParse():msg?   "+ msg);         
            String[] tmpArr = msg.split("[|]");        
            return tmpArr;
        }
        @Override
        public void run(){ //�����带 ����ϱ� ���ؼ� run()�޼��� ������
            HashMap<String, ServerRecThread> clientMap=null;   //���� Ŭ���̾�Ʈ�� ����Ǿ��ִ� �ؽ���        
            try{  
                while(in!=null){ //�Է½�Ʈ���� null�� �ƴϸ� �ݺ�.
                    String msg = in.readUTF(); //�Է½�Ʈ���� ���� �о�� ���ڿ��� msg�� �Ҵ�.                   
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    //�޼��� ó�� ----------------------------------------------
                    if(msg.startsWith("req_logon")){ //�α׿� �õ� (��ȭ��)                    
                        //req_logon|��ȭ��
                        if(!(msgArr[0].trim().equals(""))&&!isNameGlobal(msgArr[0])){                      
                            name = msgArr[0]; //�Ѿ�� ��ȭ���� �������� name�� ����
                            MultiServer.connUserCount++; //�����ڼ� ����. (����ƽ������ ����ϱ⿡ ������ �ѹ� ����غ�.)
                            out.writeUTF("logon#yes|"+getEachMapSize()); //���ӵ� Ŭ���̾�Ʈ���� �׷��� ����
                        }else{
                             out.writeUTF("logon#no|err01");
                        }
                    }else if(msg.startsWith("req_enterRoom")){ //�׷������� �õ�
                       
                        //req_enterRoom|��ȭ��|����
                         loc = msgArr[1]; //�޽������� �����κи� �����Ͽ� ���������� ����
                         
                         if(isNameGlobal(msgArr[0])){
                             out.writeUTF("logon#no|"+name);  
                             
                         }else if(globalMap.containsKey(loc)){
                             sendGroupMsg(loc, "show|[##] "+name + "Entered the room.");
                             clientMap= globalMap.get(loc); //����׷��� �ؽ����� ���� ����.
                             clientMap.put(name, this); //���� MultiServerRec�ν��Ͻ��� Ŭ���̾�Ʈ�ʿ� ����.
                             System.out.println(getEachMapSize()); //������ �׷츮��Ʈ ���.                       
                             out.writeUTF("enterRoom#yes|"+loc); //���ӵ� Ŭ���̾�Ʈ���� �׷��� ����
                             
                         }else{                        
                             out.writeUTF("enterRoom#no|"+loc);                              
                         }
                         
                    }else if(msg.startsWith("req_cmdMsg")){ //��ɾ� ����
                        //req_cmdMsg|��ȭ��|/������
                        if(msgArr[1].trim().equals("/users")){
                            out.writeUTF("show|"+showUserList()); //������ ���                             
                        }else if(msgArr[1].trim().startsWith("/whisper")){
                            //req_cmdMsg|��ȭ��|/�ӼӸ� �����ȭ�� ��ȭ����
                            String[] msgSubArr = msgArr[1].split(" ",3);                        
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of whipser is wrong.\r\n usage : /�ӼӸ� [receiver] [message].");
                            }else if(name.equals(msgSubArr[1])){
                                out.writeUTF("show|[##] You cannot whisper to yourself.\r\n usage : /�ӼӸ� [receiver] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ //����üũ
                                    System.out.println("Whisper!");
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] User does not exist.");
                                }
                            }                         
                        }else if(msgArr[1].trim().startsWith("/changeroom")){
                            String[] msgSubArr = msg.split(" ");                           
                            if(msgSubArr.length==1){ // ������ ������ �Է¾��ϰ� /�������Է�������� ������� ���                               
                                out.writeUTF("show|"+getEachMapSize(loc));                             
                            }else if(msgSubArr.length==2) {
                                String tmpLoc = msgSubArr[1]; //����
                               
                                if(loc.equals(tmpLoc)){
                                    out.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose current room.\r\n " +
                                                "usage : Showing roomlist : /changeroom" +
                                                "\r\n usage : Changing room : /changeroom [roomname].");
                                    continue;
                                }
                                if(globalMap.containsKey(tmpLoc)&& !this.chatMode){ //����üũ
                                        out.writeUTF("show|[##] Change "+loc+" to "+ tmpLoc+". ");                        
                                        clientMap.remove(name); //���� ���� �ؽ��ʿ��� �ش� �����带 ����.
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
                                if(clientMap.containsKey(toName) && !clientMap.get(toName).chatMode){ //����üũ
                                    //req_PvPchat|��û��|������|�޽��� .... ���
                                    //req_PvPchat|�޽��� .... �� ����
                                   
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
                                chatMode = false; //1:1��ȭ��� ����
                                out.writeUTF("show|[##] Quit PvP chat with " +toNameTmp);
                                clientMap.get(toNameTmp).chatMode=false; //���浵 1:1��ȭ��� ����
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
                                    //���ϼ��������� �ϴ� Ŭ���̾�Ʈ ������ �ּ� �˱����� ���� ��ü ����.
                                    System.out.println("s.getInetAddress():IP of fileserver=>"+s.getInetAddress());
                                    //���ϼ��������� �ϴ� Ŭ���̾�Ʈ ������ ���
                                    fileServerIP = s.getInetAddress().getHostAddress();
                                    clientMap.get(toNameTmp).out.writeUTF("req_fileSend|[##] "+name +" is trying to send["+sendFile.getName()+"] \r\n�����Ͻðڽ��ϱ�?(Y/N)");                        
                                    out.writeUTF("show|[##] "+ "Sending file["+sendFile.getAbsolutePath()+"] to " + toNameTmp);
                                }else{
                                    out.writeUTF("show|[##] You can't send this file. \r\nYou only can send file having extension with ["+availExtList+"].");                             
                                } //if                         
                            }else{                             
                                out.writeUTF("show|[##] The file does not exist.");                            
                            } //if
                        }else{
                            out.writeUTF("show|[##] Wrong Command.");
                        }//if
                    }else if(msg.startsWith("req_say")){ //��ȭ���� ����
                          if(!chatMode){
                            //req_say|���̵�|��ȭ����
                            sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                            //��½�Ʈ������ ������.
                          }else{
                            sendPvPMsg(loc, name,toNameTmp , "say|"+name+"|"+msgArr[1]);
                          }
                    }else if(msg.startsWith("req_whisper")){ //�ӼӸ� ����
                        if(msgArr[1].trim().startsWith("/whisper")){
                            //req_cmdMsg|��ȭ��|/�ӼӸ� �����ȭ�� ��ȭ����
                            String[] msgSubArr = msgArr[1].split(" ",3); //�޾ƿ� msg�� " "(����)�� �������� 3���� �и�
                                                           
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /whisper [opponen'ts name] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                //String toMsg = "��:from("+name+")=>"+((msgArr[2]!=null)?msgArr[2]:"");
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ //����üũ
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] The user does not exist.");
                                }
                            }
                        }
                    }else if(msg.startsWith("PvPchat")){ //1:1��ȭ��û ��������� ���� ó��
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
                    } else if(msg.startsWith("fileSend")){ //��������
                        //fileSend|result    
                        String result = msgArr[0];
                        if(result.equals("yes")){
                            System.out.println("##filetransfer##YES");                             
                            try {                      
                                String tmpfileServerIP = clientMap.get(toNameTmp).fileServerIP;
                                String tmpfilePath = clientMap.get(toNameTmp).filePath;
                                //fileSender|filepath;    
                                clientMap.get(toNameTmp).out.writeUTF("fileSender|"+tmpfilePath);
                                //������ ������ Ŭ���̾�Ʈ���� ���������� ���� filePath�� ����� ������ �о�ͼ� OutputStream���� ���
                                //fileReceiver|ip|fileName;
                                //String fileName = tmpfilePath.substring(tmpfilePath.lastIndexOf("\\")+1); //���� �� ����
                                String fileName = new File(tmpfilePath).getName();
                                out.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);                                        
                               
                                /*����*/
                                clientMap.get(toNameTmp).filePath="";
                                clientMap.get(toNameTmp).fileServerIP="";
                               
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else /*(result.equals("no"))*/{
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" rejected filesend.");
                        }//if                      
                    }else if(msg.startsWith("req_exit")){ //����  
                       
                    }
                    //------------------------------------------------- �޼��� ó��
                   
             
                }//while()---------
            }catch(Exception e){
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
                //e.printStackTrace();
            }finally{
                //���ܰ� �߻��Ҷ� ����. �ؽ��ʿ��� �ش� ������ ����.
                //���� �����ϰų� ������ java.net.SocketException: ���ܹ߻�
                if(clientMap!=null){
                    clientMap.remove(name);
                    sendGroupMsg(loc,"## "+ name + "exit the server.");
                    System.out.println("##Now "+(--MultiServer.connUserCount)+"people are(is) online.");
                }              
            }
        }//run()------------
    }//class MultiServerRec-------------
    //////////////////////////////////////////////////////////////////////
}