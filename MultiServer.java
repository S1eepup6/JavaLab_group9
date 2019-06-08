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
 
/*콘솔 멀티채팅 서버 프로그램*/
public class MultiServer {
    HashMap<String,HashMap<String,ServerRecThread>> globalMap; //지역별 해쉬맵을 관리하는 해시맵
    ServerSocket serverSocket = null;
    Socket socket = null;
    static int connUserCount = 0;
    public MultiServer(){
       globalMap = new HashMap<String,HashMap<String, ServerRecThread>>();
        //clientMap = new HashMap<String,DataOutputStream>(); //클라이언트의 출력스트림을 저장할 해쉬맵 생성.
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
            serverSocket = new ServerSocket(9999); //9999포트로 서버소켓 객체생성.
            System.out.println("##Server is started.");
            while(true){ //서버가 실행되는 동안 클라이언트들의 접속을 기다림.
                socket = serverSocket.accept(); //클라이언트의 접속을 기다리다가 접속이 되면 Socket객체를 생성.
                System.out.println(socket.getInetAddress()+":"+socket.getPort()); //클라이언트 정보 (ip, 포트) 출력
                Thread msr = new ServerRecThread(socket); //쓰레드 생성.
                msr.start(); //쓰레드 시동.
            }      
           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /** 접속된 모든 클라이언트들에게 메시지를 전달. */
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
    /**해당 클라이언트가 속해있는 그룹에대해서만 메시지 전달.*/
    public void sendGroupMsg(String loc, String msg){      
        HashMap<String, ServerRecThread> gMap = globalMap.get(loc);    
        Iterator<String> group_it = globalMap.get(loc).keySet().iterator();        
        while(group_it.hasNext()){
            try{    
                    ServerRecThread st = gMap.get(group_it.next());
                    if(!st.chatMode){ //1:1대화모드가 아닌 사람에게만.
                        st.out.writeUTF(msg);  
                    }
            }catch(Exception e){
                System.out.println("Exception:"+e);
            }
        }  
    }//sendGroupMsg()-----------
    /**1:1 대화*/
    public void sendPvPMsg(String loc,String fromName, String toName, String msg){
     
            try {
                globalMap.get(loc).get(toName).out.writeUTF(msg);
                globalMap.get(loc).get(fromName).out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
         
    }//sendPvPMsg()-----------
   
    /** 귓속말 */
    public void sendToMsg(String loc, String fromName, String toName, String msg){     
        try{
                globalMap.get(loc).get(toName).out.writeUTF("whisper|"+fromName+"|"+msg);
                globalMap.get(loc).get(fromName).out.writeUTF("whisper|"+fromName+"|"+msg);
               
           }catch(Exception e){
                System.out.println("Exception:"+e);
           }
         
     }
    /**각그룹의 접속자수와 서버에 접속된 유저를 반환
     * 하는 메소드**/
    public String getEachMapSize(){
        return getEachMapSize(null);    
    }//getEachMapSize()-----------
   
    /**각그룹의 접속자수와 서버에 접속된 유저를 반환 하는 메소드
     * 추가 지역을 전달받으면 해당 지역을 체크
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
                //if(key.equals(loc)) key+="(*)"; //현재 유저가 접속된 곳 표시
                int size = it_hash.size();
                sum +=size;
                sb.append(key+": ("+size+"people)"+(key.equals(loc)?"(*)":"")+"\r\n");
               
            }catch(Exception e){
                System.out.println("예외:"+e);
            }
        }
        //sb.append("⊙현재 대화에 참여하고있는 유저수 :"+ MultiServer.connUserCount);
        sb.append("⊙Joining People :"+ sum+ " people \r\n");
        //System.out.println(sb.toString());
        return sb.toString();
    }//getEachMapSize()-----------
   
   
    /**접속된 유저 중복체크*/        
public boolean isNameGlobal(String name){
        boolean result=false;
        Iterator<String> global_it = globalMap.keySet().iterator();
        while(global_it.hasNext()){
            try{
                String key = global_it.next();             
                HashMap<String, ServerRecThread> it_hash = globalMap.get(key);
                if(it_hash.containsKey(name)){
                    result= true; //중복된 아이디가 존재.
                    break;
                }
               
            }catch(Exception e){
                System.out.println("isNameGlobal()예외:"+e);
            }
        }
        return result;
    }
 
   
    /**문자열 null 값 및 "" 은 대체 문자열로 삽입가능.*/
    public String nVL(String str, String replace){
        String output="";
        if(str==null || str.trim().equals("")){
            output = replace;      
        }else{
            output = str;
        }
        return output;     
    }
    
    //main메서드
    public static void main(String[] args) {
        MultiServer ms = new MultiServer(); //서버객체 생성.
        ms.init();//실행.
    }//main()------  
   
   
   
    ////////////////////////////////////////////////////////////////////////
    //----// 내부 클래스 //--------//
   
    // 클라이언트로부터 읽어온 메시지를 다른 클라이언트(socket)에 보내는 역할을 하는 메서드
    class ServerRecThread extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name=""; //이름 저장
        String loc="";  //지역 저장
        String toNameTmp = null;//1:1대화 상대  
        String fileServerIP; //파일서버 아이피 저장
        String filePath; //파일 서버에서 전송할 파일 패스 저장.
        boolean chatMode; //1:1대화모드 여부
       
       public ServerRecThread(Socket socket){
            this.socket = socket;
            try{
                //Socket으로부터 입력스트림을 얻는다.
                in = new DataInputStream(socket.getInputStream());
                //Socket으로부터 출력스트림을 얻는다.
                out = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                System.out.println("ServerRecThread 생성자 예외:"+e);
            }
        }//생성자 ------------
       
       
       
        /**접속된 유저리스트  문자열로 반환*/        
        public String showUserList(){
           
            StringBuilder output = new StringBuilder("==List of Users==\r\n");
            Iterator it = globalMap.get(loc).keySet().iterator(); //해쉬맵에 등록된 사용자이름을 가져옴.
            while(it.hasNext()){ //반복하면서 사용자이름을 StringBuilder에 추가
                 try{
                    String key= (String) it.next();                                    
                    //out.writeUTF(output);
                    if(key.equals(name)){ //현재사용자 체크
                        key += " (*) ";
                    }
                    output.append(key+"\r\n");                  
                 }catch(Exception e){
                     System.out.println("예외:"+e);
                 }
             }//while---------
            output.append("=="+ globalMap.get(loc).size()+"people online==\r\n");
            System.out.println(output.toString());
            return output.toString();
         }//showUserList()-----------
       /**메시지 파서 */    
       public String[] getMsgParse(String msg){
            System.out.println("msgParse():msg?   "+ msg);         
            String[] tmpArr = msg.split("[|]");        
            return tmpArr;
        }
        @Override
        public void run(){ //쓰레드를 사용하기 위해서 run()메서드 재정의
            HashMap<String, ServerRecThread> clientMap=null;   //현재 클라이언트가 저장되어있는 해쉬맵        
            try{  
                while(in!=null){ //입력스트림이 null이 아니면 반복.
                    String msg = in.readUTF(); //입력스트림을 통해 읽어온 문자열을 msg에 할당.                   
                    String[] msgArr = getMsgParse(msg.substring(msg.indexOf("|")+1));
                    //메세지 처리 ----------------------------------------------
                    if(msg.startsWith("req_logon")){ //로그온 시도 (대화명)                    
                        //req_logon|대화명
                        if(!(msgArr[0].trim().equals(""))&&!isNameGlobal(msgArr[0])){                      
                            name = msgArr[0]; //넘어온 대화명은 전역변수 name에 저장
                            MultiServer.connUserCount++; //접속자수 증가. (스택틱변수를 사용하기에 어울려서 한번 사용해봄.)
                            out.writeUTF("logon#yes|"+getEachMapSize()); //접속된 클라이언트에게 그룹목록 제공
                        }else{
                             out.writeUTF("logon#no|err01");
                        }
                    }else if(msg.startsWith("req_enterRoom")){ //그룹입장을 시도
                       
                        //req_enterRoom|대화명|지역
                         loc = msgArr[1]; //메시지에서 지역부분만 추출하여 전역변수에 저장
                         
                         if(isNameGlobal(msgArr[0])){
                             out.writeUTF("logon#no|"+name);  
                             
                         }else if(globalMap.containsKey(loc)){
                             sendGroupMsg(loc, "show|[##] "+name + "Entered the room.");
                             clientMap= globalMap.get(loc); //현재그룹의 해쉬맵을 따로 저장.
                             clientMap.put(name, this); //현재 MultiServerRec인스턴스를 클라이언트맵에 저장.
                             System.out.println(getEachMapSize()); //서버에 그룹리스트 출력.                       
                             out.writeUTF("enterRoom#yes|"+loc); //접속된 클라이언트에게 그룹목록 제공
                             
                         }else{                        
                             out.writeUTF("enterRoom#no|"+loc);                              
                         }
                         
                    }else if(msg.startsWith("req_cmdMsg")){ //명령어 전송
                        //req_cmdMsg|대화명|/접속자
                        if(msgArr[1].trim().equals("/users")){
                            out.writeUTF("show|"+showUserList()); //접속자 출력                             
                        }else if(msgArr[1].trim().startsWith("/whisper")){
                            //req_cmdMsg|대화명|/귓속말 상대방대화명 대화내용
                            String[] msgSubArr = msgArr[1].split(" ",3);                        
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of whipser is wrong.\r\n usage : /귓속말 [receiver] [message].");
                            }else if(name.equals(msgSubArr[1])){
                                out.writeUTF("show|[##] You cannot whisper to yourself.\r\n usage : /귓속말 [receiver] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ //유저체크
                                    System.out.println("Whisper!");
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] User does not exist.");
                                }
                            }                         
                        }else if(msgArr[1].trim().startsWith("/changeroom")){
                            String[] msgSubArr = msg.split(" ");                           
                            if(msgSubArr.length==1){ // 변경할 지역을 입력안하고 /지역만입력했을경우 지역목록 출력                               
                                out.writeUTF("show|"+getEachMapSize(loc));                             
                            }else if(msgSubArr.length==2) {
                                String tmpLoc = msgSubArr[1]; //지역
                               
                                if(loc.equals(tmpLoc)){
                                    out.writeUTF("show|[##] The usage of command is wrong.\r\n You cannot choose current room.\r\n " +
                                                "usage : Showing roomlist : /changeroom" +
                                                "\r\n usage : Changing room : /changeroom [roomname].");
                                    continue;
                                }
                                if(globalMap.containsKey(tmpLoc)&& !this.chatMode){ //지역체크
                                        out.writeUTF("show|[##] Change "+loc+" to "+ tmpLoc+". ");                        
                                        clientMap.remove(name); //현재 지역 해쉬맵에서 해당 쓰레드를 제거.
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
                                if(clientMap.containsKey(toName) && !clientMap.get(toName).chatMode){ //유저체크
                                    //req_PvPchat|신청자|응답자|메시지 .... 취소
                                    //req_PvPchat|메시지 .... 로 변경
                                   
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
                                chatMode = false; //1:1대화모드 해제
                                out.writeUTF("show|[##] Quit PvP chat with " +toNameTmp);
                                clientMap.get(toNameTmp).chatMode=false; //상대방도 1:1대화모드 해제
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
                                    //파일서버역할을 하는 클라이언트 아이피 주소 알기위해 소켓 객체 얻어옴.
                                    System.out.println("s.getInetAddress():IP of fileserver=>"+s.getInetAddress());
                                    //파일서버역할을 하는 클라이언트 아이피 출력
                                    fileServerIP = s.getInetAddress().getHostAddress();
                                    clientMap.get(toNameTmp).out.writeUTF("req_fileSend|[##] "+name +" is trying to send["+sendFile.getName()+"] \r\n수락하시겠습니까?(Y/N)");                        
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
                    }else if(msg.startsWith("req_say")){ //대화내용 전송
                          if(!chatMode){
                            //req_say|아이디|대화내용
                            sendGroupMsg(loc, "say|"+name+"|"+msgArr[1]);
                            //출력스트림으로 보낸다.
                          }else{
                            sendPvPMsg(loc, name,toNameTmp , "say|"+name+"|"+msgArr[1]);
                          }
                    }else if(msg.startsWith("req_whisper")){ //귓속말 전송
                        if(msgArr[1].trim().startsWith("/whisper")){
                            //req_cmdMsg|대화명|/귓속말 상대방대화명 대화내용
                            String[] msgSubArr = msgArr[1].split(" ",3); //받아온 msg을 " "(공백)을 기준으로 3개를 분리
                                                           
                            if(msgSubArr==null||msgSubArr.length<3){
                                out.writeUTF("show|[##] The usage of command is wrong.\r\n usage : /whisper [opponen'ts name] [message].");
                            }else{
                                String toName = msgSubArr[1];
                                //String toMsg = "귓:from("+name+")=>"+((msgArr[2]!=null)?msgArr[2]:"");
                                String toMsg = msgSubArr[2];
                                if(clientMap.containsKey(toName)){ //유저체크
                                    sendToMsg(loc,name,toName,toMsg);
                                }else{
                                    out.writeUTF("show|[##] The user does not exist.");
                                }
                            }
                        }
                    }else if(msg.startsWith("PvPchat")){ //1:1대화신청 수락결과에 대한 처리
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
                    } else if(msg.startsWith("fileSend")){ //파일전송
                        //fileSend|result    
                        String result = msgArr[0];
                        if(result.equals("yes")){
                            System.out.println("##filetransfer##YES");                             
                            try {                      
                                String tmpfileServerIP = clientMap.get(toNameTmp).fileServerIP;
                                String tmpfilePath = clientMap.get(toNameTmp).filePath;
                                //fileSender|filepath;    
                                clientMap.get(toNameTmp).out.writeUTF("fileSender|"+tmpfilePath);
                                //파일을 전송할 클라이언트에서 서버소켓을 열고 filePath로 저장된 파일을 읽어와서 OutputStream으로 출력
                                //fileReceiver|ip|fileName;
                                //String fileName = tmpfilePath.substring(tmpfilePath.lastIndexOf("\\")+1); //파일 명만 추출
                                String fileName = new File(tmpfilePath).getName();
                                out.writeUTF("fileReceiver|"+tmpfileServerIP+"|"+fileName);                                        
                               
                                /*리셋*/
                                clientMap.get(toNameTmp).filePath="";
                                clientMap.get(toNameTmp).fileServerIP="";
                               
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else /*(result.equals("no"))*/{
                            clientMap.get(toNameTmp).out.writeUTF("show|[##] "+name+" rejected filesend.");
                        }//if                      
                    }else if(msg.startsWith("req_exit")){ //종료  
                       
                    }
                    //------------------------------------------------- 메세지 처리
                   
             
                }//while()---------
            }catch(Exception e){
                System.out.println("MultiServerRec:run():"+e.getMessage() + "----> ");
                //e.printStackTrace();
            }finally{
                //예외가 발생할때 퇴장. 해쉬맵에서 해당 데이터 제거.
                //보통 종료하거나 나가면 java.net.SocketException: 예외발생
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