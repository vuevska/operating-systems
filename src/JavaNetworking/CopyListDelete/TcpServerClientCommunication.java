package JavaNetworking.CopyListDelete;

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

        public void listen() {
            //TODO: Implement listen method by using SocketWorkerThread
            // for each TCP connection

            ServerSocket socket = null;
            PrintWriter writer = null;
            try {
                socket = new ServerSocket(port);
                while (true) {
                    Socket client = socket.accept();
                    SocketWorkerThread workerThread = new SocketWorkerThread(client, fileOutput);
                    workerThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            listen();
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
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                scanner = new Scanner(System.in);
                String command = scanner.nextLine();
                writer.println(command);
                writer.flush();
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
            try {
                receiveCommandFromClient(socket, dir);
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

        private void receiveCommandFromClient(Socket socket, String dir) throws IOException {
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            if (line.startsWith("COPY")) {
                copyFiles(line);
            } else if (line.startsWith("LIST")) {
                listFiles(dir);
            } else if (line.startsWith("DELETE")) {
                deleteFile(line);
            } else {
                System.out.println("Command doesn't exist.");
            }
        }

        private void copyFiles(String line) throws IOException {
            String[] parts = line.split("\\s+");
            String copyFrom = parts[1];
            String copyTo = parts[2];
            File first = new File(copyFrom);
            File[] files = first.listFiles();
            File copy = new File(copyTo);
            boolean made = copy.mkdir();
            System.out.println(made ? "Copied folder is made." : "Can't makde the copied folder");
            if (files != null) {
                for (File f : files) {
                    copyContend(f.getAbsolutePath(), copy + "/" + f.getName());
                }
            }
        }

        private void copyContend(String fileFrom, String fileTo) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(fileFrom));
            PrintWriter writer = new PrintWriter(new FileWriter(fileTo));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
                writer.flush();
            }
            reader.close();
            writer.close();
        }

        int index = 1;

        private void listFiles(String dir) {
            File file = new File(dir);
            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory())
                        listFiles(f.getAbsolutePath());
                    else if (f.isFile() && f.getName().endsWith(".txt")) {
                        System.out.println(index++ + ") " + f.getName() + " " + f.length() + " " + f.lastModified());
                    }
                }
            }
        }

        private void deleteFile(String line) {
            String[] parts = line.split("\\s+");
            File toDelete = new File(parts[1]);
            boolean delete = toDelete.delete();
            System.out.println(delete ? "Successfully deleted " + toDelete : "Can't delete " + toDelete);
        }
    }

    public static void main(String[] args) {
        //TODO: implement initial tests with one instance of TCPServer
        //and multiple threads of TCPClient

        TcpServer server = new TcpServer(7953, "src/");
        server.start();

        TcpClient client = new TcpClient("localhost", 7953);
        client.start();
    }
}