package JavaInputOutput;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteTextFile {
    public static void writeTextFile(String path, String text, boolean append) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, append));
        bufferedWriter.write(text);
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        String path = "src/JavaInputOutput/test.txt";
        try {
            writeTextFile(path, "\nnew text", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
