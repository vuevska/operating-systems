package RaceConditionAndDeadlock.ChemistrySynchronization;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

/**
 * Во една фабрика потребно е производство на алуминиум хидроксид - Al(OH)3
 * Во процесот на производство, прво паралелно се формираат трите OH групи
 * (секоја составена од по еден атом на кислород (O) и водород (H), за потоа
 * да се поврзат со атомот на алуминиум (Al). Имате бесконечна количина од кислород, водород и алуминиум.
 * Молекулите на Al(OH)3 се формираат една по една.
 * Потребно е да го синхронизирате креирањето на алуминиум хидроксид (Al(OH)3) со користење на следните функции:
 * state.bondOH() - Кажува дека може да се формира OH група
 * Се повиква само кај кислородните и водородните атоми.
 * Треба да се повика истовремено од сите кислородни и водородни атоми кои учествуваат во процесот
 * на формирање на една молекула Al(OH)3, за да се формираат нејзините OH групи.
 * Треба трите OH групи од молекулата да се креираат паралелно (во спротивен случај ќе добиете порака за грешка).
 * При повикот, водородните атоми треба да се сигурни дека има присутен кислороден атом и обратно.
 * Доколку методот истовремено го повикаат повеќе од три пара кислородни и водородни атоми, ќе добиете порака за грешка.
 * state.bondAlOH() - Кажува дека може да се формира Al(OH)3 молекулата
 * Треба да се повика од сите атоми кои учествуваат во процесот на креирање на молекулата.
 * Доколку претходно не се формирани трите OH групи, ќе добиете порака за грешка.
 * Доколку методот е повикан од повеќе од 3 кислородни, 3 водородни и 1 алуминиумов атом, ќе добиете порака за грешка.
 * state.validate() - Проверува дали молекулата е формирана успешно
 * Се повикува само од еден атом по креирањето на молекулата
 * вие одлучете од кој атом (отстранете го овој повик од execute() методот на другите две класи).
 * Доколку не се присутни три OH групи и атом на алуминиум во процесот на спојување на
 * молекулата (state.bondAlOH()), ќе добиете порака за грешка.
 * За решавање на задачата, преземете го проектот со клик на копчето Starter file, отпакувајте
 * го и отворете го со Eclipse или Netbeans.
 * Вашата задача е да ги имплементирате методите execute() од класите Oxygen, Hydrogen и Aluminium,
 * кои се наоѓаат во фајлот Al_OH_3.java. При решавањето можете да користите семафори и
 * монитори по ваша желба и нивната иницијализација треба да ја направите во init() методот.
 * При стартувањето на класата, сценариото ќе се повика 10 пати, со креирање на голем број инстанци
 * од класите Oxygen, Hydrogen и Aluminium и паралелно само еднаш ќе се повика нивниот execute() метод.
 * Решението треба да се прикачи според инструкциите подолу.
 */

class Al_OH_3 {

    public static Semaphore al;
    public static Semaphore h;
    public static Semaphore o;

    public static Semaphore oHere;
    public static Semaphore ohHere;

    public static Semaphore readyOH;
    public static Semaphore readyAl;

    public static Semaphore doneAlOH;
    public static Semaphore canLeave;


    public static void init() {
        al = new Semaphore(1);
        h = new Semaphore(3);
        o = new Semaphore(3);
        oHere = new Semaphore(0);
        ohHere = new Semaphore(0);
        readyOH = new Semaphore(0);
        readyAl = new Semaphore(0);
        doneAlOH = new Semaphore(0);
        canLeave = new Semaphore(0);
    }

    public static class Hydrogen extends TemplateThread {

        public Hydrogen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            h.acquire();
            oHere.acquire();
            readyOH.release();
            state.bondOH();
            ohHere.release();
            readyAl.acquire();
            state.bondAlOH3();
            doneAlOH.release();
            canLeave.acquire();
            h.release();
        }

    }

    public static class Oxygen extends TemplateThread {

        public Oxygen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            o.acquire();
            oHere.release();
            readyOH.acquire();
            state.bondOH();
            ohHere.release();
            readyAl.acquire();
            state.bondAlOH3();
            doneAlOH.release();
            canLeave.acquire();
            o.release();
        }

    }

    public static class Aluminium extends TemplateThread {

        public Aluminium(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            al.acquire();
            ohHere.acquire(6);
            readyAl.release(6);
            state.bondAlOH3();
            doneAlOH.acquire(6);
            state.validate();
            canLeave.release(6);
            al.release();
        }

    }

    static AluminiumHydroxideState state = new AluminiumHydroxideState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 300;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                Oxygen o = new Oxygen(numRuns);
                Hydrogen h = new Hydrogen(numRuns);
                threads.add(o);
                if (i % 3 == 0) {
                    Aluminium al = new Aluminium(numRuns);
                    threads.add(al);
                }
                threads.add(h);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

