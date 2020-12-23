import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args){
		Scanner scv = new Scanner(System.in);
		
		try{
			InetAddress localAddress = InetAddress.getLocalHost();
            Socket socket = new Socket(localAddress, 8000);      
            System.out.println("마피아 서버 접속");
           
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String name = "";
            while(true){
                System.out.println("이름을 입력해 주세요.");
                name = scv.nextLine();
                out.writeUTF(name);
                out.flush();
                String chk_code = in.readUTF();
                if(!chk_code.equals("중복된 이름입니다.")){
                    break;
                }
                System.out.println("중복된 이름입니다.");
            }
                
            System.out.println("마피아 서버 입장");
            
            Thread sender = new Sender(name, socket);
            sender.start();
            Thread receiver = new Receiver(socket); 
            receiver.start();
        }catch(Exception e){
        	scv.close();
        	e.printStackTrace();
        }   
    }//Main
}

class Receiver extends Thread{
	Socket socket;
	DataInputStream in;
	
	public Receiver(Socket socket){
		this.socket = socket;
		try{
			in = new DataInputStream(this.socket.getInputStream());
		} catch(Exception e){
			e.printStackTrace();
		}
	}//Receiver 생성자
	
	@Override
	public void run(){
		try{
			while(in != null){
				String msg = in.readUTF();
                if(msg.equals("중복된 이름입니다.")){
                    System.out.println("중복된 이름입니다.");
                }else{
                    System.out.println(msg);
                }
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

class Sender extends Thread{
	Socket socket;
	DataOutputStream out;
    String name;
    
    public Sender(String name, Socket socket){
    	this.socket = socket;
    	try{
    		out = new DataOutputStream(this.socket.getOutputStream());
    		this.name = name;
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }//Sender 생성자
    
    @Override
    public void run(){
    	Scanner scv = new Scanner(System.in);
    	try{
    		while(out != null){
    			String msg = scv.nextLine();
    			
    			if(msg.trim().startsWith("!")){
    				if(msg.startsWith("!투표")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!분석")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!제물")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!quit")){
    					System.out.println("서버에서 나갑니다.");
    					out.writeUTF(msg);
    					scv.close();
    					System.exit(0);
    					break;
    				}
    				else{
    					System.out.println("없는 명령어 업니다. 다시 입력해 주세요.");
    				}
    			}
    			else{
    				out.writeUTF("[" + name + "] : " + msg);
    			}
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
}