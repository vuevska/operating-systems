package JavaInputOutput;

import java.io.File;
import java.io.IOException;

public class FileOperations {

    public static void main(String[] args) throws IOException {

        File f = new File(".");
        boolean exists = f.exists();
        boolean isDirectory = f.isDirectory();
        boolean canRead = f.canRead();
        boolean canWrite = f.canWrite();
        boolean canExecute = f.canExecute();
        String fileName = f.getName();
        String absolutePath = f.getAbsolutePath();
        String parentPath = f.getParent();
        long size = f.length();

        System.out.println("File name: " + fileName);
        System.out.println("Absolute path: " + absolutePath);
        System.out.println("Parent path: " + parentPath);
        System.out.println("Size: " + size);
        System.out.println("Exists: " + exists);
        System.out.println("Is a directory: " + isDirectory);
        System.out.println("Can read: " + canRead);
        System.out.println("Can write: " + canWrite);
        System.out.println("Can execute: " + canExecute);

        File newFile = new File("C:\\Users\\majav\\OneDrive\\SCHOOL\\3. FAKULTET\\CETVRT SEMESTAR\\Operativni Sistemi\\OS\\src\\JavaInputOutput\\test.txt");
        boolean created = newFile.createNewFile();

        // boolean deleted = f.delete();
        // f.mkdir();
        // f.mkdirs();
        // boolean renames = newFile.renameTo(new File("newFilePath"));
        String [] files = f.list();

        System.out.println("File name: " + newFile);
        System.out.println("Absolute path: " + newFile);
        System.out.println(created);

    }
}
