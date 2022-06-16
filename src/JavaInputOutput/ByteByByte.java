package JavaInputOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteByByte {
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        try {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void correctReading(InputStream in) throws IOException {
        try {
            byte[] buffer = new byte[100];
            int readLen = 0;
            int leftToBeRead = 100;
            int offset = 0;
            while ((readLen = in.read(buffer, offset, leftToBeRead)) != -1) {
                offset += readLen;
                leftToBeRead -= readLen;
            }
            doSomethingWithReadData(buffer, offset);
        } finally {
            if (in != null)
                in.close();
        }
    }

    private static void doSomethingWithReadData(byte[] buffer, int offset) {

    }
}
