package JavaNetworking.FilterFilesRecursive;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

public class Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3398;
    private static final String FOLDER_TO_SEARCH_FROM = "src/";
    private static final String FOLDER_TO_SEND_TO_SERVER = "src/JavaNetworking/FilterFilesRecursive/files.txt";
    private static final String RESULT_FILE = "src/JavaNetworking/FilterFilesRecursive/_data.txt";

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT, RESULT_FILE);
        server.start();

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT, FOLDER_TO_SEARCH_FROM, FOLDER_TO_SEND_TO_SERVER);
        client.start();
    }
}

class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;
    private final String pathToFilter;
    private final String fileToSendToServer;

    public Client(String serverAddress, int serverPort, String pathToFilter, String fileToSendToServer) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.pathToFilter = pathToFilter;
        this.fileToSendToServer = fileToSendToServer;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(new FileWriter(fileToSendToServer));
            filterFiles(pathToFilter, writer);
            sendFileToServer(fileToSendToServer, socket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null)
                writer.close();
        }
    }

    private void filterFiles(String pathToFilter, PrintWriter writer) {
        File file = new File(pathToFilter);
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    filterFiles(f.getAbsolutePath(), writer);
                } else if (f.isFile() && (f.getName().endsWith(".txt") || f.getName().endsWith(".dat"))) {
                    writer.write(String.format("%s,%d\n", f.getAbsolutePath(), f.length()));
                    writer.flush();
                }
            }
        }
    }

    private void sendFileToServer(String fileToSendToServer, Socket socket) throws IOException {
        PrintWriter writerToServer;
        writerToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        File file = new File(fileToSendToServer);
        long size = file.length();
        long lastModifies = file.lastModified();
        writerToServer.println(size);
        writerToServer.println(lastModifies);
        writerToServer.flush();
        writerToServer.close();
    }
}

class Server extends Thread {

    private final int port;
    private final String resultFile;

    public Server(int port, String resultFile) {
        this.port = port;
        this.resultFile = resultFile;
    }

    @Override
    public void run() {
        ServerSocket socket;
        PrintWriter writer;
        try {
            socket = new ServerSocket(port);
            writer = new PrintWriter(new FileWriter(resultFile));
            while (true) {
                Socket client = socket.accept();
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
            receiveFilesFromClient(socket, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null)
                writer.close();
        }
    }

    private void receiveFilesFromClient(Socket socket, PrintWriter writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        InetAddress address = socket.getInetAddress();
        int port = socket.getPort();
        String size = reader.readLine();
        String modification = reader.readLine();
        writer.write(String.format("%s %d %s %s\n", address, port, size, modification));
        writer.flush();
        reader.close();
    }
}