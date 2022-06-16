package JavaMultithreading;

/**
 * Со помош на синхронизациските методи да се реши проблемот за определување на бројот на појавувања на
 * буквата E во  стрингот и негово запишување во глобална променлива count.
 * Секвенцијалното решение не е прифатливо поради тоа што трае многу долго време (поради големината на стрингот).
 * За таа цел, потребно е да се паралелизира овој процес, при што треба да се напише метода која ќе ги брои
 * појавувањата на буквата E во помал фрагмент од стрингот, при што резултатот повторно се чува во глобалната заедничка променлива count.
 * Напомена: Почетниот код е даден во почетниот код CountLetter. Задачата да се тестира над стринг од минимум 1000 карактери.
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.concurrent.Semaphore;


public class CountLetter {

    //TODO: Променлива која треба да го содржи бројот на појавувања на буквата E
    static int count = 0;

    //TODO: дефинирање на потребните елементи за синхронизација
    static Semaphore lock;

    public void init() {
        lock = new Semaphore(1);
    }

    public static int getCount() {
        return count;
    }

    static class Counter extends Thread {

        public void count(String data) throws InterruptedException {
            //TODO: да се имплементира
            if (data.equals("E")) {
                lock.acquire();
                count++;
                lock.release();
            }
        }

        private final String data;

        public Counter(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                count(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            CountLetter environment = new CountLetter();
            environment.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void start() throws Exception {

        init();

        HashSet<Thread> threads = new HashSet<>();
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        String pom = bf.readLine();
        String[] data = pom.split("");

        for (String d : data) {
            Counter c = new Counter(d);
            threads.add(c);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println(getCount());
    }
}
