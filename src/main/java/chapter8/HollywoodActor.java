package chapter8;

import akka.actor.UntypedActor;

public class HollywoodActor extends UntypedActor {
    public void onReceive(final Object role) {
        System.out.println("Playing " + role + " from thread " + Thread.currentThread().getName());
    }
}
