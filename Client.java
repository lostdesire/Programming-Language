import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		Scanner scv = new Scanner(System.in);
		System.out.println("에코 클라이언트 시작~");
		System.out.print("이름 입력 : ");
		String nickname = scv.nextLine();
		try {
			InetAddress localAddress = InetAddress.getLocalHost();

			try (Socket cSocket = new Socket(localAddress, 8000);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()))
							) {
				System.out.println("서버에 연결됨!");
				while (true) {
					System.out.print("메세지 입력 : ");
					String inputLine = scv.nextLine();
					if ("quit".equalsIgnoreCase(inputLine)) {
						break;
					}
					out.println(inputLine);
					String response = br.readLine();
					System.out.println(response);
				}
				scv.close();
			}
		} catch (IOException ex) {

		}
	}
}
