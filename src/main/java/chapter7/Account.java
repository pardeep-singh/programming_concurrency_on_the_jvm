package chapter7;

import clojure.lang.LockingTransaction;
import clojure.lang.Ref;

import java.util.concurrent.Callable;

public class Account {
    final private Ref balance;

    public Account(final int balance)
        throws Exception {
        this.balance = new Ref(balance);
    }

    public int getBalance() { return (Integer) balance.deref(); }

    public void deposit(final int amount)
        throws Exception {
        LockingTransaction.runInTransaction(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (amount > 0){
                    final int currentBalance = (Integer) balance.deref();
                    balance.set(currentBalance + amount);
                    System.out.println("Deposit " + amount + " ... will it stay");
                    return true;
                } else {
                    throw new RuntimeException("Operation Invalid");
                }
            }
        });
    }

    public void withdraw(final int amount)
        throws Exception {
        LockingTransaction.runInTransaction(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final int currentBalance = (Integer) balance.deref();
                if (amount > 0 && currentBalance >= amount) {
                    balance.set(currentBalance - amount);
                    return true;
                } else {
                    throw new RuntimeException("Operation Invalid");
                }
            }
        });
    }
}
