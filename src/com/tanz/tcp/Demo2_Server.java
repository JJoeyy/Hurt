package com.tanz.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Demo2_Server {

	/**
	 * * 2.服务端
	* 创建ServerSocket(需要指定端口号)
	* 调用ServerSocket的accept()方法接收一个客户端请求，得到一个Socket
	* 调用Socket的getInputStream()和getOutputStream()方法获取和客户端相连的IO流
	* 输入流可以读取客户端输出流写出的数据
	* 输出流可以写出数据到客户端的输入流
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ServerSocket server = new ServerSocket(12345);
		
		while(true) {
			final Socket socket = server.accept();						//接受客户端的请求
			new Thread() {
				public void run() {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));		//将字节流包装成了字符流
						PrintStream ps = new PrintStream(socket.getOutputStream());					//PrintStream中有写出换行的方法
						
						ps.println("我半藏中单贼6");
						System.out.println(br.readLine());
						ps.println("来solo");
						System.out.println(br.readLine());
						socket.close();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	

}
