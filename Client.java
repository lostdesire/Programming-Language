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
		System.out.println("���� Ŭ���̾�Ʈ ����~");
		System.out.print("�̸� �Է� : ");
		String nickname = scv.nextLine();
		try {
			InetAddress localAddress = InetAddress.getLocalHost();

			try (Socket cSocket = new Socket(localAddress, 8000);
					PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
					BufferedReader br = new BufferedReader(new InputStreamReader(cSocket.getInputStream()))
							) {
				System.out.println("������ �����!");
				while (true) {
					System.out.print("�޼��� �Է� : ");
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
