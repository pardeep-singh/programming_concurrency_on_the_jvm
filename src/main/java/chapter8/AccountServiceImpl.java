package chapter8;

import akka.actor.Actors;
import akka.actor.TypedActor;
import akka.transactor.Atomically;
import static akka.transactor.Coordination.coordinate;

public class AccountServiceImpl extends TypedActor implements CoordinatedAccountService {

    public void transfer(final CoordinatedAccount from,
                         final CoordinatedAccount to,
                         final int amount) {
        coordinate(true, new Atomically() {
           public void atomically() {
               to.deposit(amount);
               from.withdraw(amount);
           }
        });
    }

    public static void main(String[] args) throws InterruptedException{
        final CoordinatedAccount account1 = TypedActor.newInstance(CoordinatedAccount.class, AccountImpl.class);
        final CoordinatedAccount account2 = TypedActor.newInstance(CoordinatedAccount.class, AccountImpl.class);
        final CoordinatedAccountService accountService = TypedActor.newInstance(CoordinatedAccountService.class, AccountServiceImpl.class);

        account1.deposit(1000);
        account2.deposit(1000);

        System.out.println("Account1 balance is " + account1.getBalance());
        System.out.println("Account2 balance is " + account2.getBalance());

        System.out.println("Let's transfer $20.... should succeed");

        accountService.transfer(account1, account2, 20);

        Thread.sleep(1000);

        System.out.println("Account1 balance is " + account1.getBalance());
        System.out.println("Account2 balance is " + account2.getBalance());

        System.out.println("Let's transfer $2000.... should not succeed");

        accountService.transfer(account1, account2, 2000);

        Thread.sleep(6000);

        System.out.println("Account1 balance is " + account1.getBalance());
        System.out.println("Account2 balance is " + account2.getBalance());

        Actors.registry().shutdownAll();
    }
}
