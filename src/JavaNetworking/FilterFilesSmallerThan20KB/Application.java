package JavaNetworking.FilterFilesSmallerThan20KB;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 4498;
    private static final String FOLDER_TO_SEARCH_FROM = "src/";
    private static final String FOLDER_TO_SEARCH_FROM1 = "src/JavaNetworking/";
    private static final String RESULT_FILE = "src/JavaNetworking/FilterFilesSmallerThan20KB/results.txt";

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT, RESULT_FILE);
        server.start();

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT, FOLDER_TO_SEARCH_FROM);
        client.start();

        Client client1 = new Client(SERVER_ADDRESS, SERVER_PORT, FOLDER_TO_SEARCH_FROM1);
        client1.start();
    }
}

class Server extends Thread {

    private final int port;
    private final String pathToWriteResult;

    public Server(int port, String pathToWriteResult) {
        this.port = port;
        this.pathToWriteResult = pathToWriteResult;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        PrintWriter writer = null;
        try {
            System.out.println("Server is starting...");
            writer = new PrintWriter(new FileWriter(pathToWriteResult));
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
                SocketWorker worker = new SocketWorker(client, writer);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            receiveNumOfFiles(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveNumOfFiles(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String numFiles = reader.readLine();
        synchronized (SocketWorker.class) {
            writer.write(String.format("%s %d %s\n", socket.getInetAddress(), socket.getPort(), numFiles));
            writer.flush();
        }
    }
}


class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;
    private final String folderToFilter;

    public Client(String serverAddress, int serverPort, String folderToFilter) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.folderToFilter = folderToFilter;
    }

    @Override
    public void run() {
        PrintWriter writer = null;
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            int numFiles = filterFiles(folderToFilter);
            writer.println(numFiles);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int filterFiles(String folderToFilter) {
        File file = new File(folderToFilter);
        int numFilesSmallerThan20K = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.length() < 20 * 1024)
                    numFilesSmallerThan20K++;
            }
        }
        return numFilesSmallerThan20K;
    }
}

