import java.util.*;
import java.io.*;

public class GameStatus {
	HashMap<String, DataOutputStream> clients;
	HashMap<String, String> job;
	int total = 10;		// �� �ο�
	int cit = 6;		// �ù� �ο�
	int sci = 1;		// ������ �ο�
	int skr = 2;		// ��ũ�� �ο�
	int bet = 1;		// ����� �ο�
	boolean day = true;
	boolean talk = true;
	
	public Game game;
	public Server server;
	
	Iterator<String> iter = job.keySet().iterator();
	
	public GameStatus() {
		System.out.println("GameStatus ����");
	}
	
	public void Setting(Server server){
		try{
			this.server = server;
			this.clients = server.clients;
			this.job = server.job;
			
			if(clients.isEmpty()){
				System.out.println("�ο��� �����ϴ�.");
			}
			else{
				System.out.println("������ �����մϴ�.");
				String person[] = new String[total];
				Arrays.fill(person, 0); 			// 0 : �ù�, 1 : ������
													// 2 : ��ũ��, 3 : �����
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
		System.out.println("-----���� ����-----");
		
	}
	
	public class Game extends Thread {
		public int Win(){
			int win;		// 0 : ���� ����, 1 : ������ ���� �¸�, 2 : ��ũ�� ���� �¸�
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
				System.out.println("��ũ�� ������ �¸��߽��ϴ�.");
				win = 1;
			}
			else if(skr_num == 0){
				System.out.println("������ ������ �¸��߽��ϴ�.");
				win = 2;
			}
			else{
				win = 0;
			}
			return win;
		}
		
		public void Timer(int sec){
			System.out.println(sec / 60 + "�� ���ҽ��ϴ�.");
			try {
				Thread.sleep(sec * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void Time(){
			while(true){
				day = true;
				
				System.out.println("���� ã�ƿԽ��ϴ�.");
				if(Win() != 0){
					break;
				}
				Timer(120);
				Timer(60);
				
				System.out.println("��ǥ �ð��Դϴ�.");
				Timer(60);
				
				day = false;
				
				System.out.println("���� ã�ƿԽ��ϴ�.");
				Timer(60);
				
				
			}
		}
	}
	
	
	
	
}


