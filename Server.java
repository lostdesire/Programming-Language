import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server{
	HashMap<String, DataOutputStream> clients;
	HashMap<String, String> jobs;
	ServerSocket serverSocket = null;
	Socket socket = null;
	boolean day = true;
	
	enum Job{
		ALL, Citizen, Scientist, Skrull, Betrayer, Dead
	}
	
	public Server(){
		clients = new HashMap<String, DataOutputStream>();
		Collections.synchronizedMap(clients);
		jobs = new HashMap<String, String>();
		Collections.synchronizedMap(jobs);
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
		Iterator iter = clients.keySet().iterator();
	       
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
		Iterator iter1 = clients.keySet().iterator();
	       
        while(iter1.hasNext()){
        	if(jobs.get(iter1.next()) == job){
        		try{
                    DataOutputStream out = (DataOutputStream) clients.get(iter1.next());
                    out.writeUTF(msg);
                }catch(Exception e){
                    e.printStackTrace();
                }
        	}
        }
	}//Ư�� ������ ����
	
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
        		sendToAll("[" + name + "]���� �����ϼ̽��ϴ�.");
        		while(in != null){
        			String msg = in.readUTF();
        			
        			if(msg.startsWith("!")){
        				if(msg.startsWith("!��ǥ")){
        					
        				}
        				else if(msg.startsWith("!�м�") && (jobs.get(name) == "Scientist")){
        					
        				}
        				else if(msg.startsWith("!����") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
        					
        				}
        				else if(msg.startsWith("!quit")){

        	        		sendToAll(name + "���� �����ϼ̽��ϴ�.");
        					break;
        				}
        			}// ��ɾ� �Է½�
        			
        			else{
        				System.out.println(msg);
        				sendToAll(msg);
//        				if(day){
//        					sendToAll(msg);
//        				}// �� ä��
//        				else{
//        					sendToJob()
//        				}// �� ä��
        			}
        		}
        	} catch(Exception e){
        		e.printStackTrace();
        	} finally{
        		clients.remove(name);
        		jobs.remove(name);
        	}
        }
	}//Manager ��
}//Server