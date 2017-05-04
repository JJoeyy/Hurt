package com.tanz.udp;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Demo2_GUIChat extends Frame{
	private TextField tf;
	private Button send;
	private Button log;
	private Button clear;
	private TextArea viewText;
	private TextArea sendText;
	private DatagramSocket socket;
	private BufferedWriter bw;
	/**
	 * 
	     * 此类描述的是:UDP 自问自答聊天室
	     * @author: 谭卓
	     * @version:1.0 
	     * @date:2017年5月4日 下午3:22:51
	 */
	public Demo2_GUIChat() {
		init();
		southPanel();
		centerPanel();
		event();
	}
	//关闭窗口
	public void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
					bw.close();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		//3个按钮监听器，发送按钮
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}

		});
		//历史纪录按钮
		log.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logFile();
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}

		});
		//清屏按钮
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewText.setText("");
			}
		});
		
		//键盘事件监听器
		sendText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				//设置 ctrl+enter快捷键
				if(e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
					try {
						send();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}
			}
		});
	}
	

	
	private void logFile() throws IOException {
		bw.flush();									//刷新缓冲区
		FileInputStream fis = new FileInputStream("config.txt");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	//在内存中创建缓冲区
		
		int len;
		byte[] arr = new byte[8192];
		while((len = fis.read(arr)) != -1) {
			baos.write(arr, 0, len);
		}
		
		String str = baos.toString();				//将内存中的内容转换成了字符串
		viewText.setText(str);
		
		fis.close();
	}
	
	private void send(byte[] arr, String ip) throws IOException {
		DatagramPacket packet = 
				new DatagramPacket(arr, arr.length, InetAddress.getByName(ip), 9999);
		socket.send(packet);						//发送数据
	}
	
	private void send() throws IOException {
		String message = sendText.getText();		//获取发送区域的内容
		String ip = tf.getText();					//获取ip地址;
		ip = ip.trim().length() == 0 ? "255.255.255.255" : ip;//当ip文本框为null时，ip地址设为广播地址
		
		send(message.getBytes(),ip);
		
		String time = getCurrentTime();				//获取当前时间
		String str = time + " 我对:" + (ip.equals("255.255.255.255") ? "所有人" : ip) + "说\r\n" + message + "\r\n\r\n";	//alt + shift + l 抽取局部变量
		viewText.append(str);						//将信息添加到显示区域中
		bw.write(str);								
		sendText.setText("");
		
		
	}
	
	private String getCurrentTime() {
		Date d = new Date();						//创建当前日期对象
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return sdf.format(d);						//将时间格式化
	}

	public void centerPanel() {
		Panel center = new Panel();					//创建中间的Panel
		viewText = new TextArea();
		sendText = new TextArea(5,1);
		center.setLayout(new BorderLayout());		//设置为边界布局管理器
		center.add(sendText,BorderLayout.SOUTH);	//发送的文本区域放在南边
		center.add(viewText,BorderLayout.CENTER);	//显示区域放在中间
		viewText.setEditable(false);				//设置不可以编辑
		viewText.setBackground(Color.WHITE);		//设置背景颜色
		sendText.setFont(new Font("xxx", Font.PLAIN, 15));
		viewText.setFont(new Font("xxx", Font.PLAIN, 15));
		this.add(center,BorderLayout.CENTER);
	}

	public void southPanel() {
		Panel south = new Panel();					//创建南边的Panel
		tf = new TextField(15);
		tf.setText("127.0.0.1");
		send = new Button("send");
		log = new Button("history");
		clear = new Button("clear");
		south.add(tf);
		south.add(send);
		south.add(log);
		south.add(clear);
		this.add(south,BorderLayout.SOUTH);			//将Panel放在Frame的南边
	}

	public void init() {
		this.setLocation(500, 50);
		this.setSize(400, 600);
		new Receive().start();
		try {
			socket = new DatagramSocket();
			bw = new BufferedWriter(new FileWriter("config.txt",true));	//创建config.txt用于保存聊天记录,在尾部追加
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		this.setVisible(true);
	}
	private class Receive extends Thread {			//接收和发送需要同时执行,所以定义成多线程的
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket(9999);
				DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
				
				while(true) {
					socket.receive(packet);				//接收信息
					byte[] arr = packet.getData();		//获取字节数据
					int len = packet.getLength();		//获取有效的字节数据

					String message = new String(arr,0,len);	//转换成字符串
					
					String time = getCurrentTime();		//获取当前时间
					String ip = packet.getAddress().getHostAddress();	//获取ip地址
					String str = time + " " + ip + " 对我说:\r\n" + message + "\r\n\r\n";
					viewText.append(str);
					bw.write(str);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Demo2_GUIChat();
	}


}
