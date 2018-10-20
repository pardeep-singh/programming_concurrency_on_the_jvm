package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.TransactionFactory;
import akka.stm.TransactionFactoryBuilder;
import akka.util.DurationInt;

import java.util.Timer;
import java.util.TimerTask;

import static akka.stm.StmUtils.retry;

public class CoffeePot {
    private static final Ref<Integer> cups = new Ref<Integer>(24);
    private static final long start = System.nanoTime();

    public static int readWriteCups(final boolean write) {
        final TransactionFactory factory =
                new TransactionFactoryBuilder()
                        .setReadonly(true)
                        .build();
        return new Atomic<Integer>(factory){
            public Integer atomically() {

                if (write)
                    // this will since its a read Only transaction
                    cups.swap(20);
                return cups.get();
            }
        }.execute();
    }

    private static void fillCup(final int numberOfCups) {
        final TransactionFactory factory =
                new TransactionFactoryBuilder()
                .setBlockingAllowed(true)
                .setTimeout(new DurationInt(6).seconds())
                .build();

        new Atomic<Object>(factory) {
            public Object atomically() {
                if (cups.get() < numberOfCups) {
                    System.out.println("retry............ at " +
                            (System.nanoTime() - start) / 1.0e9);
                    retry();
                }
                cups.swap(cups.get() - numberOfCups);
                System.out.println("Filled up....." + numberOfCups);
                System.out.println("....... at" +
                        (System.nanoTime() - start) / 1.0e9);
                return null;
            }
        }.execute();
    }

    public static void main(String[] args) {
        System.out.println("Read Only");
        readWriteCups(false);
        System.out.println("Attempt to write");
        try {
            readWriteCups(true);
        } catch (Exception ex) {
            System.out.println("Failed " + ex);
        }

        final Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Refilling..... at " +
                        (System.nanoTime() - start) / 1.0e9);
                cups.swap(24);
            }
        }, 5000);

        fillCup(20);
        fillCup(10);

        try {
            fillCup(22);
        } catch (Exception ex) {
            System.out.println("Failed " + ex.getMessage());
        }
    }
}
