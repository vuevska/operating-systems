package JavaNetworking.VaccineStats;

import RaceConditionAndDeadlock.ChemistrySynchronization.CHCl3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class TCP {
    public static void main(String[] args) {
        Server server = new Server(5555, "src/JavaNetworking/VaccineStats/data.csv");
        server.start();

        Client client = new Client("localhost", 5555);
        client.start();
    }
}

class Server extends Thread {

    private final int port;
    private final String fileOutput;

    public Server(int port, String fileOutput) {
        this.port = port;
        this.fileOutput = fileOutput;
    }

    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            while (true) {
                Socket client = socket.accept();
                WorkerThread thread = new WorkerThread(client, fileOutput);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class WorkerThread extends Thread {

    private final Socket socket;
    private final String fileOutput;

    public WorkerThread(Socket socket, String fileOutput) {
        this.socket = socket;
        this.fileOutput = fileOutput;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.println("Server says: HELLO " + socket.getInetAddress());
            writer.flush();
            String response;
            if ((response = reader.readLine()).startsWith("Client says: HELLO")) {
                System.out.println(response);
                writer.println("Server says: SEND DAILY DATA");
                writer.flush();
                if ((response = reader.readLine()) != null) {
                    String[] split = response.split("\\s+");
                    if (split.length != 3) {
                        System.out.println("Insufficient information!");
                        return;
                    } else {
                        File file = new File(fileOutput);
                        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true));
                        fileWriter.write(LocalDate.now() + ", " + split[0] + ", " + split[1] + ", " + split[2] + "\n");
                        fileWriter.flush();
                        writer.println("Server says: OK");
                        writer.flush();
                        if ((response = reader.readLine()).equals("Client says: QUIT")) {
                            System.out.println(response);
                        }
                    }
                }
            } else {
                throw new RuntimeException();
            }
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
}

class Client extends Thread {

    private final String serverAddress;
    private final int serverPort;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner scanner = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(System.in);
            String message = reader.readLine();
            if (message.startsWith("Server says: HELLO")) {
                System.out.println(message);
                writer.println("Client says: HELLO " + socket.getPort());
                writer.flush();
                String response;
                if ((response = reader.readLine()).endsWith("SEND DAILY DATA")) {
                    System.out.println(response);
                    // no. of vaccinated with Pfizer, no. of vaccinated with Sinovac, no. of vaccinated with AstraZaneca
                    writer.println(scanner.nextLine());
                    writer.flush();
                    if ((response = reader.readLine()).equals("Server says: OK")) {
                        System.out.println(response);
                        writer.println("Client says: QUIT");
                        writer.flush();
                    } else {
                        throw new RuntimeException();
                    }
                } else {
                    throw new RuntimeException();
                }
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