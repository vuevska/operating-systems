package JavaNetworking.tcp.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {

    private final String serverName;
    private final int serverPort;

    public TCPClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;

        Scanner scanner = null;
        PrintWriter writer = null;

        try {
            socket = new Socket(serverName, serverPort);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                writer.println(line);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPClient client = new TCPClient("localhost", 9000);
        client.start();
    }
}
