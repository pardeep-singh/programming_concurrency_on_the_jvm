package chapter7;

import clojure.lang.LockingTransaction;

import java.util.concurrent.Callable;

public class Transfer {

    public static void transfer(final Account from,
                                final Account to,
                                final int amount)
        throws Exception {
        LockingTransaction.runInTransaction(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                to.deposit(amount);
                from.withdraw(amount);
                return true;
            }
        });
    }

    public static void transferAndPrint(final Account from,
                                        final Account to,
                                        final int amount) {
        try {
            transfer(from, to, amount);
        } catch (Exception ex) {
            System.out.println("Transfer failed " + ex.getMessage());
        }

        System.out.println("Balance of from account is " + from.getBalance());
        System.out.println("Balance of to account is " + to.getBalance());
    }

    public static void main(String[] args)
            throws Exception {
        final Account account1 = new Account(2000);
        final Account account2 = new Account(100);

        transferAndPrint(account1, account2, 500);
        transferAndPrint(account1, account2, 5000);
    }
}
