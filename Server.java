import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Server{
	HashMap<String, DataOutputStream> clients;
	HashMap<String, String> jobs;
	HashMap<String, Integer> votes;
	ServerSocket serverSocket = null;
	Socket socket = null;
	boolean day = true;
	int win = 0;
	
	public Server(){
		clients = new HashMap<String, DataOutputStream>();
		Collections.synchronizedMap(clients);
		jobs = new HashMap<String, String>();
		Collections.synchronizedMap(jobs);
		votes = new HashMap<String, Integer>();
		Collections.synchronizedMap(votes);
	}//Server ������
	
	public void init(){
		try{
			serverSocket = new ServerSocket(8000);
			System.out.println("���Ǿ� ���� ����");
			
			while(true){
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "���� ����");
               
                Thread manager = new Manager(socket);
                manager.start();
            }
		}catch(Exception e){
            e.printStackTrace();
        }
	}//Server �ʱ⼳��
	
	public void sendToAll(String msg){
		Iterator<String> iter = clients.keySet().iterator();
	       
        while(iter.hasNext()){
            try{
                DataOutputStream out = (DataOutputStream) clients.get(iter.next());
                out.writeUTF(msg);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
	}//��ü ����
	
	public void sendToJob(String job, String msg){
		Iterator<String> iter = jobs.keySet().iterator();
	       
        while(iter.hasNext()){
        	if(jobs.get(iter.next()) == job){
        		try{
                    DataOutputStream out = (DataOutputStream) clients.get(iter.next());
                    out.writeUTF(msg);
                }catch(Exception e){
                    e.printStackTrace();
                }
        	}
        }
	}//Ư�� ������ ����
	
	public String voteResult(){
		Iterator<String> iter = votes.keySet().iterator();
		int vote = 0;
		String name = "";
		while(iter.hasNext()){
			if(votes.get(iter.next()) == 0){
				continue;
			}
			if(votes.get(iter.next()) > vote){
				name = iter.next();
				vote = votes.get(iter.next());
			}
		}
		return name;
	}
	
	public void voteReset(){
		Iterator<String> iter = votes.keySet().iterator();
		
		while(iter.hasNext()){
			votes.put(iter.next(), 0);
		}
	}
	
	public void winResult(){
		Iterator<String> iter = jobs.keySet().iterator();
		int earth_num = 0;
		int skrull_num = 0;
		
		while(iter.hasNext()){
			if(iter.next() == "Citizen"){
				earth_num++;
			}
			else if(iter.next() == "Scientist"){
				earth_num++;
			}
			else if(iter.next() == "Skrull"){
				skrull_num++;
			}
			else if(iter.next() == "Betrayer"){
				skrull_num++;
			}
		}
		if(skrull_num == 0){
			win = 1;
		}
		else if(skrull_num >= earth_num){
			win = 2;
		}
		else{
			win = 0;
		}
	}
	
	public static void main(String[] args){
		Server server = new Server();
		server.init();
	}//Main
	
	
	public class Manager extends Thread{
		Socket cSocket;
        DataInputStream in;
        DataOutputStream out; 
		
        public Manager(Socket cSocket){
        	this.cSocket = cSocket;
        	try{
        		in = new DataInputStream(cSocket.getInputStream());
        		out = new DataOutputStream(socket.getOutputStream());
        	} catch(Exception e){
        		e.printStackTrace();
        	}
        }//Manager ������
        
        @Override
        public void run(){
        	String name = "";
        	try{
        		while( (name = in.readUTF())!= null){
        			if(clients.containsKey(name)){
                         out.writeUTF("�ߺ��� �̸��Դϴ�.");
                    }
        			else{
        				System.out.println("[" + name + "]���� �����ϼ̽��ϴ�.");
                    	out.writeUTF("");
                    	break;
        			}
        		}
        		clients.put(name, out);
        		votes.put(name, 0);
        		sendToAll("[" + name + "]���� �����ϼ̽��ϴ�.");
        		while(in != null){
        			String msg = in.readUTF();
        			
        			if(jobs.get(name) == "Dead"){
        				System.out.println("<�����>" + msg);
        				sendToJob("Dead", "<�����>" + msg);
        				continue;
        			}
        			
        			if(msg.startsWith("!")){
        				if(msg.startsWith("!��ǥ")){
        					msg.replace("!��ǥ", "");
        					int vote = votes.get(msg.trim());
        					votes.put(msg.trim(), vote++);
        				}
        				else if(msg.startsWith("!�м�") && (jobs.get(name) == "Scientist")){
        					msg.replace("!�м�", "");
        					if(jobs.get(msg.trim()) == "Skrull"){
        						sendToAll(msg.trim() + "�� ��ũ���Դϴ�! �߹��ϼ���!");
        					}
        					else{
        						sendToAll("�м��� �����߽��ϴ�.");
        					}
        				}
        				else if(msg.startsWith("!����") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
        					if(msg.startsWith("!����")){
        						msg.replace("!����", "");
        						jobs.put(msg.trim(), "Dead");
        					}
        				}
        				else if(msg.equals("!quit")){
        					System.out.println(name + "���� �����ϼ̽��ϴ�.");
        	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
        					break;
        				}
        			}// ��ɾ� �Է½�
        			else{
        				System.out.println(msg);
        				sendToAll(msg);
        			}
        		}
        	} catch(SocketException se){
        		se.printStackTrace();
        	}catch(Exception e){
        		e.printStackTrace();
        	} finally{
        		clients.remove(name);
        		jobs.remove(name);
        		votes.remove(name);
        	}
        }
	}//Manager ��
	
	public class DaySetting extends Thread{
		public DaySetting(){
			day = true;
		}
		
		@Override
		public void run(){
			System.out.println("������ �����մϴ�.");
			sendToAll("������ �����մϴ�.");
			
			while(true){
				try {
					System.out.println("���� ��ҽ��ϴ�.");
					sendToAll("���� ��ҽ��ϴ�.");
					if(win == 1){
						System.out.println("������ ������ �¸��߽��ϴ�.");
						sendToAll("������ ������ �¸��߽��ϴ�.");
						break;
					}
					else if(win == 2){
						System.out.println("��ũ�� ������ �¸��߽��ϴ�.");
						sendToAll("��ũ�� ������ �¸��߽��ϴ�.");
						break;
					}
					day = true;
					if(voteResult() != ""){
						System.out.println(voteResult() + "���� �߹�Ǿ����ϴ�.");
						sendToAll(voteResult() + "���� �߹�Ǿ����ϴ�.");
						jobs.put(voteResult(), "Dead");
						voteReset();
					}
					
					System.out.println("3�� ���ҽ��ϴ�.");
					sendToAll("3�� ���ҽ��ϴ�.");
					for(int i = 0; i < 180; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("��ǥ �ð��Դϴ�.");
					sendToAll("��ǥ �ð��Դϴ�.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("���� ã�ƿԽ��ϴ�.");
					sendToAll("���� ã�ƿԽ��ϴ�.");
					day = false;
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}//�ð� ����
}//Server

