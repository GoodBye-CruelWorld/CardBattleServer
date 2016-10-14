package com.cardServer.server;

import java.io.*;
import java.net.*;
import java.util.*;




public class Server {

	/*
	 * 成员变量闪亮登场
	 */
	List<ClientThread> clients = new ArrayList<ClientThread>();
	List<Games> games= new ArrayList<Games>();
	public static void main(String[] args) {
		new Server().start();
	}

	public void start() {
		try {
			System.out.println("服务器进程已经启动！");
			boolean iConnect = false;
			ServerSocket ss = new ServerSocket(10000);
			iConnect = true;
			Games game=new Games();
			games.add(game);
			while (iConnect) {
		
				Socket s = ss.accept();
				ClientThread currentClient = new ClientThread(s);// 创建线程引用
				InetAddress addr = s.getInetAddress();
				System.out.println("发现客户端！客户端ip为：" + addr.getHostAddress());
				clients.add(currentClient);// 把当前客户端加入集合
				new Thread(currentClient).start();
				System.out.println("客户端进程已经启动！");
			}
			System.out.println("服务已经关闭！");
			ss.close();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}

	class ClientThread implements Runnable {

		private Socket s;
		//private DataInputStream dis;
		private DataOutputStream dos;
		private String str;
		private boolean iConnect = false;
		private String username;
		private String opname;
		/**
		 * 小构一下
		 */
		ClientThread(Socket s) {
			this.s = s;
			iConnect = true;
		}

		public void run() {
			System.out.println("run方法启动了！");
			try {

				while (iConnect) {
					//System.out.println("RUN方法中的while循环启动，正在等待客户端的发送消息...");
					InputStream is = s.getInputStream();
					int len = is.available();
					if (len != 0) // 如果用户有输入,服务器接收信息
					{

						ByteArrayOutputStream out1 = new ByteArrayOutputStream();
						byte[] buff = new byte[len];
						System.out.println("len=" + len);

						is.read(buff);
						out1.write(buff, 0, len);
						str = new String(out1.toByteArray(), "UTF-8");// 到此为止，read为用户输入的字符串

						System.out.println("客户端发来的信息是" + str);
						
						//TODO 解码成accout+msg格式
						decodeMsg(str);

						
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// 将送至服务器的消息发送给每个连接到的客户端

		public void sendMsg(String str) {
			try {
				System.out.println("创建输出管道！");
				OutputStream os = this.s.getOutputStream();
				System.out.println("正在向客户端写消息！");
				 os.write(str.getBytes("UTF-8"));
				System.out.println("正在向客户端写消息成功！");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		//解码
		public boolean decodeMsg(String str){
			//情況1：當為玩家加入遊戲的指令
			if(str.startsWith("username")){
				String name=str.substring(9);
				this.username=name;
				Games aGame=games.get(games.size()-1);
				if(! aGame.addPlayer(name)){
					Games game=new Games();
					games.add(game);
					games.get(games.size()-1).addPlayer(name);
					System.out.println("正在等待其他玩家...");
				}
				else{
					//即玩家加入后即可開始遊戲
					if(aGame.getPlayersNum()==2){
						
						this.opname= aGame.getPlayersID(0);
						this.sendMsg("rank1");
						for (int i = 0; i < clients.size(); i++) {
							ClientThread c = clients.get(i);
							if(c.username.equals(opname)){
								c.opname=this.username;
								c.sendMsg("rank2");
							}
						}
						System.out.println("正在開始一場遊戲："+this.opname+" VS "+this.username);
					}
				
				}
				return false;
			}
			else{
				for (int i = 0; i < clients.size(); i++) {
					System.out.println("转发消息中..." + i);
					ClientThread c = clients.get(i);
					if(c.username.equals(opname)){
						c.sendMsg(str);
					}
				}
			}
			
			

			return true;
		}
		
	}
}