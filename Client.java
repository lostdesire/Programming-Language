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
            System.out.println("���Ǿ� ���� ����");
           
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String name = "";
            while(true){
                System.out.println("�̸��� �Է��� �ּ���.");
                name = scv.nextLine();
                out.writeUTF(name);
                out.flush();
                String chk_code = in.readUTF();
                if(!chk_code.equals("�ߺ��� �̸��Դϴ�.")){
                    break;
                }
                System.out.println("�ߺ��� �̸��Դϴ�.");
            }
                
            System.out.println("���Ǿ� ���� ����");
            
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
	}//Receiver ������
	
	@Override
	public void run(){
		try{
			while(in != null){
				String msg = in.readUTF();
                if(msg.equals("�ߺ��� �̸��Դϴ�.")){
                    System.out.println("�ߺ��� �̸��Դϴ�.");
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
    }//Sender ������
    
    @Override
    public void run(){
    	Scanner scv = new Scanner(System.in);
    	try{
    		while(out != null){
    			String msg = scv.nextLine();
    			
    			if(msg.trim().startsWith("!")){
    				if(msg.startsWith("!��ǥ")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!�м�")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!����")){
    					out.writeUTF(msg);
    				}
    				else if(msg.startsWith("!quit")){
    					System.out.println("�������� �����ϴ�.");
    					out.writeUTF(msg);
    					scv.close();
    					System.exit(0);
    					break;
    				}
    				else{
    					System.out.println("���� ��ɾ� ���ϴ�. �ٽ� �Է��� �ּ���.");
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