package RaceConditionAndDeadlock.ChemistrySynchronization;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

/**
 * Во една фабрика потребно е производство на оцет - C2H4O2.
 * Во процесот на производство треба да се присутни 2 јаглеродни (C) атоми, 4 водородни (H) и 2 кислородни (О) атоми.
 * Молекулите на C2H4O2 се формираат една по една.
 * Секој од атомите е претставен преку соодветна класа, во која execute() методот треба да се извршува во позадина.
 * Во execute методот, треба да овозможите да се извршуваат паралелно максимум 2 јаглеродни (C) атоми, 4 водородни (H)
 * и 2 кислородни (О) атоми. По влегувањето на секој од атомите треба да се испечати порака дека е присутен. Потоа,
 * атомите треба да чекаат додека сите потребни атоми за молекулата пристигнат, по што се печати Molecule bonding.
 * од страна на сите атоми. Откако ќе заврши спојувањето, секој од методите печати дека е завршен.
 * На крајот треба само еден атом да испечати Molecule created. и да овозможи креирање на нова молекула.
 * Вашата задача е во main методот да стартувате 20 јаглеродни, 40 водородни и 20 кислородни атоми,
 * кои ќе се извршуваат во позадина. Потоа треба да почекате 2 секунди за да завршат сите. Они кои
 * не завршиле, треба да ги прекинете и да испечатите Possible deadlock!. Ако сите завршиле без да ги прекинете, испечатете Process finished..
 * Вашата задача е да го дополните дадениот код според барањата на задачата, при што треба да внимавате не настане Race Condition и Deadlock.
 */


public class C2H4O2 {

    public static Semaphore c = new Semaphore(2);
    public static Semaphore h = new Semaphore(4);
    public static Semaphore o = new Semaphore(2);

    public static Semaphore cHere = new Semaphore(0);
    public static Semaphore hHere = new Semaphore(0);
    public static Semaphore oHere = new Semaphore(0);

    public static Semaphore ready = new Semaphore(0);
    public static Semaphore done = new Semaphore(0);
    public static Semaphore canLeave = new Semaphore(0);

    public static int cNum = 0;
    public static Semaphore lock = new Semaphore(1);


    public static class C extends Thread {
        public void execute() throws InterruptedException {
            c.acquire();
            System.out.println("C here.");
            lock.acquire();
            cNum++;
            if (cNum == 2) {
                cNum = 0;
                lock.release();
                cHere.acquire();
                hHere.acquire(4);
                oHere.acquire(2);
                ready.release(7);
                System.out.println("Molecule bonding.");
                Thread.sleep(100);
                System.out.println("C done.");
                done.acquire(7);
                System.out.println("Molecule created.");
                canLeave.release(7);
            } else {
                lock.release();
                cHere.release();
                ready.acquire();
                System.out.println("Molecule bonding.");
                Thread.sleep(100);
                done.release();
                canLeave.acquire();
            }
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

        public void execute() throws InterruptedException {
            h.acquire();
            System.out.println("H here.");
            hHere.release();
            ready.acquire();
            System.out.println("Molecule bonding.");
            Thread.sleep(100);
            System.out.println("H done.");
            done.release();
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

    public static class O extends Thread {

        public void execute() throws InterruptedException {
            o.acquire();
            System.out.println("O here.");
            oHere.release();
            ready.acquire();
            System.out.println("Molecule bonding.");
            Thread.sleep(100);
            System.out.println("O done.");
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
        HashSet<Thread> threads = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            threads.add(new C());
            threads.add(new H());
            threads.add(new H());
            threads.add(new O());
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join(2_000);
        }

        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("Possible deadlock!");
                return;
            }
        }

        System.out.println("Main finished.");
    }
}
