package com.cardServer.server;

import java.io.*;
import java.net.*;
import java.util.*;




public class Server {

	/*
	 * ��Ա���������ǳ�
	 */
	List<ClientThread> clients = new ArrayList<ClientThread>();
	List<Games> games= new ArrayList<Games>();
	public static void main(String[] args) {
		new Server().start();
	}

	public void start() {
		try {
			System.out.println("�����������Ѿ�������");
			boolean iConnect = false;
			ServerSocket ss = new ServerSocket(10000);
			iConnect = true;
			Games game=new Games();
			games.add(game);
			while (iConnect) {
		
				Socket s = ss.accept();
				ClientThread currentClient = new ClientThread(s);// �����߳�����
				InetAddress addr = s.getInetAddress();
				System.out.println("���ֿͻ��ˣ��ͻ���ipΪ��" + addr.getHostAddress());
				clients.add(currentClient);// �ѵ�ǰ�ͻ��˼��뼯��
				new Thread(currentClient).start();
				System.out.println("�ͻ��˽����Ѿ�������");
			}
			System.out.println("�����Ѿ��رգ�");
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
		 * С��һ��
		 */
		ClientThread(Socket s) {
			this.s = s;
			iConnect = true;
		}

		public void run() {
			System.out.println("run���������ˣ�");
			try {

				while (iConnect) {
					//System.out.println("RUN�����е�whileѭ�����������ڵȴ��ͻ��˵ķ�����Ϣ...");
					InputStream is = s.getInputStream();
					int len = is.available();
					if (len != 0) // ����û�������,������������Ϣ
					{

						ByteArrayOutputStream out1 = new ByteArrayOutputStream();
						byte[] buff = new byte[len];
						System.out.println("len=" + len);

						is.read(buff);
						out1.write(buff, 0, len);
						str = new String(out1.toByteArray(), "UTF-8");// ����Ϊֹ��readΪ�û�������ַ���

						System.out.println("�ͻ��˷�������Ϣ��" + str);
						
						//TODO �����accout+msg��ʽ
						decodeMsg(str);

						
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// ����������������Ϣ���͸�ÿ�����ӵ��Ŀͻ���

		public void sendMsg(String str) {
			try {
				System.out.println("��������ܵ���");
				OutputStream os = this.s.getOutputStream();
				System.out.println("������ͻ���д��Ϣ��");
				 os.write(str.getBytes("UTF-8"));
				System.out.println("������ͻ���д��Ϣ�ɹ���");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		//����
		public boolean decodeMsg(String str){
			//��r1��������Ҽ����[���ָ��
			if(str.startsWith("username")){
				String name=str.substring(9);
				this.username=name;
				Games aGame=games.get(games.size()-1);
				if(! aGame.addPlayer(name)){
					Games game=new Games();
					games.add(game);
					games.get(games.size()-1).addPlayer(name);
					System.out.println("���ڵȴ��������...");
				}
				else{
					//����Ҽ���󼴿��_ʼ�[��
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
						System.out.println("�����_ʼһ���[��"+this.opname+" VS "+this.username);
					}
				
				}
				return false;
			}
			else{
				for (int i = 0; i < clients.size(); i++) {
					System.out.println("ת����Ϣ��..." + i);
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