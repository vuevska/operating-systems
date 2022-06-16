package RaceConditionAndDeadlock.Synchronization;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProtectingClassFields {
    public Lock lock = new ReentrantLock();
    public Semaphore binarySemaphore = new Semaphore(1);

    public void publicFieldSafeIncrement() {
        synchronized (this) {
            publicFieldSafeIncrement();
        }

        // or

        lock.lock();
        publicFieldSafeIncrement();
        lock.unlock();

        // or

        try {
            binarySemaphore.acquire();
            publicFieldSafeIncrement();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            binarySemaphore.release();
        }
    }
}
