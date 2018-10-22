package chapter8;

import akka.actor.ActorRef;
import akka.actor.Actors;

public class UseHollywoodActor {
    public static void main(String[] args)
            throws InterruptedException {
        final ActorRef johnnyDepp = Actors.actorOf(HollywoodActor.class).start();
        johnnyDepp.sendOneWay("Jack Sparrow");
        Thread.sleep(100);
        johnnyDepp.sendOneWay("Edward Scissorhands");
        Thread.sleep(100);
        johnnyDepp.sendOneWay("Willy Wonka");
        Actors.registry().shutdownAll();
    }
}
