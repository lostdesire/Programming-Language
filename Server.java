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
	}//Server 생성자
	
	public void init(){
		try{
			serverSocket = new ServerSocket(8000);
			System.out.println("마피아 서버 시작");
			
			while(true){
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "에서 접속");
               
                Thread manager = new Manager(socket);
                manager.start();
            }
		}catch(Exception e){
            e.printStackTrace();
        }
	}//Server 초기설정
	
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
	}//전체 전송
	
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
	}//특정 직업에 전송
	
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
        }//Manager 생성자
        
        @Override
        public void run(){
        	String name = "";
        	try{
        		while( (name = in.readUTF())!= null){
        			if(clients.containsKey(name)){
                         out.writeUTF("중복된 이름입니다.");
                    }
        			else{
        				System.out.println("[" + name + "]님이 접속하셨습니다.");
                    	out.writeUTF("");
                    	break;
        			}
        		}
        		clients.put(name, out);
        		sendToAll("[" + name + "]님이 접속하셨습니다.");
        		while(in != null){
        			String msg = in.readUTF();
        			
        			if(msg.startsWith("!")){
        				if(msg.startsWith("!투표")){
        					
        				}
        				else if(msg.startsWith("!분석") && (jobs.get(name) == "Scientist")){
        					
        				}
        				else if(msg.startsWith("!제물") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
        					
        				}
        				else if(msg.startsWith("!quit")){

        	        		sendToAll(name + "님이 퇴장하셨습니다.");
        					break;
        				}
        			}// 명령어 입력시
        			
        			else{
        				System.out.println(msg);
        				sendToAll(msg);
//        				if(day){
//        					sendToAll(msg);
//        				}// 낮 채팅
//        				else{
//        					sendToJob()
//        				}// 밤 채팅
        			}
        		}
        	} catch(Exception e){
        		e.printStackTrace();
        	} finally{
        		clients.remove(name);
        		jobs.remove(name);
        	}
        }
	}//Manager 끝
}//Server