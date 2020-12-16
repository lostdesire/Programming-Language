import java.util.*;
import java.io.*;

public class GameStatus {
	HashMap<String, DataOutputStream> clients;
	HashMap<String, String> job;
	int total = 10;		// 총 인원
	int cit = 6;		// 시민 인원
	int sci = 1;		// 과학자 인원
	int skr = 2;		// 스크럴 인원
	int bet = 1;		// 배신자 인원
	boolean day = true;
	boolean talk = true;
	
	public Game game;
	public Server server;
	
	Iterator<String> iter = job.keySet().iterator();
	
	public GameStatus() {
		System.out.println("GameStatus 생성");
	}
	
	public void Setting(Server server){
		try{
			this.server = server;
			this.clients = server.clients;
			this.job = server.job;
			
			if(clients.isEmpty()){
				System.out.println("인원이 없습니다.");
			}
			else{
				System.out.println("직업을 설정합니다.");
				String person[] = new String[total];
				Arrays.fill(person, 0); 			// 0 : 시민, 1 : 과학자
													// 2 : 스크럴, 3 : 배신자
				for(int i = 0; i < sci + skr + bet; i++){
					if(i < sci)
						person[i] = "1";
					else if(i < sci + skr)
						person[i] = "2";
					else if(i < sci + skr + bet)
						person[i] = "3";
				}
				
				List<String> person_list = Arrays.asList(person);
				Collections.shuffle(person_list);
				
				for(int i = 0; i < person_list.size() - 1; i ++){
					iter = clients.keySet().iterator();
					switch(person_list.get(i)){
					case "0":
						job.put(iter.next(), "Citizen");
					case "1":
						job.put(iter.next(), "Scientist");
					case "2":
						job.put(iter.next(), "Skrull");
					case "3":
						job.put(iter.next(), "Betrayer");
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void GameStart() {
		System.out.println("-----게임 시작-----");
		
	}
	
	public class Game extends Thread {
		public int Win(){
			int win;		// 0 : 게임 진행, 1 : 지구인 진영 승리, 2 : 스크럴 진영 승리
			int skr_num = 0;
			int earth_num = 0;
			
			win = 0;
			
			while(iter.hasNext()){
				String temp_client = iter.next();
				
				switch(temp_client){
				case "Citizen":
					earth_num++;
					break;
				case "Scientist":
					earth_num++;
					break;
				case "Skrull":
					skr_num++;
					break;
				case "Betrayer":
					skr_num++;
					break;
				}
			}
			if(skr_num >= earth_num){
				System.out.println("스크럴 진영이 승리했습니다.");
				win = 1;
			}
			else if(skr_num == 0){
				System.out.println("지구인 진영이 승리했습니다.");
				win = 2;
			}
			else{
				win = 0;
			}
			return win;
		}
		
		public void Timer(int sec){
			System.out.println(sec / 60 + "분 남았습니다.");
			try {
				Thread.sleep(sec * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void Time(){
			while(true){
				day = true;
				
				System.out.println("낮이 찾아왔습니다.");
				if(Win() != 0){
					break;
				}
				Timer(120);
				Timer(60);
				
				System.out.println("투표 시간입니다.");
				Timer(60);
				
				day = false;
				
				System.out.println("밤이 찾아왔습니다.");
				Timer(60);
				
				
			}
		}
	}
	
	
	
	
}


