package JavaInputOutput;

import java.io.File;
import java.io.FilenameFilter;

// Listing the directory content with or without a filter

public class DirList {

    public static void main(String[] args) {

        File path = new File(".");
        String[] list;
        if (args.length == 0) {
            list = path.list();
        } else {
            list = path.list(new DirFilter(args[0]));
        }
        assert list != null;
        for (String s : list) {
            System.out.println(s);
        }

    }
}

class DirFilter implements FilenameFilter {
    String afn;

    DirFilter(String afn) {
        this.afn = afn;
    }
    public boolean accept (File dir, String name) {
        String f = new File(name).getName();
        return f.contains(afn);
    }
}