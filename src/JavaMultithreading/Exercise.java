package JavaMultithreading;

/**
* Во дадениот код изберете соодветна опција за стартување на нова нишка која
 * ќе биде во блокирана состојба за 1 час - Thread.sleep(ms). Изберете соодветни
 * опции за насилно прекинување на нишката и чекање да заврши.
* */

public class Exercise {
    public static void main(String[] args) throws InterruptedException {
        ThreadExample.example();
    }
}
class ThreadExample {
    public static void example() throws InterruptedException {
        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Block thread for an hour");
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException ie) {
                    System.out.println("Thread is interrupted : " + Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                    System.out.println("The thread is up");
                }
            }
        });
        t1.start();
        t1.interrupt();
        t1.join();
    }
}
