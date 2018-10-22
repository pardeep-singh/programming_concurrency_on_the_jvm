package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.TransactionFactory;
import akka.stm.TransactionFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Portfolio {
    final private Ref<Integer> checkingBalance = new Ref<Integer>(500);
    final private Ref<Integer> savingsBalance = new Ref<Integer>(600);

    public int getCheckingBalance() { return checkingBalance.get(); }
    public int getSavingsBalance() { return savingsBalance.get(); }

    public void withDraw(final boolean fromChecking,
                         final int amount) {

        TransactionFactory factory =
                new TransactionFactoryBuilder()
                .setWriteSkew(false)
                .setTrackReads(true)
                .build();

        new Atomic<Object>(factory) {
            public Object atomically() {
                final int totalBalance =
                        checkingBalance.get() + savingsBalance.get();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}

                if (totalBalance - amount >= 1000) {
                    if(fromChecking)
                        checkingBalance.swap(checkingBalance.get() - amount);
                    else
                        savingsBalance.swap(savingsBalance.get() - amount);
                } else {
                    System.out.println(
                            "Sorry! can't withdraw due to constraint violation"
                    );
                }
                return null;
            }
        }.execute();
    }

    public static void main(String[] args)
            throws Exception {
        final Portfolio portfolio = new Portfolio();

        int checkingBalance = portfolio.getCheckingBalance();
        int savingsBalance = portfolio.getSavingsBalance();

        System.out.println("Checking balance is " + checkingBalance);
        System.out.println("Savings balance is " + savingsBalance);
        System.out.println("Total balance is " +
                (checkingBalance + savingsBalance));

        final ExecutorService service = Executors.newFixedThreadPool(10);
        service.execute(new Runnable() {
            @Override
            public void run() {
                portfolio.withDraw(true, 100);
            }
        });

        service.execute(new Runnable() {
            @Override
            public void run() {
                portfolio.withDraw(false, 100);
            }
        });

        service.shutdown();

        Thread.sleep(4000);

        checkingBalance = portfolio.getCheckingBalance();
        savingsBalance = portfolio.getSavingsBalance();

        System.out.println("Checking balance is " + checkingBalance);
        System.out.println("Savings balance is " + savingsBalance);
        System.out.println("Total balance is " +
                (checkingBalance + savingsBalance));

        if (checkingBalance + savingsBalance < 1000)
            System.out.println("Oops, broke the constraint");
    }

}
