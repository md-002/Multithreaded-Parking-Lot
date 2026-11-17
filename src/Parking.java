import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Parking {
    private final int LEVELS = 3;
    private final int SPOTS_PER_LEVEL = 5;
    private final int TOTAL_CAPACITY = LEVELS * SPOTS_PER_LEVEL;

    private final Semaphore semaphore;
    private final AtomicInteger[] spotsOccupiedPerLevel;
    private final AtomicInteger totalOccupied;
    private final Object displayLock;
    private final AtomicInteger totalCarsServed;
    private final long simulationStartTime;

    public Parking() {
        this.semaphore = new Semaphore(TOTAL_CAPACITY, true);

        this.spotsOccupiedPerLevel = new AtomicInteger[LEVELS];
        for (int i = 0; i < LEVELS; i++) {
            this.spotsOccupiedPerLevel[i] = new AtomicInteger(0);
        }

        this.totalOccupied = new AtomicInteger(0);
        this.displayLock = new Object();
        this.totalCarsServed = new AtomicInteger(0);
        this.simulationStartTime = System.currentTimeMillis();
    }

    public int enter(String carNumber) throws InterruptedException {
        System.out.println(carNumber + " is waiting for a spot...");

        semaphore.acquire();

        int level = findFreeLevel();

        synchronized (displayLock) {
            spotsOccupiedPerLevel[level].incrementAndGet();
            totalOccupied.incrementAndGet();
            totalCarsServed.incrementAndGet();

            System.out.println("[+] " + carNumber + " parked on level " + (level + 1));
            displayStatus();
        }

        return level;
    }

    public void exit(String carNumber, int level) {
        synchronized (displayLock) {
            spotsOccupiedPerLevel[level].decrementAndGet();
            totalOccupied.decrementAndGet();

            semaphore.release();

            System.out.println("[-] " + carNumber + " left from level " + (level + 1));
            displayStatus();
        }
    }

    private int findFreeLevel() {
        for (int i = 0; i < LEVELS; i++) {
            if (spotsOccupiedPerLevel[i].get() < SPOTS_PER_LEVEL) {
                return i;
            }
        }
        return 0;
    }

    private void displayStatus() {
        System.out.println("=== PARKING STATUS ===");
        for (int i = 0; i < LEVELS; i++) {
            int occupied = spotsOccupiedPerLevel[i].get();
            System.out.print("Level " + (i + 1) + ": [");
            for (int j = 0; j < SPOTS_PER_LEVEL; j++) {
                if (j < occupied) {
                    System.out.print("X");
                } else {
                    System.out.print("_");
                }
            }
            System.out.println("] (" + occupied + "/" + SPOTS_PER_LEVEL + ")");
        }
        System.out.println("Occupied spots: " + totalOccupied.get() + "/" + TOTAL_CAPACITY);
        System.out.println("========================\n");
    }

    public void displayStatistics() {
        long simulationTime = System.currentTimeMillis() - simulationStartTime;
        System.out.println("=== FINAL STATISTICS ===");
        System.out.printf("Total simulation time: %.3f seconds\n", simulationTime / 1000.0);
        System.out.println("Total cars served: " + totalCarsServed.get());
        System.out.println("Available spots: " + semaphore.availablePermits() + "/" + TOTAL_CAPACITY);
    }
}
