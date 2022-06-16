package RaceConditionAndDeadlock.ChemistrySynchronization;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Потребно е да се синхронизира процесот на врзување на молекулата Co2O3.
 * За да се формира молекулата се чека да пристигнат сите потребни атоми, по што
 * започнува процесот на врзување. Има бесконечна количина на атоми од секој тип.
 * Молекулите се сврзуваат една по една.
 * Методот bond() го означува врзувањето на молекулата и треба да се повика од сите
 * атоми кои се дел од молекулата. Откако една молекула е врзана, мозе да се премине
 * на врзување на следна молекула.
 */

public class Co2O3 {

    static Semaphore co;
    static Semaphore o;
    static Semaphore coHere;
    static Semaphore oHere;
    static Semaphore canBond;
    static Semaphore finishedBonding;
    static Semaphore canLeave;

    static Semaphore lock;
    static int coCount;

    public static void init() {
        co = new Semaphore(2);
        o = new Semaphore(3);
        coHere = new Semaphore(0);
        oHere = new Semaphore(0);
        canBond = new Semaphore(0);
        finishedBonding = new Semaphore(0);
        canLeave = new Semaphore(0);
        lock = new Semaphore(1);
        coCount = 0;
    }

    static class CO extends Thread {
        public void bond() {
            System.out.println("CO is bonding...");
        }

        public void execute() throws InterruptedException {
            co.acquire();
            lock.acquire();
            coCount++;
            if (coCount == 2) {
                coCount = 0;
                lock.release();
                coHere.acquire();
                oHere.acquire(3);
                canBond.release(4);
                bond();
                finishedBonding.acquire(4);
                canLeave.release(4);
            } else {
                lock.release();
                coHere.release();
                canBond.acquire();
                bond();
                finishedBonding.release();
                canLeave.acquire();
            }
            co.release();
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

    public static class O extends Thread {
        public void bond() {
            System.out.println("O is bonding...");
        }

        public void execute() throws InterruptedException {
            o.acquire();
            oHere.release();
            canBond.acquire();
            bond();
            finishedBonding.release();
            canLeave.acquire();
            o.release();
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
        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            threadList.add(new CO());
            threadList.add(new CO());
            threadList.add(new O());
            threadList.add(new O());
            threadList.add(new O());
        }

        init();

        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join(1000);
        }
        for (Thread thread : threadList) {
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("Possible deadlock");
                return;
            }
        }
        System.out.println("Main finished.");
    }
}