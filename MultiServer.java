package assignment6;

 

import java.io.BufferedReader;

import java.io.DataInputStream;

import java.io.DataOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.net.ConnectException;

import java.net.InetAddress;

import java.net.Socket;

import java.util.Scanner;

 

public class TCPClient {

	public static void main(String args[]) {

		try {

			String serverIp = "127.0.0.1";

			String message;

			System.out.println("Requesting to access. Server IP : " + serverIp);

			Socket socket = new Socket(serverIp, 7777);

			InputStream in = socket.getInputStream();

			OutputStream out = socket.getOutputStream();

			DataInputStream dis = new DataInputStream(in);

			DataOutputStream dos = new DataOutputStream(out);

			

			Scanner input = new Scanner(System.in);

			System.out.print("Message to send : ");

			message = input.next();

			

			dos.writeUTF(message);

			

			System.out.println("Message from server : " + dis.readUTF());

			System.out.println("Closing connection...");

			

			dis.close();

			dos.close();

			socket.close();

			System.out.println("Connection is closed.");

		} catch (ConnectException ce) {

			ce.printStackTrace();

		} catch (IOException ie) {

			ie.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}

