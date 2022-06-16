package JavaMultithreading;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Да се имплементира класа FileScanner која што ќе се однесува како thread. Во класата FileScanner
 * се чуваат податоци за : - името на директориумот што треба да се скенира - статичка променлива
 * counter што ќе брои колку нишки од класата FileScanner ќе се креираат Во класата FileScanner
 * да се имплементираа статички методот што ќе печати информации за некоја датотека од следниот формат:
 * dir: lab1 - resenija 100 (dir за директориуми, името на директориумот и број на фајлови)
 * file: spisok.pdf 29198 (file за обични фајлови, име на фајл и големина)
 * Дополнително да се преоптовари методот run() од класата Thread, така што ќе печати информации за директориумот
 * за којшто е повикан. Доколку во директориумот има други под директориуми, да се креира нова нишка од
 * тип FileScanner што ќе ги прави истите работи како и претходно за фајловите/директориумите што се наоѓаат во тие директориуми (рекурзивно).
 * На крај да се испечати вредноста на counter-от, односно колку вкупно нишки биле креирани.  Користете го следниот почетен код.
 */

public class FileScanner extends Thread {

    private final String fileToScan;
    //TODO: Initialize the start value of the counter
    private static Long counter = 0L;

    public FileScanner(String fileToScan) {
        this.fileToScan = fileToScan;
        //TODO: Increment the counter on every creation of FileScanner object
        counter++;
    }

    public static void printInfo(File file) throws FileNotFoundException {

        // TODO: Print the info for the @argument File file, according to the requirement of the task
        StringBuilder builder = new StringBuilder();
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (file.isFile()) {
            builder.append("file: ").append(file.getName()).append(" ").append(file.length());
        } else if (file.isDirectory()) {
            builder.append("dir: ").append(file.getName()).append(" - resenija ").append(file.length());
        }
        System.out.println(builder);
    }

    public static Long getCounter() {
        return counter;
    }

    public void run() {

        //TODO Create object File with the absolute path fileToScan.
        File file = new File(fileToScan);

        //TODO Create a list of all the files that are in the directory file.
        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {

                // TODO If the File f is not a directory, print its info using the function printInfo(f)
                if (file.isFile()) {
                    try {
                        printInfo(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                // TODO If the File f is a directory, create a thread from type FileScanner and start it.
                else if (file.isDirectory()) {
                    try {
                        printInfo(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                //TODO: wait for all the FileScanner-s to finish
                FileScanner fileScanner = new FileScanner(f.getAbsolutePath());
                fileScanner.start();
                try {
                    fileScanner.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        String FILE_TO_SCAN = "src/";

        //TODO Construct a FileScanner object with the fileToScan = FILE_TO_SCAN
        FileScanner fileScanner = new FileScanner(FILE_TO_SCAN);

        //TODO Start the thread from type FileScanner
        fileScanner.start();

        //TODO wait for the fileScanner to finish
        fileScanner.join();

        //TODO print a message that displays the number of thread that were created
        System.out.println("Number of threads created: " + FileScanner.getCounter());
    }
}