class AluminiumHydroxideState extends AbstractState {

    private static final String DONE_SHOULD_CALLED_ONCE = "The validate() method should be called only once per molecule.";
    private static final String OH_BONDING_NOT_PARALLEL = "The OH bonding is not in parallel!";
    private static final String MOLECULE_NOT_BOUNDED_COMPLITELY = "The previous molecule is not bonded completely.";
    private static final String OH_3_GROUP_IS_NOT_PRESENT = "(OH)3 group is not present.";
    private static final String MAXIMUM_3_OXYGEN = "Maximum 3 Oxygen atoms for bonding are allowed.";
    private static final String MAXIMUM_3_HYDROGEN = "Maximum 3 TribeMember atoms for bonding are allowed.";
    private static final String MAXIMUM_1_ALUMINIUM = "Maximum 1 Aluminium atom for bonding is allowed.";
    private static final int MAXIMUM_1_ALUMINIUM_POINTS = 5;
    private static final int MAXIMUM_3_HYDROGEN_POINTS = 5;
    private static final int MAXIMUM_3_OXYGEN_POINTS = 5;
    private static final int OH_3_GROUP_IS_NOT_PRESENT_PONTS = 5;
    private static final int MOLECULE_NOT_BOUNDED_COMPLITELY_POINTS = 10;
    private static final int OH_BONDING_NOT_PARALLEL_POINTS = 5;
    private static final int DONE_SHOULD_CALLED_ONCE_POINTS = 5;

    int numAtoms = 0;
    private BoundCounterWithRaceConditionCheck O;
    private BoundCounterWithRaceConditionCheck H;
    private BoundCounterWithRaceConditionCheck Al;

    public AluminiumHydroxideState() {
        O = new BoundCounterWithRaceConditionCheck(0, 3,
                MAXIMUM_3_OXYGEN_POINTS, MAXIMUM_3_OXYGEN, null, 0, null);
        H = new BoundCounterWithRaceConditionCheck(0, 3,
                MAXIMUM_3_HYDROGEN_POINTS, MAXIMUM_3_HYDROGEN, null, 0, null);
        Al = new BoundCounterWithRaceConditionCheck(0, 1,
                MAXIMUM_1_ALUMINIUM_POINTS, MAXIMUM_1_ALUMINIUM, null, 0, null);
    }

    public void bondOH() {

        Switcher.forceSwitch(3);
        if (getThread() instanceof Al_OH_3.Oxygen) {
            log(O.incrementWithMax(false), "Oxygen for OH group");
        } else if (getThread() instanceof Al_OH_3.Hydrogen) {
            log(H.incrementWithMax(false), "TribeMember for OH group");
        }
    }

    public void bondAlOH3() {
        synchronized (this) {
            // first check
            if (numAtoms == 0) {
                if (O.getValue() == 3 && H.getValue() == 3) {
                    O.setValue(0);
                    H.setValue(0);
                } else {
                    log(new PointsException(OH_3_GROUP_IS_NOT_PRESENT_PONTS,
                            OH_3_GROUP_IS_NOT_PRESENT), null);
                }
            }
            numAtoms++;
        }
        Switcher.forceSwitch(3);
        if (getThread() instanceof Al_OH_3.Oxygen) {
            log(O.incrementWithMax(false), "Oxygen for Al(OH)3");
        } else if (getThread() instanceof Al_OH_3.Hydrogen) {
            log(H.incrementWithMax(false), "TribeMember for Al(OH)3");
        } else {
            log(Al.incrementWithMax(false), "Aluminium for Al(OH)3");
        }
    }

    public void validate() {
        synchronized (this) {
            if (numAtoms == 7) {
                reset();
                log(null, "Al(OH)3 molecule is formed.");
            } else if (numAtoms != 0) {
                log(new PointsException(MOLECULE_NOT_BOUNDED_COMPLITELY_POINTS,
                        MOLECULE_NOT_BOUNDED_COMPLITELY), null);
                reset();
            } else {
                log(new PointsException(DONE_SHOULD_CALLED_ONCE_POINTS,
                        DONE_SHOULD_CALLED_ONCE), null);
            }
        }
    }

    private synchronized void reset() {
        O.setValue(0);
        H.setValue(0);
        Al.setValue(0);
        numAtoms = 0;
    }

    @Override
    public synchronized void finalize() {
        if (O.getMax() == 1 && H.getMax() == 1) {
            logException(new PointsException(OH_BONDING_NOT_PARALLEL_POINTS,
                    OH_BONDING_NOT_PARALLEL));
        }
    }

}