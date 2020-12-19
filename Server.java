import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable{
	public static Socket clientSocket;
	public HashMap<String, DataOutputStream> clients;
	public HashMap<String, String> job;
	
	public Server(Socket clientSocket){
		Server.clientSocket = clientSocket;
	}
	
	public static void main(String[] args) {
		System.out.println("������ ���� ���� ����~");

		try(ServerSocket sSocket = new ServerSocket(8000)){
			while(true){
				System.out.println("���� ��� �� ......");
				clientSocket = sSocket.accept();
				Server tes = new Server(clientSocket);
				new Thread(tes).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("������ ���� ���� ����.");
	}
	
	@Override
	public void run() {
		try(
				// ���ſ� ����
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				// �۽ſ� ����
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
		){
			String name;
			name = br.readLine();
			Thread.currentThread().setName(name);
			System.out.println(Thread.currentThread().getName() + " �����!");
			
			String inputLine;
			while((inputLine = br.readLine()) != null) {
				System.out.println(Thread.currentThread().getName() + " : " + inputLine);
				out.println(Thread.currentThread().getName() + " : " + inputLine);
			}
			System.out.println(Thread.currentThread().getName() + " �����!");
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
