package RaceConditionAndDeadlock.Synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ProducerController {

    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        Producer producer = new Producer(buffer);
        List<Controller> controllers = new ArrayList<>();
        init();
        for (int i = 0; i < 10; i++) {
            controllers.add(new Controller(buffer));
        }
        producer.start();
        for (int i = 0; i < 10; i ++) {
            controllers.get(i).start();
        }
    }

    static Semaphore accessBuffer;
    static Semaphore canCheck;
    static Semaphore lock;
    static int numChecks;

    public static void init() {
        accessBuffer = new Semaphore(1);
        canCheck = new Semaphore(10);
        lock = new Semaphore(1);
        numChecks = 0;
    }

    public static class Buffer {
        public int numChecks = 0;
        public void produce() {
            System.out.println("Producer is producing...");
        }
        public void check() {
            System.out.println("Controller is checking...");
        }
    }

    public static class Producer extends Thread {

        private final Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            accessBuffer.acquire();
            this.buffer.produce();
            accessBuffer.release();
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Controller extends Thread {

        private final Buffer buffer;

        public Controller(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            lock.acquire();
            if (this.buffer.numChecks == 0) {
                accessBuffer.acquire();
            }
            this.buffer.numChecks++;
            lock.release();

            canCheck.acquire();
            this.buffer.check();
            lock.acquire();
            this.buffer.numChecks--;
            if (this.buffer.numChecks == 0) {
                accessBuffer.release();
            }
            lock.release();
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
