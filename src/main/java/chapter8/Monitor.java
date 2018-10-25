package chapter8;

import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Monitor extends UntypedActor {

    public void onReceive(Object message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        Actors.remote().start("localhost", 8000)
                .register("system-monitor", Actors.actorOf(Monitor.class));

        System.out.println("Press key to stop");
        System.console().readLine();
        Actors.registry().shutdownAll();
        Actors.remote().shutdown();
    }
}
