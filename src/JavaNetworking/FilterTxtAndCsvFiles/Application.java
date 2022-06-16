package JavaNetworking.FilterTxtAndCsvFiles;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3398;
    private static final String RESULT_FILE = "src/JavaNetworking/FilterTxtAndCsvFiles/clients_data.txt";
    private static final String FOLDER_TO_FILTER = "src/JavaNetworking/FilterTxtAndCsvFiles/";

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT, RESULT_FILE);
        server.start();

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT, FOLDER_TO_FILTER);
        client.start();
    }
}

class Server extends Thread {

    private final int port;
    private final String filePath;

    public Server(int port, String filePath) {
        this.port = port;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        PrintWriter writer = null;
        System.out.println("Sever is starting...");
        try {
            serverSocket = new ServerSocket(port);
            writer = new PrintWriter(new FileWriter(filePath));
            while (true) {
                Socket client = serverSocket.accept();
                SocketWorker worker = new SocketWorker(client, writer);
                worker.start();
            }
        } catch (IOException e) {
            System.err.println("Server failed to start.");
        }
    }
}

class SocketWorker extends Thread {

    private final Socket socket;
    private final PrintWriter writer;

    public SocketWorker(Socket socket, PrintWriter writer) {
        this.socket = socket;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            receiveData(socket, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveData(Socket socket, PrintWriter writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String numFiles = reader.readLine();
        writer.println(String.format("%s %d %s\n", socket.getInetAddress(), socket.getPort(), numFiles));
        writer.flush();
        reader.close();
    }
}

class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;
    private final String folderPath;

    public Client(String serverAddress, int serverPort, String folderPath) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.folderPath = folderPath;
    }

    @Override
    public void run() {
        System.out.println("Client is here.");
        int numFiles = getNumFiles(folderPath);
        try {
            sendDataToServer(serverAddress, serverPort, numFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNumFiles(String folderPath) {
        File file = new File(folderPath);
        File[] files = file.listFiles();
        int num = 0;
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    num += getNumFiles(f.getAbsolutePath());
                } else if (f.isFile() && (f.getName().endsWith(".txt") || f.getName().endsWith("csv")))
                    if (f.length() < 100 * 1024 && f.length() > 10 * 1024)
                        num++;
            }
        }
        return num;
    }

    private void sendDataToServer(String serverAddress, int port, int numFiles) throws IOException {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            socket = new Socket(serverAddress, port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(numFiles);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (writer != null)
                writer.close();
        }
    }
}

