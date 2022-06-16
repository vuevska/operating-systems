package JavaInputOutput;

import java.io.*;

public class TextFileContentCopy {
    public static void writeTextFile(String from, String to) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(from));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(to));

        String line = null;
        bufferedWriter.write("THIS IS THE COPY FILE\n");
        while((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line + "\n");
        }
        bufferedReader.close();
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        String from = "src/JavaInputOutput/test.txt";
        String to = "src/JavaInputOutput/testCopy.txt";
        try {
            writeTextFile(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
