package JavaNetworking.UploadListDownload;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class TcpServerClientCommunication {

    static class TcpServer extends Thread {

        private final int port;
        private final String fileOutput;

        public TcpServer(int port, String fileOutput) {
            //TODO: implement the constructor and init the TCP Server
            this.port = port;
            this.fileOutput = fileOutput;
        }

        public void listen() throws IOException {
            //TODO: Implement listen method by using SocketWorkerThread
            // for each TCP connection
            ServerSocket socket = null;
            socket = new ServerSocket(port);
            while (true) {
                Socket client = socket.accept();
                SocketWorkerThread workerThread = new SocketWorkerThread(client, fileOutput);
                workerThread.start();
            }
        }

        @Override
        public void run() {
            try {
                listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class TcpClient extends Thread {

        private final String serverIpAddress;
        private final int port;

        public TcpClient(String serverIpAddress, int port) {
            //TODO: Implement the constructor and init the connection with TCP Server
            this.serverIpAddress = serverIpAddress;
            this.port = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            PrintWriter writer = null;
            Scanner scanner = null;
            try {
                socket = new Socket(serverIpAddress, port);
                scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (line.equals("UPLOAD")) {
                    writer.println(line);
                    writer.flush();
                    String start = scanner.nextLine();
                    if (start.endsWith("BEGIN")) {
                        writer.println(start);
                        writer.flush();
                        while (!(start = scanner.nextLine()).equals("END")) {
                            writer.println(start);
                            writer.flush();
                        }
                        writer.println("END");
                        writer.flush();
                    }
                } else {
                    writer.println(line);
                    writer.flush();
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
                if (scanner != null)
                    scanner.close();
            }
        }
    }

    static class SocketWorkerThread extends Thread {

        private final Socket socket;
        private final String dir;

        public SocketWorkerThread(Socket socket, String dir) {
            //TODO: implement server-client communication
            // by using the reference of socket
            this.socket = socket;
            this.dir = dir;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String lineFromClient = reader.readLine();
                if (lineFromClient.equals("UPLOAD")) {
                    uploadFile(reader);
                } else if (lineFromClient.equals("LIST")) {
                    listFiles(dir);
                } else if (lineFromClient.startsWith("DOWNLOAD")) {
                    downloadFile(lineFromClient);
                } else {
                    System.out.println("Command doesn't exist.");
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
            }
        }

        private void uploadFile(BufferedReader reader) throws IOException {

            System.out.println("Received the UPLOAD command. You can start the upload.");
            String line;
            if ((line = reader.readLine()).endsWith("BEGIN")) {
                String[] parts = line.split("\\s+");
                String fileName = parts[0];
                File file = new File("src/JavaNetworking/UploadListDownload/files/" + fileName + ".txt");
                PrintWriter writer = new PrintWriter(new FileWriter(file));
                System.out.println("Uploading files start:");
                String read;
                while (!(read = reader.readLine()).equals("END")) {
                    writer.write(read);
                    writer.write("\n");
                    writer.flush();
                }
                System.out.println("Uploading files end.");
                writer.close();
            } else {
                System.out.println("Didn't receive the command to begin the upload.");
            }
        }

        int index = 1;

        private void listFiles(String dir) {
            File file = new File(dir);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        listFiles(f.getAbsolutePath());
                    }
                    if (f.isFile() && f.getName().endsWith(".txt")) {
                        System.out.println(index++ + ") " + f.getName() + " " + f.length() + " " + f.lastModified());
                    }

                }
            }
        }

        private void downloadFile(String lineFromClient) throws IOException {
            String[] parts = lineFromClient.split("\\s+");
            String fileName = parts[1];
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            System.out.println("BEGIN");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("END");
            reader.close();
        }
    }

    public static void main(String[] args) {
        //TODO: implement initial tests with one instance of TCPServer
        //and multiple threads of TCPClient

        TcpServer server = new TcpServer(9357, "src/");
        server.start();

        TcpClient client = new TcpClient("localhost", 9357);
        client.start();
    }
}