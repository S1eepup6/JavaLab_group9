import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
 
public class FileTrans {
	
}

class FileSender extends Thread{ //파일서버 역할 outputstream으로 파일을 클라이언트에게 전송.
    String filePath;
    public FileSender() {
       
    }
    public FileSender(String filePath) {
        this.filePath = filePath;
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9990);
            System.out.println("====================> Ready to send file");
            serverSocket.setSoTimeout(5000);
            Socket socket = serverSocket.accept();
            System.out.println("====================> File Send Start");
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            OutputStream out = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            int r=0;
            while((r = bis.read())!=-1){
                bos.write(r);                          
            }
           
            System.out.println("====================> File Send Complete");
           
            bos.close();
            bis.close();
            out.close();
            fis.close();
            socket.close();
            serverSocket.close();
           
        } catch (IOException e) {      
            e.printStackTrace();
            System.out.println("====================> File Send Failed");
        }
    }
}
 
 
class FileReceiver extends Thread{
//파일클라이언트 역할
   
    Socket socket;
    InputStream in;
    FileOutputStream fos;
    String filename;
    BufferedInputStream bis;
    BufferedOutputStream bos;
    public FileReceiver() {
       
    }
   
    public FileReceiver(String ip,String filename) {
        try {
            this.filename = filename;
            socket = new Socket(ip,9990);
            System.out.println("====================> Start File Download");
            in = socket.getInputStream();
            bis = new BufferedInputStream(in);
           
        } catch (UnknownHostException e) {         
            e.printStackTrace();
        } catch (IOException e) {              
            e.printStackTrace();
        }
    }
   
    @Override
    public void run() {
        try {
            String fileSeparator = System.getProperty("file.separator");
            File f = new File("Down");         
            if(!f.isDirectory()){
                f.mkdir();
            }
            fos = new FileOutputStream("Down"+fileSeparator+filename);
            bos = new BufferedOutputStream(fos);
            int r=0;
            while((r= bis.read())!=-1){
                bos.write(r);
            }
            System.out.println("====================> Down"+fileSeparator+filename);
            System.out.println("====================> File Download Complete");
        } catch (FileNotFoundException e) {        
            System.out.println("예외: "+e.getMessage());
            //e.printStackTrace();
        } catch (IOException e) {          
            //e.printStackTrace();
            System.out.println("====================> File Send Failed");
        }finally{
            try {
                fos.close();
                in.close();
                bis.close();
                bos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }          
        }
    }
}