package chapter4;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Comparable<Account> {
    private int balance;
    public final Lock monitor = new ReentrantLock();

    public Account(final int initialBalance) {
        this.balance = initialBalance;
    }

    public int compareTo(final Account other) {
        return new Integer(hashCode()).compareTo(other.hashCode());
    }

    public void deposit(final int amount) {
        this.monitor.lock();
        try {
            if (amount > 0) this.balance += amount;
        } finally {
            this.monitor.unlock();
        }
    }

    public boolean withdraw(final int amount) {
        try {
            monitor.lock();
            if (amount > 0 && this.balance >= amount) {
                this.balance -= amount;
                return true;
            }
            return false;
        } finally {
            monitor.unlock();
        }
    }

    public void printBalance() {
        System.out.println("Balance is " + this.balance);
    }
}
