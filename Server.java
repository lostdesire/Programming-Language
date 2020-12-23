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
	}//특정 직업에 전송
	
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
		sendToJob("Citizen", "당신은 시민입니다.");
		sendToJob("Scientist", "당신은 과학자입니다.");
		sendToJob("Skrull", "당신은 스크럴입니다.");
		sendToJob("Betrayer", "당신은 배신자입니다.");
		
		sendToJob("Skrull", "================");
		sendToJob("Betrayer", "================");
		sendToJob("Skrull", "과학자는 " + sci_name + "입니다.");
		sendToJob("Betrayer", "과학자는 " + sci_name + "입니다.");
		sendToJob("Skrull", "================");
		sendToJob("Betrayer", "================");
	}//직업 배분
	
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
	}//투표 결과
	
	public void voteReset(){
		Iterator<String> iter = votes.keySet().iterator();
		
		while(iter.hasNext()){
			votes.put(iter.next(), 0);
		}
	}//투표 리셋
	
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
	}//승리 결과
	
	public void deleteName(String name){
		clients.remove(name);
		jobs.remove(name);
		votes.remove(name);
	}//나간 인원 비우기
	
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
	}//스크럴 2명 생존 체크
	
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
        		sendToAll("---------------------");
        		sendToAll("[" + name + "]님이 접속하셨습니다.");
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
        						System.out.println(name + "님이 퇴장하셨습니다.");
            	        		sendToAll(name + "님이 퇴장하셨습니다.");
            	        		deleteName(name);
            					break;
        					}
            				System.out.println("<사망자>" + msg);
            				sendToJob("Dead", "<사망자>" + msg);
            				continue;
            			}
        				if(msg.startsWith("!")){
            				if(msg.startsWith("!투표")){
            					msg = msg.replace("!투표 ", "");
            					if(jobs.get(msg) == "Dead"){
            						sendToJob("이미 죽은 사람입니다." ,"Skrull");
        							sendToJob("이미 죽은 사람입니다." ,"Betrayer");
            						continue;
            					}
            					int vote = votes.get(msg);
            					votes.put(msg, vote + 1);
            				}
            				else if(msg.startsWith("!분석") && (jobs.get(name) == "Scientist")){
            					msg = msg.replace("!분석 ", "");
            					if(jobs.get(msg) == "Scientist"){
            						continue;
            					}
            					if(jobs.get(msg) == "Skrull"){
            						sendToAll(msg + "은 스크럴입니다! 추방하세요!");
            					}
            					else{
            						sendToAll("분석이 실패했습니다.");
            					}
            				}
            				else if(msg.equals("!quit")){
            					System.out.println(name + "님이 퇴장하셨습니다.");
            	        		sendToAll(name + "님이 퇴장하셨습니다.");
            	        		deleteName(name);
            					break;
            				}
        				}
        				else{
            				System.out.println(msg);
            				sendToAll(msg);
        				}
        			}//낮
        			
        			if(!day){
        				if(jobs.get(name) == "Dead"){
        					if(msg.equals("!quit")){
        						System.out.println(name + "님이 퇴장하셨습니다.");
            	        		sendToAll(name + "님이 퇴장하셨습니다.");
            	        		deleteName(name);
            					break;
        					}
            				System.out.println("<사망자>" + msg);
            				sendToJob("Dead", "<사망자>" + msg);
            				continue;
            			}
        				if(msg.startsWith("!")){
            				if(msg.startsWith("!제물") && ((jobs.get(name) == "Skrull") || (jobs.get(name) == "Betrayer"))){
            					if(msg.startsWith("!제물") && kill){
            						msg = msg.replace("!제물 ", "");
            						if(jobs.get(msg) == "Skrull" || jobs.get(msg) == "Betrayer"){
            							sendToJob("같은 스크럴 진영입니다." ,"Skrull");
            							sendToJob("같은 스크럴 진영입니다." ,"Betrayer");
            							continue;
            						}
            						else if(jobs.get(msg) == "Dead"){
            							sendToJob("이미 죽은 사람입니다." ,"Skrull");
            							sendToJob("이미 죽은 사람입니다." ,"Betrayer");
            							continue;
            						}
            						else if(isTwoSkrull() && jobs.get(msg) == "Scientist"){
            							sendToJob("아직 과학자를 죽일수 없습니다." ,"Skrull");
            							sendToJob("아직 과학자를 죽일수 없습니다." ,"Betrayer");
            							continue;
            						}
            						sendToAll("제물이 선택되었습니다.");
            						sendToAll("선택된 제물은 " + msg + "입니다.");
            						jobs.put(msg, "Dead");
            						kill = false;
            					}
            				}
            				else if(msg.equals("!quit")){
            					System.out.println(name + "님이 퇴장하셨습니다.");
            					deleteName(name);
            	        		sendToAll(name + "님이 퇴장하셨습니다.");
            					break;
            				}
            			}// 명령어 입력시
            			else if(jobs.get(name) == "Skrull" || jobs.get(name) == "Betrayer"){
            				System.out.println(msg);
            				sendToJob("Skrull", msg);
            				sendToJob("Betrayer", msg);
            			}
        			}//밤
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
			System.out.println("================");
			System.out.println("게임을 시작합니다.");
			System.out.println("================");
			sendToAll("================");
			sendToAll("게임을 시작합니다.");
			sendToAll("================");
			
			jobSetting();
			voteReset();
			
			while(true){
				try {
					System.out.println("================");
					System.out.println("낮이 밝았습니다.");
					System.out.println("================");
					sendToAll("================");
					sendToAll("낮이 밝았습니다.");
					sendToAll("================");
					
					day = true;
					
					winResult();
					if(win == 1){
						System.out.println("================");
						System.out.println("지구인 진영이 승리했습니다.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("지구인 진영이 승리했습니다.");
						sendToAll("================");
						break;
					}
					else if(win == 2){
						System.out.println("================");
						System.out.println("스크럴 진영이 승리했습니다.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("스크럴 진영이 승리했습니다.");
						sendToAll("================");
						break;
					}
					
					System.out.println("1분 남았습니다.");
					sendToAll("1분 남았습니다.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					
					System.out.println("투표 시간입니다.");
					sendToAll("투표 시간입니다.");
					for(int i = 0; i < 60; i++){
						Thread.sleep(1000);
					}
					System.out.println("================");
					System.out.println("밤이 찾아왔습니다.");
					System.out.println("================");
					sendToAll("================");
					sendToAll("밤이 찾아왔습니다.");
					sendToAll("================");
					
					kill = true;
					day = false;
					
					String vote;
					if((vote = voteResult()) != ""){
						System.out.println("================");
						System.out.println(vote + "님이 추방되었습니다.");
						System.out.println("================");
						sendToAll("================");
						sendToAll(vote + "님이 추방되었습니다.");
						sendToAll("================");
						jobs.put(vote, "Dead");
					}
					voteReset();
					
					winResult();
					if(win == 1){
						System.out.println("================");
						System.out.println("지구인 진영이 승리했습니다.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("지구인 진영이 승리했습니다.");
						sendToAll("================");
						break;
					}
					else if(win == 2){
						System.out.println("================");
						System.out.println("스크럴 진영이 승리했습니다.");
						System.out.println("================");
						sendToAll("================");
						sendToAll("스크럴 진영이 승리했습니다.");
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
	}//시간 관리
}//Server

