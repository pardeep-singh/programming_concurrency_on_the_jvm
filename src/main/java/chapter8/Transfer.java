package chapter8;

import akka.actor.ActorRef;

public class Transfer {

    public final ActorRef from;
    public final ActorRef to;
    public final int amount;

    public Transfer(final ActorRef from,
                    final ActorRef to,
                    final int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
