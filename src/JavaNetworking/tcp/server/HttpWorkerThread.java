package JavaNetworking.tcp.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpWorkerThread extends Thread {

    private Socket socket;

    public HttpWorkerThread(Socket socket) {
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
            StringBuilder builder = new StringBuilder();
            while (!(line = reader.readLine()).isEmpty()) {
                builder.append(line);
                System.out.println(line);
            }
            RequestProcessor requestProcessor = RequestProcessor.of(builder.toString());
            writer.write("HTTP/1.1 200 OK\n\n");

            if (requestProcessor.getCommand().equals("GET") && requestProcessor.getUri().equals("/time")) {
                writer.printf("<html><body><h1>%s</h1></body></html>", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            } else {
                writer.printf("<html><body><h1>HELLO WORLD!</h1></body></html>");
            }

            writer.flush();

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
