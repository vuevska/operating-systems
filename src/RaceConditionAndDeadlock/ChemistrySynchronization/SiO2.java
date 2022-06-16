package RaceConditionAndDeadlock.ChemistrySynchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
* Во процесот на производство на EPROM меморија, потребен е слој на силициум диоксид (SiO2).
 *  За да се формира оксидниот слој потребно е во ист момент да бидат присутни два атоми на кислород и
 * еден атом на силициум.  Со користење на семафори напишете програма која ќе помогне во процесот
 * на производство на EPROM меморија. Секој од атомите е посебен процес.
 *  Силициумовите атоми (процеси) го извршуваат методот proc_Si(), а кислородните атоми методот proc_O().
 *  Откако ќе „се сретнат“ сите три атоми, секој од нив го повикува виртуелниот метод bond(), за да се формира SiO2
 * Ограничувања:
 *  Доколку до “бариерата” пристигне атом на силициум,
 * истиот мора да чека да се соберат два атоми на кислород.
 *  Доколку пристигне атом на кислород, мора да чека на
 * еден атом на силициум и еден атом на кислород.  Бариерата треба да ја
 * напушти еден атом на силициум и два атоми на кислород, кои формираат молекул на
 * силициум диоксид (SiO2).
 *
* */

public class SiO2 {

    public static Semaphore si;
    public static Semaphore o;

    public static Semaphore oHere;
    public static Semaphore ready;
    public static Semaphore done;
    public static Semaphore canLeave;

    public static void init() {
        si = new Semaphore(1);
        o = new Semaphore(2);

        oHere = new Semaphore(0);
        ready = new Semaphore(0);
        done = new Semaphore(0);
        canLeave = new Semaphore(0);
    }

    public static class Si extends Thread {

        public void bond() {
            System.out.println("Si is bonding now.");
        }

        public void execute() throws InterruptedException {
            si.acquire();
            oHere.acquire(2);
            ready.release(2);
            bond();
            done.acquire(2);
            System.out.println("Molecule created");
            canLeave.release(2);
            si.release();
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
            System.out.println("O is bonding now.");
        }

        public void execute() throws InterruptedException {
            o.acquire();
            oHere.release();
            ready.acquire();
            bond();
            done.release();
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
            threadList.add(new Si());
            threadList.add(new O());
            threadList.add(new O());
        }

        init();

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join(100);
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
