package JavaNetworking.FilterFilesSmallerThan100KB;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3398;
    private static final String FOLDER_TO_SEARCH_FROM = "src/";
    private static final String RESULT_FILE = "src/JavaNetworking/FilterFilesSmallerThan100KB/clients_data.txt";

    public static void main(String[] args) {

        Server server = new Server(SERVER_PORT, RESULT_FILE);
        server.start();

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT, FOLDER_TO_SEARCH_FROM);
        client.start();
    }
}

class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;
    private final String pathToSearchFrom;

    public Client(String serverAddress, int serverPort, String pathToSearchFrom) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.pathToSearchFrom = pathToSearchFrom;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter writer = null;

        try {
            socket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendDataToServer(writer, pathToSearchFrom);
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

    private void sendDataToServer(PrintWriter writer, String pathToSearchFrom) {
        int num = numFiles(pathToSearchFrom);
        writer.println(num);
        writer.flush();
    }

    private int numFiles(String pathToSearchFrom) {
        File file = new File(pathToSearchFrom);
        int num = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    num += numFiles(f.getAbsolutePath());
                } else if (f.isFile() && (f.getName().endsWith(".txt") || f.getName().endsWith(".csv"))) {
                    if (f.length() < 100 * 1024 && f.length() > 10 * 1024)
                        num++;
                }
            }
        }
        return num;
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
            receiveDataAndWriteIntoResultFile(socket, writer);
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

    private void receiveDataAndWriteIntoResultFile(Socket socket, PrintWriter writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String numFiles = reader.readLine();
        InetAddress clientAddress = socket.getInetAddress();
        int clientPort = socket.getPort();
        writer.write(String.format("%s %d %s\n", clientAddress, clientPort, numFiles));
        writer.flush();
    }
}
