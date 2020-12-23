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
	boolean kill = true;
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
	    String name;
	    
        while(iter.hasNext()){
        	name = iter.next();
        	if(jobs.get(name) == job){
        		try{
                    DataOutputStream out = (DataOutputStream) clients.get(name);
                    out.writeUTF(msg);
                }catch(Exception e){
                    e.printStackTrace();
                }
        	}
        }
	}//Ư�� ������ ����
	
	public void jobSetting(){
		Iterator<String> iter = clients.keySet().iterator();
		int sci = 1;
		int skr = 2;
		int bet = 1;
		String temp[] = new String[10];
		
		for(int i = 0; i < 10; i++){
			if(i < sci)
				temp[i] = "1";
			else if(i < sci + skr)
				temp[i] = "2";
			else if(i < sci + skr + bet)
				temp[i] = "3";
			else
				temp[i] = "0";
		}
		List<String> job_list = Arrays.asList(temp);
		Collections.shuffle(job_list);
		String sci_name = "";
		
		for(int i = 0; i < job_list.size(); i ++){
			String name = iter.next();
			switch(job_list.get(i)){
			case "0":
				jobs.put(name, "Citizen");
				break;
			case "1":
				jobs.put(name, "Scientist");
				sci_name = name;
				break;
			case "2":
				jobs.put(name, "Skrull");
				break;
			case "3":
				jobs.put(name, "Betrayer");
				break;
			}
		}
		sendToJob("Citizen", "����� �ù��Դϴ�.");
		sendToJob("Scientist", "����� �������Դϴ�.");
		sendToJob("Skrull", "����� ��ũ���Դϴ�.");
		sendToJob("Betrayer", "����� ������Դϴ�.");
		
		sendToJob("Skrull", "================");
		sendToJob("Betrayer", "================");
		sendToJob("Skrull", "�����ڴ� " + sci_name + "�Դϴ�.");
		sendToJob("Betrayer", "�����ڴ� " + sci_name + "�Դϴ�.");
		sendToJob("Skrull", "================");
		sendToJob("Betrayer", "================");
	}//���� ���
	
	public String voteResult(){
		Iterator<String> iter = votes.keySet().iterator();
		int vote = 0;
		String name = "";
		String v_name = "";
		while(iter.hasNext()){
			name = iter.next();
			if(votes.get(name) == 0){
				continue;
			}
			else if(votes.get(name) > vote){
				vote = votes.get(name);
				v_name = name;
			}
		}
		if(vote == 0){
			return "";
		}
		else{
			return v_name;
		}
	}//��ǥ ���
	
	public void voteReset(){
		Iterator<String> iter = votes.keySet().iterator();
		
		while(iter.hasNext()){
			votes.put(iter.next(), 0);
		}
	}//��ǥ ����
	
	public void winResult(){
		Iterator<String> iter = jobs.keySet().iterator();
		int earth_num = 0;
		int skrull_num = 0;
		
		while(iter.hasNext()){
			String job = jobs.get(iter.next());
			if(job == "Citizen"){
				earth_num++;
			}
			else if(job == "Scientist"){
				earth_num++;
			}
			else if(job == "Skrull"){
				skrull_num++;
			}
			else if(job == "Betrayer"){
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
	}//�¸� ���
	
	public void deleteName(String name){
		clients.remove(name);
		jobs.remove(name);
		votes.remove(name);
	}//���� �ο� ����
	
	public boolean isTwoSkrull(){
		Iterator<String> iter = jobs.keySet().iterator();
		int skr_num = 0;
		
		while(iter.hasNext()){
			if(jobs.get(iter.next()) == "Skrull"){
				skr_num++;
			}
		}
		if(skr_num == 2){
			return true;
		}
		else{
			return false;
		}
	}//��ũ�� 2�� ���� üũ
	
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
        		sendToAll("---------------------");
        		sendToAll("[" + name + "]���� �����ϼ̽��ϴ�.");
        		sendToAll("---------------------");
        		
        		if(clients.size() == 10){
        			Thread daySetting = new DaySetting();
        			daySetting.start();
        		}
        		while(in != null){
        			String msg = in.readUTF();
        			
        			if(day){
        				if(jobs.get(name) == "Dead"){
        					if(msg.equals("!quit")){
        						System.out.println(name + "���� �����ϼ̽��ϴ�.");
            	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
            	        		deleteName(name);
            					break;
        					}
            				System.out.println("<�����>" + msg);
            				sendToJob("Dead", "<�����>" + msg);
            				continue;
            			}
        				if(msg.startsWith("!")){
            				if(msg.startsWith("!��ǥ")){
            					msg = msg.replace("!��ǥ ", "");
            					if(jobs.get(msg) == "Dead"){
            						sendToJob("�̹� ���� ����Դϴ�." ,"Skrull");
        							sendToJob("�̹� ���� ����Դϴ�." ,"Betrayer");
            						continue;
            					}
            					int vote = votes.get(msg);
            					votes.put(msg, vote + 1);
            				}
            				else if(msg.startsWith("!�м�") && (jobs.get(name) == "Scientist")){
            					msg = msg.replace("!�м� ", "");
            					if(jobs.get(msg) == "Scientist"){
            						continue;
            					}
            					if(jobs.get(msg) == "Skrull"){
            						sendToAll(msg + "�� ��ũ���Դϴ�! �߹��ϼ���!");
            					}
            					else{
            						sendToAll("�м��� �����߽��ϴ�.");
            					}
            				}
            				else if(msg.equals("!quit")){
            					System.out.println(name + "���� �����ϼ̽��ϴ�.");
            	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
            	        		deleteName(name);
            					break;
            				}
        				}
        				else{
            				System.out.println(msg);
            				sendToAll(msg);
        				}
        			}//��
        			
        			if(!day){
        				if(jobs.get(name) == "Dead"){
        					if(msg.equals("!quit")){
        						System.out.println(name + "���� �����ϼ̽��ϴ�.");
            	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
            	        		deleteName(name);
            					break;
        					}
            				System.out.println("<�����>" + msg);
            				sendToJob("Dead", "<�����>" + msg);
            				continue;
            			}
        				if(msg.startsWith("!")){
            				if(msg.startsWith("!����") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
            					if(msg.startsWith("!����") && kill){
            						msg = msg.replace("!���� ", "");
            						if(jobs.get(msg) == "Skrull" || jobs.get(msg) == "Betrayer"){
            							sendToJob("���� ��ũ�� �����Դϴ�." ,"Skrull");
            							sendToJob("���� ��ũ�� �����Դϴ�." ,"Betrayer");
            							continue;
            						}
            						else if(jobs.get(msg) == "Dead"){
            							sendToJob("�̹� ���� ����Դϴ�." ,"Skrull");
            							sendToJob("�̹� ���� ����Դϴ�." ,"Betrayer");
            							continue;
            						}
            						else if(isTwoSkrull() && jobs.get(msg) == "Scientist"){
            							sendToJob("���� �����ڸ� ���ϼ� �����ϴ�." ,"Skrull");
            							sendToJob("���� �����ڸ� ���ϼ� �����ϴ�." ,"Betrayer");
            							continue;
            						}
            						sendToAll("������ ���õǾ����ϴ�.");
            						sendToAll("���õ� ������ " + msg + "�Դϴ�.");
            						jobs.put(msg, "Dead");
            						kill = false;
            					}
            				}
            				else if(msg.equals("!quit")){
            					System.out.println(name + "���� �����ϼ̽��ϴ�.");
            					deleteName(name);
            	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
            					break;
            				}
            			}// ��ɾ� �Է½�
            			else if(jobs.get(name) == "Skrull" || jobs.get(name) == "Betrayer"){
            				System.out.println(msg);
            				sendToJob("Skrull", msg);
            				sendToJob("Betrayer", msg);
            			}
        			}//��
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
			System.out.println("================");
			System.out.println("������ �����մϴ�.");
			System.out.println("================");
			sendToAll("================");
			sendToAll("������ �����մϴ�.");
			sendToAll("================");
			
			jobSetting();
			voteReset();
			
			while(true){
				try {
					System.out.println("================");
					System.out.println("���� ��ҽ��ϴ�.");
					System.out.println("================");
					sendToAll("================");
					sendToAll("���� ��ҽ��ϴ�.");
					sendToAll("================");
					
					day = true;
					
					winResult();
					if(win == 1){
						System.out.println("================");
						System.out.println("������ ������ �¸��߽��ϴ�.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("������ ������ �¸��߽��ϴ�.");
						sendToAll("================");
						break;
					}
					else if(win == 2){
						System.out.println("================");
						System.out.println("��ũ�� ������ �¸��߽��ϴ�.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("��ũ�� ������ �¸��߽��ϴ�.");
						sendToAll("================");
						break;
					}
					
					System.out.println("1�� ���ҽ��ϴ�.");
					sendToAll("1�� ���ҽ��ϴ�.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("��ǥ �ð��Դϴ�.");
					sendToAll("��ǥ �ð��Դϴ�.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					System.out.println("================");
					System.out.println("���� ã�ƿԽ��ϴ�.");
					System.out.println("================");
					sendToAll("================");
					sendToAll("���� ã�ƿԽ��ϴ�.");
					sendToAll("================");
					
					kill = true;
					day = false;
					
					String vote;
					if((vote = voteResult()) != ""){
						System.out.println("================");
						System.out.println(vote + "���� �߹�Ǿ����ϴ�.");
						System.out.println("================");
						sendToAll("================");
						sendToAll(vote + "���� �߹�Ǿ����ϴ�.");
						sendToAll("================");
						jobs.put(vote, "Dead");
					}
					voteReset();
					
					winResult();
					if(win == 1){
						System.out.println("================");
						System.out.println("������ ������ �¸��߽��ϴ�.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("������ ������ �¸��߽��ϴ�.");
						sendToAll("================");
						break;
					}
					else if(win == 2){
						System.out.println("================");
						System.out.println("��ũ�� ������ �¸��߽��ϴ�.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("��ũ�� ������ �¸��߽��ϴ�.");
						sendToAll("================");
						break;
					}
					
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

