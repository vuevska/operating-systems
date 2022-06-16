package JavaNetworking.tcp_laboratory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class TCP {
    public static void main(String[] args) {
        TCPClient client = new TCPClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
        client.start();
    }
}

// first thread for establishing a connection with the server and sending messages
class TCPClient extends Thread {

    private final String serverAddress;
    private final int serverPort;

    public TCPClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner scanner = null;
        Socket socket = null;

        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(System.in);

            writer.println("login:" + Constants.MY_INDEX);
            writer.flush();
            String serverResponse = null;
            if (!(serverResponse = reader.readLine()).isEmpty()) {
                System.out.println(serverResponse);

                writer.println("hello:" + Constants.MY_INDEX);
                writer.flush();

                if (!(serverResponse = reader.readLine()).isEmpty()) {
                    System.out.println(serverResponse);
                    String message = null;
                    new ReceiveMessages(socket).start();
                    while (true) {
                        message = scanner.nextLine();
                        writer.println(Constants.MY_INDEX + ":" + message);
                        writer.flush();
                    }

                }
            } else {
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null)
                writer.close();
            if (scanner != null)
                scanner.close();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

// second thread for receiving messages from colleagues
class ReceiveMessages extends Thread {

    private final Socket socket;

    public ReceiveMessages(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        String responseFromColleague;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((responseFromColleague = reader.readLine()) != null) {
                System.out.println(responseFromColleague);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

class Constants {
    public static final String MY_INDEX = "203007";

    public static final int SERVER_PORT = 9753;
    public static final String SERVER_ADDRESS = "194.149.135.49";
}