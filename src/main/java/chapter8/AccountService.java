package chapter8;

import akka.transactor.SendTo;
import akka.transactor.UntypedTransactor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AccountService extends UntypedTransactor {

    @Override public Set<SendTo> coordinate(final Object message) {
        if (message instanceof Transfer) {
            Set<SendTo> coordinations = new HashSet<SendTo>();
            Transfer transfer = (Transfer) message;
            coordinations.add(sendTo(transfer.to, new Deposit(transfer.amount)));
            coordinations.add(sendTo(transfer.from, new Withdraw(transfer.amount)));
            return Collections.unmodifiableSet(coordinations);
        }

        return nobody();
    }

    public void atomically(final Object message) {}
}
