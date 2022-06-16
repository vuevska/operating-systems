package JavaInputOutput;


import java.io.File;

public class RecursiveDirList {

    public static void listFile(String absolutePath) {
        File file = new File(absolutePath);

        if(file.exists()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File f : subFiles) {
                    System.out.println(getPrefix(f) + getPermissions(f) + "\t" + f.getName());

                    if(f.isDirectory()) {
                        listFile(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static String  getPermissions(File f) {
        return String.format("%s%s%s",
                f.canRead() ? "r" : "-",
                f.canWrite() ? "w" : "-",
                f.canExecute() ? "x" : "-");
    }

    public static String getPrefix(File f) {
        boolean prefix = f.isDirectory();
        return prefix ? "d" : "-";
    }

    public static void main(String[] args) {
        String filePath = ".";
        listFile(filePath);
    }
}
