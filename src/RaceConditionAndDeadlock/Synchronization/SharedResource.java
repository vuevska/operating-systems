package RaceConditionAndDeadlock.Synchronization;

import java.util.ArrayList;
import java.util.List;

public class SharedResource {
    private int value = 0;
    public SharedResource() {
    }

    public synchronized void increment() {
        this.value++;
    }

    public synchronized void multiply() {
        this.value = this.value * 2;
    }

    public synchronized void print() {
        System.out.println(value);
    }


    public static class CustomThread extends Thread {
        public SharedResource resource;

        public CustomThread(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for(int i = 0; i < 10; i++) {
                this.resource.increment();
            }
        }
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        SharedResource sharedResource = new SharedResource();
        List<SharedResource.CustomThread> customThreads = new ArrayList<>();
        int numThreads = 5;
        for (int i = 0; i < numThreads; i++) {
            customThreads.add(new SharedResource.CustomThread(sharedResource));
        }
        for (SharedResource.CustomThread t : customThreads) {
            t.start();
        }

        for (SharedResource.CustomThread t : customThreads) {
            t.join();
        }
        sharedResource.print();
    }
}
