package chapter4;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AccountService {
    public static boolean transfer(final Account from,
                                   final Account to,
                                   final int amount)
            throws Exception {
        final Account[] accounts = new Account[]{from, to};
        Arrays.sort(accounts);

        if (accounts[0].monitor.tryLock(1, TimeUnit.SECONDS)) {
            try {
                if (accounts[1].monitor.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        if (from.withdraw(amount)) {
                            to.deposit(amount);
                            return true;
                        } else {
                            return false;
                        }
                    } finally {
                        accounts[1].monitor.unlock();
                    }
                }
            } finally {
                accounts[0].monitor.unlock();
            }
        }
        throw new Exception("Unable to acquire locks on the accounts");
    }

    public static void main(String[] args)
            throws Exception {
        Account from = new Account(1000);
        Account to = new Account(0);
        transfer(from, to, 100);
        from.printBalance();
        to.printBalance();
    }
}
