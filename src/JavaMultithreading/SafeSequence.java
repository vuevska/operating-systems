package JavaMultithreading;

public class SafeSequence {
    private int value;
    public synchronized int getNext() {
        return value++;
    }

    /*public int getNext() {
        synchronized (this) {
            return value++;
        }
    }*/
}
