import java.util.Random;

public class Car implements Runnable {
    private final String number;
    private final Parking parking;
    private final Random random;

    public Car(String number, Parking parking) {
        this.number = number;
        this.parking = parking;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(3000));

            int level = parking.enter(number);

            int parkingTime = 2000 + random.nextInt(3000);
            Thread.sleep(parkingTime);

            parking.exit(number, level);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(number + " execution was interrupted.");
        }
    }
}
