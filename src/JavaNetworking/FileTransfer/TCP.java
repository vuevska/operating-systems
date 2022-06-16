package JavaNetworking.FileTransfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class TCP {

    public static void main(String[] args) {
        Server server = new Server(2345, "src/JavaNetworking/FileTransfer/");
        server.start();

        Client client = new Client("localhost", 2345, "src/JavaNetworking/FileTransfer/downloadsFolder/");
        client.start();
    }
}

class Server extends Thread {

    private final int port;
    private final String folder;

    public Server(int port, String folder) {
        this.port = port;
        this.folder = folder;
    }

    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            while (true) {
                Socket client = socket.accept();
                WorkerThread thread = new WorkerThread(client, folder);
                thread.start();
            }

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
        }
    }
}

class WorkerThread extends Thread {

    private final Socket socket;
    private final String folder;

    public WorkerThread(Socket socket, String folder) {
        this.socket = socket;
        this.folder = folder;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String fromClient;
            if ((fromClient = reader.readLine()).equals("listFiles")) {
                System.out.println("Server received the listFiles command.");
                System.out.println("Files in the folder: " + folder + "\nChoose which file to download!");
                sendFilesToClient(folder, writer);

                if ((fromClient = reader.readLine()).startsWith("downloadfile")) {
                    String[] partsOfDownloadFileName = fromClient.split("=");
                    String fileName = partsOfDownloadFileName[1].substring(1, partsOfDownloadFileName[1].length() - 1);
                    writer.println("downloadfile:start");
                    System.out.println("downloadfile:start");
                    writer.flush();
                    BufferedReader fileReader = new BufferedReader(new FileReader(new File(folder + "/" + fileName)));
                    String fileLine;
                    while ((fileLine = fileReader.readLine()) != null && fileLine.length() != 0) {
                        writer.println(fileLine);
                        writer.flush();
                    }
                    writer.println("downloadfile:end");
                    System.out.println("downloadfile:end");
                    writer.flush();
                }
            }

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

    private void sendFilesToClient(String folder, PrintWriter writer) throws IOException {
        File file = new File(folder);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    writer.println(f.getName());
                    writer.flush();
                }
            }
            writer.println("listFiles:finish");
            writer.flush();
        }
    }
}

class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;
    private final String downloadsFolder;

    public Client(String serverAddress, int serverPort, String downloadsFolder) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.downloadsFolder = downloadsFolder;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        Scanner scanner = null;
        PrintWriter fileWriter = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            scanner = new Scanner(System.in);
            writer.println("listFiles");
            writer.flush();
            String files;
            int index = 1;
            HashMap<String, String> map = new HashMap<>();
            while (!(files = reader.readLine()).equals("listFiles:finish")) {
                map.put(String.valueOf(index), files);
                System.out.println(index++ + ") " + files);
            }
            String fileNumber = scanner.nextLine();
            String fileName = map.get(fileNumber);
            writer.println("downloadfile?name=<" + fileName + ">");
            writer.flush();

            File file = new File(downloadsFolder + "/" + fileName);
            fileWriter = new PrintWriter(new FileWriter(file));

            String response;
            if ((response = reader.readLine()).equals("downloadfile:start")) {
                while (!(response = reader.readLine()).equals("downloadfile:end")) {
                    fileWriter.write(response + "\n");
                    fileWriter.flush();
                }
            }

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
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null)
                scanner.close();
            if (fileWriter != null)
                fileWriter.close();
        }
    }
}
