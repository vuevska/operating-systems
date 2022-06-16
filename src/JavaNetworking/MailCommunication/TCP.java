package JavaNetworking.MailCommunication;

import RaceConditionAndDeadlock.ChemistrySynchronization.CHCl3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCP {
    public static void main(String[] args) {
        Server server = new Server(8765, "src/JavaNetworking/MailCommunication/server.txt");
        server.start();

        Client client = new Client("localhost", 8765);
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
        PrintWriter fileWriter = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            fileWriter = new PrintWriter(new FileWriter(fileOutput, true));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("-----START OF COMMUNICATION-----");
            writer.println("START " + socket.getInetAddress());
            writer.flush();
            String response = reader.readLine();
            if (response.startsWith("MAIL TO")) {
                String mailTo = response;
                if (checkValidityOfMailAddress(response)) {
                    fileWriter.write(response + "\n");
                    System.out.println("Client says: " + response);
                    writer.println("TNX");
                    writer.flush();

                    if ((response = reader.readLine()).startsWith("MAIL FROM")) {
                        if (checkValidityOfMailAddress(response)) {
                            fileWriter.write(response + "\n");
                            System.out.println("Client says: " + response);
                            writer.println("200");
                            writer.flush();

                            String mailCC;
                            if ((mailCC = reader.readLine()).startsWith("CC")) {
                                if (checkValidityOfMailAddress(response)) {
                                    fileWriter.write(mailCC);
                                    System.out.println("Client says: " + mailCC);

                                    writer.println("RECEIVERS: " + mailTo + ", " + mailCC.split("\\s+")[1]);
                                    writer.flush();

                                    String data;
                                    int numLines = 0;
                                    while (!(data = reader.readLine()).equals("?")) {
                                        fileWriter.write(data + "\n");
                                        System.out.println("Client says: " + data);
                                        numLines++;
                                    }
                                    writer.println("RECEIVED: " + numLines);
                                    writer.flush();

                                    String quit;
                                    if ((quit = reader.readLine()).equals("QUIT")) {
                                        fileWriter.write(quit + "\n\n");
                                        System.out.println("Client says: " + quit);
                                        fileWriter.flush();
                                    }

                                } else {
                                    throw new RuntimeException("Email address not valid!");
                                }
                            }
                        } else {
                            throw new RuntimeException("Email address not valid!");
                        }
                    }
                } else {
                    throw new RuntimeException("Email address not valid!");
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
            if (fileWriter != null)
                fileWriter.close();
        }
    }

    private boolean checkValidityOfMailAddress(String response) {
        String[] parts = response.split("\\s+");
        String mail = parts[parts.length - 1];
        int special = 0;
        for (int i = 0; i < mail.length(); i++) {
            if (mail.charAt(i) == '@')
                special++;
        }
        return special == 1;
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
            String line;
            if ((line = reader.readLine()).startsWith("START")) {
                System.out.println("Server says: " + line);
                System.out.println("Enter an email address to be sent to the server:");
                String emailTo = scanner.nextLine();
                writer.println("MAIL TO: " + emailTo);
                writer.flush();
                if ((line = reader.readLine()).equals("TNX")) {
                    System.out.println("Server says: " + line);

                    System.out.println("Enter an email address from:");
                    String emailFrom = scanner.nextLine();
                    writer.println("MAIL FROM: " + emailFrom);
                    writer.flush();

                    if ((line = reader.readLine()).equals("200")) {
                        System.out.println("Server says: " + line);

                        System.out.println("Write a Carbon Copy email address:");
                        String mailCC = scanner.nextLine();
                        writer.println("CC: " + mailCC);
                        writer.flush();

                        if ((line = reader.readLine()).startsWith("RECEIVERS")) {
                            System.out.println("Server says: " + line);

                            System.out.println("Start sending the data, at the end finish with the symbol '?'");
                            String data;
                            while (!(data = scanner.nextLine()).equals("?")) {
                                writer.println(data);
                                writer.flush();
                            }
                            writer.println(data);
                            writer.flush();

                            String received;
                            if ((received = reader.readLine()).startsWith("RECEIVED")) {
                                System.out.println("Server says: " + received);

                                writer.println("QUIT");
                                writer.flush();
                            }
                        }
                    }
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
            if (writer != null)
                writer.close();
            if (scanner != null)
                scanner.close();
        }

    }
}
