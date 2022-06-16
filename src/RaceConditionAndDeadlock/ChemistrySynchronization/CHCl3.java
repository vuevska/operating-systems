package RaceConditionAndDeadlock.ChemistrySynchronization;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class CHCl3 {

    static Semaphore c;
    static Semaphore h;
    static Semaphore cl;

    static Semaphore hHere;
    static Semaphore clHere;

    static Semaphore ready;
    static Semaphore finished;
    static Semaphore canLeave;

    public static void init() {
        c = new Semaphore(1);
        h = new Semaphore(1);
        cl = new Semaphore(3);

        hHere = new Semaphore(0);
        clHere = new Semaphore(0);
        ready = new Semaphore(0);
        finished = new Semaphore(0);
        canLeave = new Semaphore(0);
    }


    public static class C extends Thread {
        public void bond() {
            System.out.println("C is bonding.");
        }

        public void validate() {
            System.out.println("Molecule is created.");
        }

        public void execute() throws InterruptedException {
            c.acquire();
            hHere.acquire();
            clHere.acquire(3);
            ready.release(4);
            bond();
            finished.acquire(4);
            validate();
            canLeave.release(4);
            c.release();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class H extends Thread {
        public void bond() {
            System.out.println("H is bonding.");
        }

        public void execute() throws InterruptedException {
            h.acquire();
            hHere.release();
            ready.acquire();
            bond();
            finished.release();
            canLeave.acquire();
            h.release();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class  Cl extends Thread {
        public void bond() {
            System.out.println("Cl is bonding.");
        }


        public void execute() throws InterruptedException {
            cl.acquire();
            clHere.release();
            ready.acquire();
            bond();
            finished.release();
            canLeave.acquire();
            cl.release();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HashSet<Thread> threads = new HashSet<>();
        for (int i = 0; i < 10; i ++) {
            threads.add(new C());
            threads.add(new H());
            threads.add(new Cl());
            threads.add(new Cl());
            threads.add(new Cl());
        }

        init();

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join(100);
        }

        for (Thread t : threads) {
            if (t.isAlive()) {
                t.interrupt();
                System.out.println("Possible deadlock.");
                return;
            }
        }
        System.out.println("Main finished.");
    }
}
