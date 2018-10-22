package chapter8;

import akka.actor.ActorRef;
import akka.actor.ActorTimeoutException;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class FortuneTeller extends UntypedActor {
    public void onReceive(final Object name) {
        if (getContext().replySafe(String.format("%s you'll rock", name)))
            System.out.println("Message sent for " + name);
        else
            System.out.println("Sender not found for " + name);
    }

    public static void main(String[] args) {
        final ActorRef fortuneTeller =
                Actors.actorOf(FortuneTeller.class).start();

        try {
            fortuneTeller.sendOneWay("Bill");
            final Object response = fortuneTeller.sendRequestReply("Joe");
            System.out.println(response);
        } catch (ActorTimeoutException ex) {
            System.out.println("Never got a response before timeout");
        } finally {
            fortuneTeller.stop();
        }
    }
}
