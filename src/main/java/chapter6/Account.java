package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;

public class Account {
    final private Ref<Integer> balance = new Ref<Integer>();

    public Account(int initialBalance) { balance.swap(initialBalance); }

    public int getBalance() { return balance.get(); }

    public void deposit(final int amount) {
        new Atomic<Boolean>() {
            public Boolean atomically() {
                System.out.println("Deposit:" + amount);
                if (amount > 0) {
                    balance.swap(balance.get() + amount);
                    return true;
                }
                throw new AccountOperationFailedException();
            }
        }.execute();
    }

    public void withdram(final int amount) {
        new Atomic<Boolean>() {
            public Boolean atomically() {
                int currentBalance = balance.get();
                if (amount > 0 && currentBalance >= amount) {
                    balance.swap(currentBalance - amount);
                    return true;
                }
                throw new AccountOperationFailedException();
            }
        }.execute();
    }
}
