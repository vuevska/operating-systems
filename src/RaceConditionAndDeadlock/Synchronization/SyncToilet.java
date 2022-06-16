package RaceConditionAndDeadlock.Synchronization;

import java.util.concurrent.Semaphore;

public class SyncToilet {
    public static class Toilet {

        public void vlezi() {
            System.out.println("Vleguva...");
        }

        public void izlezi() {
            System.out.println("Izleguva...");
        }
    }

    public static Semaphore accessToilet;
    public static Semaphore wLock;
    public static Semaphore mLock;
    public static int numW;
    public static int numM;

    public static void init() {
        accessToilet = new Semaphore(1);
        wLock = new Semaphore(1);
        mLock = new Semaphore(1);
        numM = 0;
        numW = 0;
    }

    public static class Man extends Thread {
        private Toilet toilet;

        public Man(Toilet toilet) {
            this.toilet = toilet;
        }

        public void enter() throws InterruptedException {
            mLock.acquire();
            if(numM == 0) {
                accessToilet.acquire();
            }
            numM++;
            this.toilet.vlezi();
            mLock.release();
        }

        public void exit() throws InterruptedException {
            mLock.acquire();
            numM--;
            this.toilet.izlezi();
            if(numM == 0) {
                accessToilet.release();
            }
            mLock.release();
        }

        @Override
        public void run() {
            super.run();
        }
    }

    public static class Woman extends Thread {
        private Toilet toilet;

        public Woman(Toilet toilet) {
            this.toilet = toilet;
        }

        public void enter() throws InterruptedException {
            wLock.acquire();
            if(numW == 0) {
                accessToilet.acquire();
            }
            numW++;
            this.toilet.vlezi();
            wLock.release();
        }

        public void exit() throws InterruptedException {
            wLock.acquire();
            numW--;
            this.toilet.izlezi();
            if(numW == 0) {
                accessToilet.release();
            }
            wLock.release();
        }

        @Override
        public void run() {
            super.run();
        }
    }
}
