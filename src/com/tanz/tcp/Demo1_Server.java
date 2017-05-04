package com.tanz.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Demo1_Server {

	/**
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(54321);
		System.out.println("服务器启动,绑定54321端口");
		
		while(true) {
			final Socket socket = server.accept();					//接受客户端的请求
			
			new Thread() {											//开启一条线程
				public void run() {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//获取输入流
						PrintStream ps = new PrintStream(socket.getOutputStream());//获取输出流 PrintStream可换行
						
						String line = br.readLine();				//将客户端写过来的数据读取出来
						line = new StringBuilder(line).toString();	
						ps.println(line);							
						
						socket.close();//socket关闭时，ps,br也会跟着关闭
						//server.close();服务器一般不关
					} catch (IOException e) {						
						
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

}
