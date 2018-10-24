package chapter8;

import akka.actor.ActorRef;
import akka.actor.Actors;

public class UseAccountService {

    public static void printBalance(final String accountName,
                                    final ActorRef account) {
        Balance balance =
                (Balance)(account.sendRequestReply(new FetchBalance()));
        System.out.println(accountName + " balance is " + balance.amount);
    }

    public static void main(String[] args) throws InterruptedException{
        final ActorRef account1 = Actors.actorOf(Account.class).start();
        final ActorRef account2 = Actors.actorOf(Account.class).start();
        final ActorRef accountService = Actors.actorOf(AccountService.class).start();

        account1.sendOneWay(new Deposit(1000));
        account2.sendOneWay(new Deposit(1000));

        Thread.sleep(1000);

        printBalance("Account1", account1);
        printBalance("Account2", account2);

        System.out.println("Let's transfer $20... should succeed");
        accountService.sendOneWay(new Transfer(account1, account2, 20));

        Thread.sleep(1000);

        printBalance("Account1", account1);
        printBalance("Account2", account2);

        System.out.println("Let's transfer $2000... should not succeed");
        accountService.sendOneWay(new Transfer(account1, account2, 2000));

        Thread.sleep(6000);

        printBalance("Account1", account1);
        printBalance("Account2", account2);

        Actors.registry().shutdownAll();

    }
}
