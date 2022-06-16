package RaceConditionAndDeadlock.Synchronization;

/**
 Потребно е да направите систем за синхронизација на турнир во кошарка, кој се одржува според следните правила:
  На турнирот учествуваат 100 кошаркари, кои произволно се групираат во тимови. Во салата истовремено може да влезат најмногу 20 играчи.
  По влегувањето во салата, секој кошаркар треба да испечати Player inside.. Потоа кошаркарите треба да се пресоблечат за што имаат
  на располагање кабина со капацитет 10, односно може да се пресоблекуваат 10 играчи во исто време. При влегувањето во соблекувалната,
  треба да се испечати In dressing room.. По пресоблекувањето, играчите се чекаат меѓусебно. Откако сите ќе завршат со пресоблекувањето,
  започнуваат со натпреварот, при што сите печатат Game started.. Откако ќе заврши натпреварот, сите печатат Player done.,
  а последниот го повикува печати Game finished., со што означува дека салата е слободна. Потоа, во салата може да влезат
  нови 20 играчи и да започне нов натпревар.
  Во почетниот код кој е даден, дефинирани се класите BasketballTournament и Player.
  Во main методот од класата BasketballTournament потребно е да стартувате 100 играчи,
  кои се репрезентирани преку класата Player. Потоа секој од играчите треба да започне да го извршува
  претходно дефинираното сценарио во позадина. Однесувањето на играчите треба да го дефинрате во execute
  методот од Player класата, кој треба да се извршува паралелно кај сите играчи. По стартувањето на сите играчи,
  во main треба да се чека секој од играчите да заврши за 5 секунди (5000 ms). Доколку некој од играчите не
  заврши за 5 секунди, треба да се испечати Possible deadlock! и да се терминира, а доколку сите играчи завршиле во
  предвиденото време, да се испечати Tournament finished..
  Вашата задача е да го дополните дадениот код според барањата на задачата, при што треба да внимавате не настане Race Condition и Deadlock.
  */

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class BasketballTournament {

    public static void main(String[] args) throws InterruptedException {
        HashSet<Player> threads = new HashSet<>();
        for (int i = 0; i < 60; i++) {
            Player p = new Player();
            threads.add(p);
        }
        // run all threads in background
        for (Thread thread : threads) {
            thread.start();
        }

        // after all of them are started, wait each of them to finish for maximum 5_000 ms
        for (Thread thread : threads) {
            thread.join(5_000);
        }

        // for each thread, terminate it if it is not finished
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("Possible deadlock!");
                return;
            }
        }

        System.out.println("Tournament finished.");
    }
}

class Player extends Thread {
    public static Semaphore players = new Semaphore(20);
    public static Semaphore dressingRoom = new Semaphore(10);

    public static Semaphore canPlay = new Semaphore(0);
    public static Semaphore canFinish = new Semaphore(0);

    public static int inGame = 0;
    public static int inDressingRoom = 0;
    public static int isDone = 0;

    public static Semaphore lock = new Semaphore(1);

    public void execute() throws InterruptedException {
        players.acquire();
        // at most 20 players should print this in parallel
        System.out.println("Player inside.");
        Thread.sleep(100);

        dressingRoom.acquire();
        // at most 10 players may enter the dressing room in parallel
        System.out.println("In dressing room.");
        Thread.sleep(100);// this represents the dressing time
        lock.acquire();
        inDressingRoom++;
        if (inDressingRoom == 10) {
            inDressingRoom = 0;
            canPlay.release(10);
        }
        lock.release();
        dressingRoom.release();


        canPlay.acquire();
        // after all players are ready, they should start with the game together
        System.out.println("Game started.");
        Thread.sleep(100); // this represents the game duration
        lock.acquire();
        inGame++;
        if (inGame == 20) {
            inGame = 0;
            canFinish.release(20);
        }
        lock.release();

        canFinish.acquire();
        System.out.println("Player done.");
        Thread.sleep(100);
        lock.acquire();
        isDone++;
        if (isDone == 20) {
            isDone = 0;
            // only one player should print the next line, representing that the game has finished
            System.out.println("Game finished.");
            players.release(20);
        }
        lock.release();
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
