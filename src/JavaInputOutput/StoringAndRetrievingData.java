package JavaInputOutput;

import java.io.*;

public class StoringAndRetrievingData {
    public static void dataReadWrite() throws IOException {
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        try {
            outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("src/JavaInputOutput/Data.txt")));
            outputStream.writeDouble(3.14);
            outputStream.writeUTF("That was pi");
            outputStream.writeDouble(1.414);
            outputStream.writeUTF("Square root of 2");
            outputStream.flush();
            inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("src/JavaInputOutput/Data.txt")));
            // only readUTF() will recover the Java-UTF String properly
            System.out.println(inputStream.readDouble());
            System.out.println(inputStream.readUTF());
            System.out.println(inputStream.readDouble());
            System.out.println(inputStream.readUTF());
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        }
    }

    public static void main(String[] args) {
        try {
            dataReadWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
