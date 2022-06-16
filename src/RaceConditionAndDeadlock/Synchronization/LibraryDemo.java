package RaceConditionAndDeadlock.Synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class LibraryDemo {
    public static void main(String[] args) throws InterruptedException {
        List<Member> members = new ArrayList<>();
        SemaphoreLibrary library = new SemaphoreLibrary(10);
        for (int i = 0; i < 10; i++) {
            Member member = new Member("Member" + i, library);
            members.add(member);
        }

        for (Member member : members) {
            member.start();
        }

        for (Member member : members) {
            member.join();
        }

        System.out.println("Successful program.");
    }
}

class Member extends Thread {
    private final String name;
    private final SemaphoreLibrary library;

    public Member(String name, SemaphoreLibrary library) {
        this.name = name;
        this.library = library;
    }

    @Override
    public void run() {

        for (int i = 0; i < 3; i++) {
            System.out.println("Member " + i + " return book");
            try {
                library.returnBook("Book" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 2; i++) {
            System.out.println("Member " + i + " borrows book");
            try {
                library.borrowBook();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class SemaphoreLibrary {

    List<String> books = new ArrayList<>();
    int capacity;
    Semaphore coordinator = new Semaphore(1);
    Semaphore returnBookSemaphore = new Semaphore(10);
    Semaphore borrowBookSemaphore = new Semaphore(10);

    public SemaphoreLibrary(int capacity) {
        this.capacity = capacity;
    }

    public void returnBook(String book) throws InterruptedException {
        returnBookSemaphore.acquire();
        coordinator.acquire();
        while (books.size() == capacity) {
            coordinator.release();
            Thread.sleep(1000);
            coordinator.acquire();
        }
        books.add(book);
        coordinator.release();
        borrowBookSemaphore.release();
    }

    public String borrowBook() throws InterruptedException {
        borrowBookSemaphore.acquire();
        String book = "";
        coordinator.acquire();
        while (books.size() == 0) {
            coordinator.release();
            Thread.sleep(1000);
            coordinator.acquire();
        }
        book = books.remove(0);
        returnBookSemaphore.release();
        return book;
    }
}