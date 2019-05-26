
package assignment6;

 

import java.net.*;

import java.io.*;

import java.util.Date;

import java.text.SimpleDateFormat;

 

public class MultiServer {

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {

			serverSocket = new ServerSocket(7777);

			System.out.println(getTime() + "Server is ready.");

		}

		catch(IOException e) {

			e.printStackTrace();

		}

        while(true) {

        	try {

				System.out.println(getTime() + "Waiting for connection.");

                Socket connectedClientSocket = serverSocket.accept();

                InetAddress ia = connectedClientSocket.getInetAddress();

                String ip = ia.getHostAddress(); // 접속된 원격 Client IP 

				System.out.println(getTime() + ip + "Access request");

                ThreadServerHandler handler = new ThreadServerHandler(connectedClientSocket);

                handler.start(); 

            } 

        	catch(IOException e) {

				e.printStackTrace();

			}

        }

    }

    static String getTime() {

		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");

		return f.format(new Date());

	}

}

 

class ThreadServerHandler extends Thread {

    private Socket connectedClientSocket;

    String ClientMessage;

    public ThreadServerHandler(Socket connectedClientSocket) {

        this.connectedClientSocket = connectedClientSocket;  

    }

    public void run() {

        try {

            //클라이언트로 내용을 출력 할 객체 생성

            InputStream in = connectedClientSocket.getInputStream();

			OutputStream out = connectedClientSocket.getOutputStream();

			DataInputStream dis = new DataInputStream(in);

			DataOutputStream dos = new DataOutputStream(out);

			ClientMessage = new String(dis.readUTF());

			dos.writeUTF("[Notice] " + ClientMessage + "from Server.");

			System.out.println(getTime() + "Data is transmitted.");

			dos.close();

			dis.close();

        } catch(IOException ignored) {

        } finally {

            try {

                connectedClientSocket.close();

            } catch(IOException ignored) {}

        }

    }

    static String getTime() {

		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");

		return f.format(new Date());

	}

}

