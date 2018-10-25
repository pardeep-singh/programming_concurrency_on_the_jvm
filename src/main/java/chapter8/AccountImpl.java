package chapter8;

import akka.actor.TypedActor;
import akka.stm.Ref;

public class AccountImpl extends TypedActor implements CoordinatedAccount {
    private final Ref<Integer> balance = new Ref<Integer>(0);

    public int getBalance() { return balance.get(); }

    public void deposit(final int amount) {
        if (amount > 0) {
            balance.swap(balance.get() + amount);
            System.out.println("Received deposit request " + amount);
        }
    }

    public void withdraw(final int amount) {
        System.out.println("Received withdraw request " + amount);
        if (amount > 0 && balance.get() >= amount)
            balance.swap(balance.get() - amount);
        else {
            System.out.println(".... insufficient funds ....");
            throw new RuntimeException("Insufficient fund");
        }
    }
}
