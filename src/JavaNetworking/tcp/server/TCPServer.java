package JavaNetworking.tcp.server;

import JavaNetworking.tcp.client.TCPClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {

    private final int port;

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("TCP is staring...");
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException exception) {
            System.err.println("Socket Server failed to start.");
            return;
        }
        System.out.println("TCP server is started.");
        System.out.println("Waiting for connections...");
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                /*WorkerThread workerThread = new WorkerThread(socket);
                workerThread.start();*/
                new HttpWorkerThread(socket).start();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer(9000);
        server.start();
    }
}
