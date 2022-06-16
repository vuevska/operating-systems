package JavaMultithreading;

public class ThreadBasicTest {
    public static void main(String[] args) {
        Thread ta = new ThreadA1();
        Thread tb = new ThreadB1();
        ta.start();
        tb.start();
        System.out.println("Main done");
    }
}

class ThreadA1 extends Thread {
    @Override
    public void run() {
        for (int i = 1; i <= 20 ; i++) {
            System.out.println("A: " + i);
        }
        System.out.println("A done");
    }
}

class ThreadB1 extends Thread {
    @Override
    public void run() {
        for (int i = -1 ; i >= -20 ; i--) {
            System.out.println("\t\tB: " + i);
        }
        System.out.println("B done");
    }
}