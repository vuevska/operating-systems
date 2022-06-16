package JavaInputOutput;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileOutputWithLineNumbers {
    public static void fileOutputWithLineNumbers() throws IOException {
        String outFile = "src/JavaInputOutput/fileOutputWithLineNumbers.out";
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new FileReader("src/JavaInputOutput/test.txt"));
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            int lineCount = 1;
            String s;
            while((s = in.readLine()) != null)
                out.println(lineCount++ + ": " + s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
        }
        System.out.println(readTextFile(outFile));
    }

    public static String readTextFile(String path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        String line = null;

        // this approach will fail for huge files
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        try {
            fileOutputWithLineNumbers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
