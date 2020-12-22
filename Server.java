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
		Iterator<String> iter = clients.keySet().iterator();
	       
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
	}//특정 직업에 전송
	
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
        		votes.put(name, 0);
        		sendToAll("[" + name + "]님이 접속하셨습니다.");
        		while(in != null){
        			String msg = in.readUTF();
        			
        			if(jobs.get(name) == "Dead"){
        				System.out.println("<사망자>" + msg);
        				sendToJob("Dead", "<사망자>" + msg);
        				continue;
        			}
        			
        			if(msg.startsWith("!")){
        				if(msg.startsWith("!투표")){
        					msg.replace("!투표", "");
        					int vote = votes.get(msg.trim());
        					votes.put(msg.trim(), vote++);
        				}
        				else if(msg.startsWith("!분석") && (jobs.get(name) == "Scientist")){
        					msg.replace("!분석", "");
        					if(jobs.get(msg.trim()) == "Skrull"){
        						sendToAll(msg.trim() + "은 스크럴입니다! 추방하세요!");
        					}
        					else{
        						sendToAll("분석이 실패했습니다.");
        					}
        				}
        				else if(msg.startsWith("!제물") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
        					if(msg.startsWith("!제물")){
        						msg.replace("!제물", "");
        						jobs.put(msg.trim(), "Dead");
        					}
        				}
        				else if(msg.equals("!quit")){
        					System.out.println(name + "님이 퇴장하셨습니다.");
        	        		sendToAll(name + "님이 퇴장하셨습니다.");
        					break;
        				}
        			}// 명령어 입력시
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
	}//Manager 끝
	
	public class DaySetting extends Thread{
		public DaySetting(){
			day = true;
		}
		
		@Override
		public void run(){
			System.out.println("게임을 시작합니다.");
			sendToAll("게임을 시작합니다.");
			
			while(true){
				try {
					System.out.println("낮이 밝았습니다.");
					sendToAll("낮이 밝았습니다.");
					if(win == 1){
						System.out.println("지구인 진영이 승리했습니다.");
						sendToAll("지구인 진영이 승리했습니다.");
						break;
					}
					else if(win == 2){
						System.out.println("스크럴 진영이 승리했습니다.");
						sendToAll("스크럴 진영이 승리했습니다.");
						break;
					}
					day = true;
					if(voteResult() != ""){
						System.out.println(voteResult() + "님이 추방되었습니다.");
						sendToAll(voteResult() + "님이 추방되었습니다.");
						jobs.put(voteResult(), "Dead");
						voteReset();
					}
					
					System.out.println("3분 남았습니다.");
					sendToAll("3분 남았습니다.");
					for(int i = 0; i < 180; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("투표 시간입니다.");
					sendToAll("투표 시간입니다.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("밤이 찾아왔습니다.");
					sendToAll("밤이 찾아왔습니다.");
					day = false;
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}//시간 관리
}//Server

