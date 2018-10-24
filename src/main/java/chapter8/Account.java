package chapter8;

import akka.stm.Ref;
import akka.transactor.UntypedTransactor;

public class Account extends UntypedTransactor {
    private final Ref<Integer> balance = new Ref<Integer>(0);

    public void atomically(final Object message){
        if (message instanceof Deposit) {
            int amount = ((Deposit)(message)).amount;
            if (amount > 0) {
                balance.swap(balance.get() + amount);
                System.out.println("Received deposit request " + amount);
            }
        } else if (message instanceof Withdraw) {
            int amount = ((Withdraw)(message)).amount;
            System.out.println("Received withdraw request " + amount);
            if (amount > 0 && balance.get() >= amount)
                balance.swap(balance.get() - amount);
            else {
                System.out.println("..... insufficient funds .....");
                throw new RuntimeException("Insuffiicient fund");
            }
        } else if (message instanceof FetchBalance) {
            getContext().replySafe(new Balance(balance.get()));
        }
    }
}
