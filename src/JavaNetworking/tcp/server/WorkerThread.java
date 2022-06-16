package JavaNetworking.tcp.server;

import java.io.*;
import java.net.Socket;

public class WorkerThread extends Thread {

    private final Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            System.out.printf("Connected:%s:%d\n", socket.getInetAddress(), socket.getPort());

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = null;
            while (!(line = reader.readLine()).isEmpty()) {
                System.out.println(line);
                writer.write(line);
                writer.flush();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null)
                writer.close();
        }
    }
}
