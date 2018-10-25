package chapter8;

import akka.transactor.annotation.Coordinated;

public interface CoordinatedAccount {
    int getBalance();
    @Coordinated void deposit(final int amount);
    @Coordinated void withdraw(final int amount);
}
