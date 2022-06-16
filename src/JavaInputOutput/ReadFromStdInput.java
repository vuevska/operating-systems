package JavaInputOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadFromStdInput {
    public static void stdinRead() throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String s;
        System.out.println("Enter a line:");
        while ((s = stdin.readLine()) != null && s.length() != 0)
            System.out.println(s);
    }

    public static void main(String[] args) {
        try {
            stdinRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


