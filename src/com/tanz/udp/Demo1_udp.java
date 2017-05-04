package com.tanz.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Demo1_udp {
	/**
	 * 
	     * 此类描述的是:用多线程将发送和接收用同一个窗口完成
	     * @author: 谭卓
	     * @version:1.0 
	     * @date:2017年5月4日 下午2:18:52
	 */
	public static void main(String[] args) {
		
		new Receive().start();

		new Send().start();
	}

}


class Receive extends Thread {
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(6666); // 创建Socket相当于创建码头，指定端口号6666
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);// 创建Packet相当于创建集装箱

			while (true) {
				socket.receive(packet); // 接货,接收数据

				byte[] arr = packet.getData(); // 获取数据
				int len = packet.getLength(); // 获取有效的字节个数
				String ip = packet.getAddress().getHostAddress(); // 获取ip地址
				int port = packet.getPort(); // 获取端口号
				System.out.println(ip + ":" + port + ":" + new String(arr, 0, len));
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}

class Send extends Thread {
	public void run() {
		try {
			Scanner sc = new Scanner(System.in); // 创建键盘录入对象
			DatagramSocket socket = new DatagramSocket(); // 创建Socket相当于创建码头

			while (true) {
				String line = sc.nextLine(); // 获取键盘录入的字符串
				if ("quit".equals(line)) {
					break;
				}
				DatagramPacket packet = // 创建Packet相当于集装箱
						new DatagramPacket(line.getBytes(), line.getBytes().length, InetAddress.getByName("127.0.0.1"),
								6666);
				socket.send(packet); // 发货,将数据发出去
			}
			socket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
